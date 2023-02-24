package scpc.dutyhelper.auth.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.bytebuddy.utility.RandomString;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import scpc.dutyhelper.auth.model.role.ERole;
import scpc.dutyhelper.auth.model.role.Role;
import scpc.dutyhelper.auth.model.User;
import scpc.dutyhelper.auth.repository.RoleRepository;
import scpc.dutyhelper.auth.repository.UserRepository;
import scpc.dutyhelper.auth.service.UserService;
import scpc.dutyhelper.auth.model.UserDetailsImpl;
import scpc.dutyhelper.exception.*;

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
        return repository.save(User.builder()
                .id(0L)
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .password(encoder.encode(user.getPassword()))
                .roles(
                        userRoles == null || userRoles.size() == 0 ?
                                Set.of(roleRepository.findByName(ERole.USER).get()) :
                                userRoles
                        )
                .enabled(user.getEnabled() != null && user.getEnabled())
                .confirmationCode(RandomString.make(64))
                .build());
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
    public User updateRoles(Long id, Set<Role> roles) {
        User user = get(id);
        user.setRoles(roles);
        log.info("{} updates roles for {} to {}", getCurrentUser(), user, roles);
        return repository.save(user);
    }

    @Override
    public void delete(Long id) {
        User user = get(id);
        log.info("Deletes {} by {}", user, getCurrentUser());
        repository.delete(user);
    }

    private UserDetailsImpl getCurrentUser() {
        return (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }
}
