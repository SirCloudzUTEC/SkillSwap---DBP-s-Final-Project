package is25.onlyswapx.auth.application;

import is25.onlyswapx.auth.domain.AuthService;
import is25.onlyswapx.auth.dto.SignInRequest;
import is25.onlyswapx.auth.dto.SignUpRequest;
import is25.onlyswapx.auth.dto.TokenResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/sign-up")
    public ResponseEntity<TokenResponse> signUp(
            @Valid @RequestBody SignUpRequest request) {
        return ResponseEntity.ok(authService.signUp(request));
    }

    @PostMapping("/sign-in")
    public ResponseEntity<TokenResponse> signIn(
            @Valid @RequestBody SignInRequest request) {
        return ResponseEntity.ok(authService.signIn(request));
    }
}
