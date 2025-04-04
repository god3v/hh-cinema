package com.cinema.application.service;

import com.cinema.application.dto.ReservedEvent;
import com.cinema.domain.exception.CoreException;
import com.cinema.domain.exception.ErrorType;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class ReservationEventListener {
    private final MessageService messageService;

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void reserved(ReservedEvent event) {
        try {
            messageService.send("Reserved:" + event);
        } catch (InterruptedException e) {
            throw new CoreException(ErrorType.EVENT_PUBLISH_FAILED, "이벤트: " + event);
        }
    }
}
