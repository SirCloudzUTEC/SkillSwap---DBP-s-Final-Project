package is25.onlyswapx.session.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class SessionRequest {
    @NotNull
    private Long exchangeId;
    @NotNull
    private Long teacherId;
    @NotNull
    private Long studentId;
    @NotBlank
    private String topic;
    @NotNull @Future
    private LocalDateTime scheduledAt;
    @NotNull @Min(1)
    private Integer creditsAmount;
    private Integer durationMinutes = 60;
}