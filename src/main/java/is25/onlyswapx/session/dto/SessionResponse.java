package is25.onlyswapx.session.dto;

import is25.onlyswapx.session.domain.Session;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data @Builder
public class SessionResponse {
    private Long id;
    private Long exchangeId;
    private Long teacherId;
    private String teacherName;
    private Long studentId;
    private String studentName;
    private String topic;
    private LocalDateTime scheduledAt;
    private Integer durationMinutes;
    private Integer creditsAmount;
    private Session.SessionStatus status;
    private Boolean teacherConfirmed;
    private Boolean studentConfirmed;
}