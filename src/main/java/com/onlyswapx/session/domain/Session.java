package com.onlyswapx.session.domain;

import com.onlyswapx.exchange.domain.ExchangeRequest;
import com.onlyswapx.user.domain.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "sessions")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class Session {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "exchange_id", nullable = false)
    private ExchangeRequest exchange;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "teacher_id", nullable = false)
    private User teacher;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private User student;

    @Column(nullable = false)
    private String topic;

    @Column(nullable = false)
    private LocalDateTime scheduledAt;

    @Builder.Default
    private Integer durationMinutes = 60;

    @Column(nullable = false)
    private Integer creditsAmount;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private SessionStatus status = SessionStatus.SCHEDULED;

    @Builder.Default
    private Boolean teacherConfirmed = false;

    @Builder.Default
    private Boolean studentConfirmed = false;

    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    public enum SessionStatus {
        SCHEDULED, TEACHER_CONFIRMED, STUDENT_CONFIRMED, COMPLETED, CANCELLED
    }
}