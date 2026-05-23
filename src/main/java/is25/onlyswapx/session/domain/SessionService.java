package is25.onlyswapx.session.domain;

import is25.onlyswapx.credit.domain.CreditService;
import is25.onlyswapx.exchange.domain.ExchangeRequest;
import is25.onlyswapx.exchange.infrastructure.ExchangeRepository;
import is25.onlyswapx.session.dto.SessionRequest;
import is25.onlyswapx.session.dto.SessionResponse;
import is25.onlyswapx.session.infrastructure.SessionRepository;
import is25.onlyswapx.user.domain.User;
import is25.onlyswapx.user.infrastructure.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SessionService {

    private final SessionRepository sessionRepository;
    private final ExchangeRepository exchangeRepository;
    private final UserRepository userRepository;
    private final CreditService creditService;

    @Transactional
    public SessionResponse create(SessionRequest request) {
        ExchangeRequest exchange = exchangeRepository.findById(request.getExchangeId())
                .orElseThrow(() -> new RuntimeException("Exchange not found"));
        if (exchange.getStatus() != ExchangeRequest.ExchangeStatus.ACCEPTED) {
            throw new RuntimeException("Exchange must be ACCEPTED");
        }

        User teacher = userRepository.findById(request.getTeacherId())
                .orElseThrow(() -> new RuntimeException("Teacher not found"));
        User student = userRepository.findById(request.getStudentId())
                .orElseThrow(() -> new RuntimeException("Student not found"));

        Session session = Session.builder()
                .exchange(exchange)
                .teacher(teacher)
                .student(student)
                .topic(request.getTopic())
                .scheduledAt(request.getScheduledAt())
                .durationMinutes(request.getDurationMinutes())
                .creditsAmount(request.getCreditsAmount())
                .build();

        Session saved = sessionRepository.save(session);
        creditService.holdEscrow(saved.getId());
        return toResponse(saved);
    }

    @Transactional
    public SessionResponse cancel(Long sessionId, String userEmail) {
        Session session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new RuntimeException("Session not found"));
        session.setStatus(Session.SessionStatus.CANCELLED);
        sessionRepository.save(session);
        creditService.refundEscrow(sessionId);
        return toResponse(session);
    }

    public List<SessionResponse> getMySessions(String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));
        List<Session> sessions = sessionRepository.findByTeacherId(user.getId());
        sessions.addAll(sessionRepository.findByStudentId(user.getId()));
        return sessions.stream().map(this::toResponse).toList();
    }

    private SessionResponse toResponse(Session s) {
        return SessionResponse.builder()
                .id(s.getId())
                .exchangeId(s.getExchange().getId())
                .teacherId(s.getTeacher().getId())
                .teacherName(s.getTeacher().getFullName())
                .studentId(s.getStudent().getId())
                .studentName(s.getStudent().getFullName())
                .topic(s.getTopic())
                .scheduledAt(s.getScheduledAt())
                .durationMinutes(s.getDurationMinutes())
                .creditsAmount(s.getCreditsAmount())
                .status(s.getStatus())
                .teacherConfirmed(s.getTeacherConfirmed())
                .studentConfirmed(s.getStudentConfirmed())
                .build();
    }
}