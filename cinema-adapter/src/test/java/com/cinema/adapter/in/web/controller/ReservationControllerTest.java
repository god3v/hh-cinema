package com.cinema.adapter.in.web.controller;

import com.cinema.adapter.in.web.dto.request.ReservationRequest;
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
        List<ReservationRequest> reservationRequests = new ArrayList<>();
        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger exceptionCount = new AtomicInteger(0);


        for (int i=0; i < TOTAL_RESERVATION; i++) {
            reservationRequests.add(
                    ReservationRequest.builder()
                            .scheduleId(1L)
                            .seatIds(List.of(1L, 2L, 3L, 4L, 5L))
                            .userId((long) i)
                            .build()
            );
        }

        int threadCount = reservationRequests.size();

        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);

        long startTime = System.currentTimeMillis(); // 시작 시간 측정

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

        long endTime = System.currentTimeMillis(); // 종료 시간 측정
        long elapsedTime = endTime - startTime;

        System.out.println("🔹 실행 시간(ms) : " + elapsedTime);
        System.out.println("✅ 예약 성공 횟수 : " + successCount.get());
        System.out.println("❌ 예약 실패 횟수 : " + exceptionCount.get());

        assertEquals(1, successCount.get());
        assertEquals(TOTAL_RESERVATION - 1, exceptionCount.get());
    }
}
