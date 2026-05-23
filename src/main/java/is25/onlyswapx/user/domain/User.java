package is25.onlyswapx.user.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String fullName;

    private String university;
    private String career;

    @Builder.Default
    private Integer creditsBalance = 10;

    @Builder.Default
    private String role = "USER";

    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();
}