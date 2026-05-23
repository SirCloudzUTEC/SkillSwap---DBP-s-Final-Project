package com.onlyswapx.credit.domain;

import com.onlyswapx.session.domain.Session;
import com.onlyswapx.user.domain.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "credit_transactions")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class CreditTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "from_user_id")
    private User fromUser;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "to_user_id")
    private User toUser;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "session_id")
    private Session session;

    @Column(nullable = false)
    private Integer amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransactionType transactionType;

    private String description;

    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    public enum TransactionType {
        ESCROW_HOLD, ESCROW_RELEASE, ESCROW_REFUND, INITIAL_GRANT
    }
}