package is25.onlyswapx.exchange.domain;

import is25.onlyswapx.user.domain.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "exchange_requests")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class ExchangeRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "requester_id", nullable = false)
    private User requester;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receiver_id", nullable = false)
    private User receiver;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private ExchangeStatus status = ExchangeStatus.PENDING;

    private String message;

    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    public enum ExchangeStatus {
        PENDING, ACCEPTED, REJECTED, COMPLETED, CANCELLED
    }
}
