package com.onlyswapx.credit.domain;

import com.onlyswapx.credit.dto.CreditBalanceResponse;
import com.onlyswapx.credit.infrastructure.CreditTransactionRepository;
import com.onlyswapx.session.domain.Session;
import com.onlyswapx.session.domain.SessionCompletedEvent;
import com.onlyswapx.session.infrastructure.SessionRepository;
import com.onlyswapx.user.domain.User;
import com.onlyswapx.user.infrastructure.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class CreditService {

    private final UserRepository userRepository;
    private final SessionRepository sessionRepository;
    private final CreditTransactionRepository transactionRepository;
    private final ApplicationEventPublisher eventPublisher;

    public CreditBalanceResponse getBalance(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return CreditBalanceResponse.builder()
                .userId(user.getId())
                .balance(user.getCreditsBalance())
                .build();
    }

    @Transactional
    public void holdEscrow(Long sessionId) {
        Session session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new RuntimeException("Session not found"));
        User student = session.getStudent();

        if (student.getCreditsBalance() < session.getCreditsAmount()) {
            throw new RuntimeException("Insufficient credits");
        }

        student.setCreditsBalance(student.getCreditsBalance() - session.getCreditsAmount());
        userRepository.save(student);

        transactionRepository.save(CreditTransaction.builder()
                .fromUser(student)
                .session(session)
                .amount(session.getCreditsAmount())
                .transactionType(CreditTransaction.TransactionType.ESCROW_HOLD)
                .description("Hold for session: " + session.getTopic())
                .build());

        log.info("ESCROW_HOLD: {} credits from student {}", session.getCreditsAmount(), student.getId());
    }

    @Transactional
    public void confirmSession(Long sessionId, String userEmail) {
        Session session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new RuntimeException("Session not found"));
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        boolean isTeacher = session.getTeacher().getId().equals(user.getId());
        boolean isStudent = session.getStudent().getId().equals(user.getId());

        if (!isTeacher && !isStudent) throw new RuntimeException("Not a participant");

        if (isTeacher) session.setTeacherConfirmed(true);
        if (isStudent) session.setStudentConfirmed(true);

        sessionRepository.save(session);

        if (session.getTeacherConfirmed() && session.getStudentConfirmed()) {
            releaseEscrow(session);
        }
    }

    @Transactional
    public void refundEscrow(Long sessionId) {
        Session session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new RuntimeException("Session not found"));

        boolean holdExists = transactionRepository.findBySessionId(sessionId)
                .stream().anyMatch(t -> t.getTransactionType()
                        == CreditTransaction.TransactionType.ESCROW_HOLD);

        if (!holdExists) return;

        User student = session.getStudent();
        student.setCreditsBalance(student.getCreditsBalance() + session.getCreditsAmount());
        userRepository.save(student);

        transactionRepository.save(CreditTransaction.builder()
                .toUser(student)
                .session(session)
                .amount(session.getCreditsAmount())
                .transactionType(CreditTransaction.TransactionType.ESCROW_REFUND)
                .description("Refund for cancelled session: " + session.getTopic())
                .build());

        log.info("ESCROW_REFUND: {} credits returned to student {}", session.getCreditsAmount(), student.getId());
    }

    private void releaseEscrow(Session session) {
        session.setStatus(Session.SessionStatus.COMPLETED);
        sessionRepository.save(session);

        User teacher = session.getTeacher();
        teacher.setCreditsBalance(teacher.getCreditsBalance() + session.getCreditsAmount());
        userRepository.save(teacher);

        transactionRepository.save(CreditTransaction.builder()
                .toUser(teacher)
                .session(session)
                .amount(session.getCreditsAmount())
                .transactionType(CreditTransaction.TransactionType.ESCROW_RELEASE)
                .description("Payment for: " + session.getTopic())
                .build());

        eventPublisher.publishEvent(new SessionCompletedEvent(this, session));
        log.info("ESCROW_RELEASE: {} credits to teacher {}", session.getCreditsAmount(), teacher.getId());
    }
}