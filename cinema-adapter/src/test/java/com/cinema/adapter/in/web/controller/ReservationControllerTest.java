package com.cinema.adapter.in.web.controller;

import com.cinema.adapter.in.web.dto.request.CreateReservationRequest;
import com.cinema.domain.exception.CoreException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class ReservationControllerTest {

    private final int TOTAL_RESERVATION = 100;

    @Autowired
    private ReservationController sut;

    @Test
    public void 동시성_예약_테스트() throws InterruptedException {
        List<CreateReservationRequest> reservationRequests = new ArrayList<>();
        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger exceptionCount = new AtomicInteger(0);


        for (int i=0; i < TOTAL_RESERVATION; i++) {
            reservationRequests.add(
                    CreateReservationRequest.builder()
                            .scheduleId(1L)
                            .seatIds(List.of(1L, 2L, 3L, 4L, 5L))
                            .userId((long) i)
                            .build()
            );
        }

        int threadCount = reservationRequests.size();

        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);

        for (int i = 0; i < threadCount; i++) {
            final int taskId = i;

            executor.execute(() -> {
                try {
                    sut.createReservation(reservationRequests.get(taskId));
                    successCount.incrementAndGet();
                } catch (CoreException e) {
                    exceptionCount.incrementAndGet();
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await(); // 카운트 0까지 기다림
        executor.shutdown(); // pool 종료

        assertEquals(1, successCount.get());
        assertEquals(TOTAL_RESERVATION - 1, exceptionCount.get());
    }
}
