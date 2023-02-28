package scpc.dutyhelper.auth.service.impl.auth;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import scpc.dutyhelper.auth.model.User;
import scpc.dutyhelper.auth.model.UserDetailsImpl;
import scpc.dutyhelper.auth.repository.UserRepository;
import scpc.dutyhelper.exception.UnauthorizedException;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {
    private final UserRepository userRepository;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        try {
            return loadUserByEmail(username);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    private UserDetails loadUserByEmail(String email) {
        User client = userRepository.findByEmail(email)
                .orElseThrow(() -> new UnauthorizedException("User not found with email: " + email));
        return UserDetailsImpl.build(client);
    }
}
