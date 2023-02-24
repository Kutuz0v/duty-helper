package scpc.dutyhelper.auth.service.impl.auth;

import lombok.RequiredArgsConstructor;
import net.bytebuddy.utility.RandomString;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import scpc.dutyhelper.auth.model.UserDetailsImpl;
import scpc.dutyhelper.auth.model.role.ERole;
import scpc.dutyhelper.auth.model.User;
import scpc.dutyhelper.auth.payload.request.SignInRequest;
import scpc.dutyhelper.auth.payload.request.SignUpRequest;
import scpc.dutyhelper.auth.payload.response.JwtResponse;
import scpc.dutyhelper.auth.repository.RoleRepository;
import scpc.dutyhelper.auth.repository.UserRepository;
import scpc.dutyhelper.auth.service.EmailService;
import scpc.dutyhelper.auth.service.AuthService;
import scpc.dutyhelper.config.security.jwt.JwtUtils;
import scpc.dutyhelper.exception.BadRequestException;
import scpc.dutyhelper.exception.ConflictException;

import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository repository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder encoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;

    private final EmailService emailService;

    @Override
    public User signUp(SignUpRequest signUpRequest, String baseUrl) {
        String userEmail = signUpRequest.getEmail();
        if (!userEmail.endsWith("@scpc.gov.ua"))
            throw new BadRequestException("Email host is not acceptable!");

        if (repository.existsByEmail(userEmail)) {
            throw new ConflictException("Email is already taken!");
        }

        User user = saveUserOnSignUp(signUpRequest);

        emailService.sendSignUpConfirmationEmail(user.getEmail(), baseUrl, user.getConfirmationCode());

        return user;
    }

    @Override
    public JwtResponse signIn(SignInRequest signinRequest) {
        authenticate(signinRequest);

        UserDetailsImpl userDetails =
                (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        return generateJwtResponse(userDetails);
    }

    @Override
    public void confirmEmail(String confirmationCode) {
        User user = repository.findByConfirmationCode(confirmationCode).orElse(null);
        if (user == null || user.getEnabled()) {
            throw new BadRequestException("Invalid confirmation code!");
        } else {
            user.setConfirmationCode(null);
            user.setEnabled(true);
            repository.save(user);
        }
    }

    @Override
    public void initializeResetPassword(String email, String baseUrl) {
        User user = repository.findByEmail(email).orElse(null);
        if (user == null)
            throw new BadRequestException(String.format("User with email %s not found!", email));
        if (!user.getEnabled())
            throw new ConflictException(String.format("User with email %s disabled!", email));

        String randomConfirmationCode = RandomString.make(64);
        user.setConfirmationCode(randomConfirmationCode);
        repository.save(user);

        emailService.sendResetPasswordConfirmation(user.getEmail(), baseUrl, user.getConfirmationCode());
    }

    @Override
    public void resetPassword(String confirmationCode, String password) {
        User user = repository.findByConfirmationCode(confirmationCode).orElse(null);
        if (user == null)
            throw new BadRequestException("Invalid confirmation code!");

        user.setConfirmationCode(null);
        user.setPassword(encoder.encode(password));
        repository.save(user);
    }

    private User saveUserOnSignUp(SignUpRequest signUpRequest) {
        String randomConfirmationCode = RandomString.make(64);

        return repository.save(
                User.builder()
                        .firstName(signUpRequest.getFirstName())
                        .lastName(signUpRequest.getLastName())
                        .email(signUpRequest.getEmail())
                        .password(encoder.encode(signUpRequest.getPassword()))
                        .roles(Set.of(roleRepository.findByName(ERole.USER).get()))
                        .enabled(false)
                        .confirmationCode(randomConfirmationCode)
                        .build()
        );
    }

    private void authenticate(SignInRequest signinRequest) {
        SecurityContextHolder.getContext().setAuthentication(
                authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(
                                signinRequest.getEmail(), signinRequest.getPassword())
                )
        );
    }

    private JwtResponse generateJwtResponse(UserDetailsImpl userDetails) {
        String jwt = jwtUtils.generateJwtToken(userDetails.getEmail());

        List<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority).toList();

        return JwtResponse.builder()
                .accessToken(jwt)
                .id(userDetails.getId())
                .firstName(userDetails.getFirstName())
                .lastName(userDetails.getLastName())
                .email(userDetails.getEmail())
                .roles(roles)
                .build();
    }
}
