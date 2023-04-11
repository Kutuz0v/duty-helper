package scpc.dutyhelper.auth.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.bytebuddy.utility.RandomString;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import scpc.dutyhelper.auth.model.User;
import scpc.dutyhelper.auth.model.UserDetailsImpl;
import scpc.dutyhelper.auth.model.role.ERole;
import scpc.dutyhelper.auth.model.role.Role;
import scpc.dutyhelper.auth.repository.RoleRepository;
import scpc.dutyhelper.auth.repository.UserRepository;
import scpc.dutyhelper.auth.service.UserService;
import scpc.dutyhelper.exception.BadRequestException;
import scpc.dutyhelper.exception.ConflictException;
import scpc.dutyhelper.exception.InternalError;
import scpc.dutyhelper.exception.NotFoundException;
import scpc.dutyhelper.telegram.service.TelegramService;

import java.util.List;
import java.util.Set;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository repository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder encoder;
    private final AuthenticationManager authenticationManager;
    private final TelegramService telegramService;
    @Value("${bot.username}")
    private String telegramBotName;

    @Override
    public List<User> getAll() {
        return repository.findAll();
    }

    @Override
    public User get(Long id) {
        return repository.findById(id).orElseThrow(
                () -> new NotFoundException(String.format("User with id %d not found", id)));
    }

    @Override
    public User create(User user) {
        repository.findByEmail(user.getEmail()).ifPresent((it) -> {
            throw new ConflictException("Email already exists");
        });
        Set<Role> userRoles = user.getRoles();
        Role userRole = roleRepository.findByName(ERole.USER).orElseThrow(
                () -> new InternalError("Role 'USER' not found"));
        User userForSave = User.builder()
                .id(0L)
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .password(encoder.encode(user.getPassword()))
                .roles(
                        userRoles == null || userRoles.size() == 0 ?
                                Set.of(userRole) :
                                userRoles
                )
                .enabled(user.getEnabled() != null && user.getEnabled())
                .confirmationCode(RandomString.make(64))
                .build();
        return repository.save(userForSave);
    }

    @Override
    public User update(Long id, User changes) {
        User user = get(id);

        if (user == null)
            throw new BadRequestException("User not found with passed ID!");

        if (changes.getFirstName() != null && !changes.getFirstName().isBlank())
            user.setFirstName(changes.getFirstName());

        if (changes.getLastName() != null && !changes.getLastName().isBlank())
            user.setLastName(changes.getLastName());

        Role userRole = roleRepository.findByName(ERole.USER).orElseThrow(
                () -> new InternalError("Role 'USER' not found"));

        if (changes.getRoles() != null && !changes.getRoles().isEmpty()) {
            changes.getRoles().add(userRole);
            user.setRoles(changes.getRoles());
        }

        if (changes.getEnabled() != null) {
            user.setEnabled(changes.getEnabled());
        }

        if (changes.getPassword() != null && changes.getNewPassword() != null) {
            boolean authenticated = authenticationManager.authenticate(
                            new UsernamePasswordAuthenticationToken(
                                    user.getEmail(),
                                    changes.getPassword()))
                    .isAuthenticated();

            if (authenticated) {
                user.setPassword(encoder.encode(changes.getNewPassword()));
            }
        }

        return repository.save(user);
    }

    @Override
    public void delete(Long id) {
        User user = get(id);
        log.info("Deletes {} by {}", user, getCurrentUser());
        repository.delete(user);
    }

    @Override
    public String generateTelegramConnectUrl(Long id) {
        String telegramCode = RandomString.make(28);
        User user = repository.findById(id)
                .orElseThrow(() -> new BadRequestException(String.format("User with id %d not found", id)));
        user.setConfirmationCode(telegramCode);
        repository.save(user);
        return String.format("https://t.me/%s?start=%s", telegramBotName, telegramCode);
    }

    @Override
    public User connectTelegram(Long chatId, String code) {
        User user = repository.findByConfirmationCode(code).orElse(null);

        if (user != null) {
            Long telegramChatId = user.getTelegramChatId();
            if (telegramChatId != null && telegramChatId > 0 && !telegramChatId.equals(chatId)) {
                telegramService.sendMessage(
                        telegramChatId,
                        "Зв'язка з цим акаунтом анулюється аби ви могли прив'язати інший акаунт Telegram.\n\r" +
                                "Якщо ви не робили запит на з'єднання з новим акаунтом, негайно змініть пароль до " +
                                "свого облікового запису на сервісі DutyHelper та зв'яжіть цей акаунт повторно!"
                );
            }
            user.setTelegramChatId(chatId);
            user.setConfirmationCode(null);
            return repository.save(user);
        } else return null;
    }

    private UserDetailsImpl getCurrentUser() {
        return (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }
}
