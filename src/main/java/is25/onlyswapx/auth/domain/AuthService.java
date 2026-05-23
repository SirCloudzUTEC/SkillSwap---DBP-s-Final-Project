package is25.onlyswapx.auth.domain;

import is25.onlyswapx.auth.components.JwtService;
import is25.onlyswapx.auth.dto.SignInRequest;
import is25.onlyswapx.auth.dto.SignUpRequest;
import is25.onlyswapx.auth.dto.TokenResponse;
import is25.onlyswapx.user.domain.User;
import is25.onlyswapx.user.infraestructure.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @Transactional
    public TokenResponse signUp(SignUpRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already registered");
        }

        User user = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .fullName(request.getFullName())
                .university(request.getUniversity())
                .career(request.getCareer())
                .build();

        userRepository.save(user);

        String token = jwtService.generateToken(user.getEmail(), user.getRole());
        return buildResponse(user, token);
    }

    public TokenResponse signIn(SignInRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Invalid credentials"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid credentials");
        }

        String token = jwtService.generateToken(user.getEmail(), user.getRole());
        return buildResponse(user, token);
    }

    private TokenResponse buildResponse(User user, String token) {
        return TokenResponse.builder()
                .token(token)
                .email(user.getEmail())
                .fullName(user.getFullName())
                .role(user.getRole())
                .creditsBalance(user.getCreditsBalance())
                .build();
    }
}
