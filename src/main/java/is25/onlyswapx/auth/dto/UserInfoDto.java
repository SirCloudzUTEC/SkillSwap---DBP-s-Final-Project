package is25.onlyswapx.auth.dto;

import lombok.Builder;
import lombok.Data;

@Data @Builder
public class UserInfoDto {
    private Long id;
    private String email;
    private String role;
}