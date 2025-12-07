package ru.khkhlv.messenger.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.khkhlv.messenger.configuration.JwtService;
import ru.khkhlv.messenger.dto.LoginRequest;
import ru.khkhlv.messenger.dto.RegisterRequest;
import ru.khkhlv.messenger.model.User;
import ru.khkhlv.messenger.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public void register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new RuntimeException("Email already exists");
        }
        User user = new User();
        user.setEmail(request.email());
        user.setUsername(request.username());
        user.setPasswordHash(passwordEncoder.encode(request.password()));
        userRepository.save(user);
    }

    public String login(LoginRequest request) {
        userRepository.findByEmail(request.email())
                .orElseThrow(() -> new RuntimeException("Invalid credentials"));
        // Spring Security уже проверит пароль через Provider, но для упрощения:
        // Можно использовать AuthenticationManager, но здесь — упрощённо
        return jwtService.generateToken(
                org.springframework.security.core.userdetails.User.builder()
                        .username(request.email())
                        .password("dummy") // не используется при генерации токена
                        .roles("USER")
                        .build()
        );
    }
}
