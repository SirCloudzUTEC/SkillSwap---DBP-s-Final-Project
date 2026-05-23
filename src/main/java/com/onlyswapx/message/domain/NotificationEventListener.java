package com.onlyswapx.message.domain;

import com.onlyswapx.session.domain.SessionCompletedEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@Slf4j
public class NotificationEventListener {

    @Async("taskExecutor")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onSessionCompleted(SessionCompletedEvent event) {
        var session = event.getSession();
        log.info("[ASYNC EVENT] Session '{}' completed. Teacher: {} received {} credits.",
                session.getTopic(),
                session.getTeacher().getFullName(),
                session.getCreditsAmount());
        // Aquí iría integración con Resend para emails transaccionales
    }
}
