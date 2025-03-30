package com.cinema.application.event;

import com.cinema.application.dto.CreateReservationResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class MessageService {
    private final ApplicationEventPublisher applicationEventPublisher;

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void send(List<CreateReservationResult> reservations) {
        try {
            reservations.forEach(reservation -> log.info("send reservation message: {}", reservation));
            applicationEventPublisher.publishEvent(reservations);
        } catch (Exception e) {
            // 예외를 처리하고 롤백되지 않도록 한다
            log.error("Failed to publish event: ", e);
        }
    }
}
