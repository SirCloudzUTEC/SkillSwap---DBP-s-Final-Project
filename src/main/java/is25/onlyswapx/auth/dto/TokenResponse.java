package is25.onlyswapx.auth.dto;

import lombok.Builder;
import lombok.Data;

@Data @Builder
public class TokenResponse {
    private String token;
    private String email;
    private String fullName;
    private String role;
    private Integer creditsBalance;
}