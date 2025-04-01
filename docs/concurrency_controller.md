# 동시성 제어 보고서
## 개요
> 동시성 제어는 다중 사용자 환경에서 데이터의 일관성과 무결성을 보장하기 위한 핵심 기법입니다.   
> 본 보고서에는 영화 예매 서비스에서 발생할 수 있는 동시성 문제를 해결하기 위해 낙관적 잠금, 비관적 잠금, 분산 잠금 전략과 각 동시성 테스트 결과를 정리했습니다.   
> 또한, Redisson을 활용한 분산락 설정 전략에 대해 논의합니다.   

### 동시성 테스트 방법
```java
@SpringBootTest
public class ReservationControllerTest {

    private final int TOTAL_RESERVATION = 100;
    ...

    @Test
    public void 동시성_예약_테스트() throws InterruptedException {
        List<CreateReservationRequest> reservationRequests = new ArrayList<>();
        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger exceptionCount = new AtomicInteger(0);


        for (int i = 0; i < TOTAL_RESERVATION; i++) {
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

        latch.await();
        executor.shutdown();

        assertEquals(1, successCount.get());
        assertEquals(TOTAL_RESERVATION - 1, exceptionCount.get());
    }
}
```
- 100명의 사용자가 동시에 같은 좌석을 예매하려고 시도합니다.
- 단 한 번만 예매에 성공했는지, 나머지 예매 시도들은 실패했는지 검증합니다.

---

## 낙관적 잠금(Optimistic Locking)
> 낙관적 잠금은 **트랜잭션이 충돌할 가능성이 낮다**고 가정하고, **데이터 갱신 시점에 충돌을 감지**하여 해결하는 방식입니다.   
> 보통 `version` 또는 `timestamp`를 이용하여, 데이터 변경 시 기존 값과 비교 후 충돌 여부를 판단합니다.   

### 적용 방법
- `JPA`에서는 엔티티에 `@Version` 필드를 추가하여 적용할 수 있습니다.
- 엔티티가 변경될 때 기존 버전과 비교하여 충돌이 발생하면 예외가 발생합니다.
- 충돌이 발생할 경우 `OptimisticLockException` 예외가 발생하고, 재시도 로직이 필요합니다.

### 적용 가능성 검토
- 본 영화 예매 서비스에서는 예약(Reservation) 또는 좌석(Seat) 엔티티에 대한 상태 변경이 없으며, update 쿼리가 발생하지 않습니다.
- 따라서, 예매 시 단순히 새로운 `Reservation` 데이터를 생성하는 구조이므로, 낙관적 잠금이 불필요 합니다.

---

## 비관적 잠금(Pessimistic Locking)
> 비관적 잠금은 데이터에 대한 충돌 가능성이 높다고 가정하고, 트랜잭션이 데이터를 수정하기 전에 선제적으로 락을 걸어 충돌을 방지하는 방식입니다.   
> 조회 단계에서 `for update` 또는 `lock`을 활용해 데이터 잠금을 수행합니다.

### 적용 방법
```java
    public List<ScreenSeat> findSeatsNotReservedByScheduleIdAndSeatIds(Long scheduleId, List<Long> seatIds) {
        QScreenSeatEntity seat = QScreenSeatEntity.screenSeatEntity;
        QTicketReservationEntity reservation = QTicketReservationEntity.ticketReservationEntity;

        return queryFactory
                .select(Projections.constructor(
                        ScreenSeat.class,
                        seat.id,
                        seat.screen.id,
                        seat.row,
                        seat.col))
                .from(seat)
                .leftJoin(reservation)
                .on(reservation.screenSeat.eq(seat)
                        .and(reservation.screeningSchedule.id.eq(scheduleId)))
                .where(seat.id.in(seatIds)
                        .and(reservation.id.isNull()))
                .setLockMode(LockModeType.PESSIMISTIC_WRITE) // 비관적 잠금 적용
                .fetch();
    }
```
- 예약하려는 좌석이 이미 예약되어 있는지 조회하는 메서드입니다.
- 좌석 조회 시 `LockModeType.PESSIMISTIC_WRITE` 가 설정되어 `for update` 가 붙은 select 쿼리를 실행합니다.

### 실제 실행 쿼리
```text
[Hibernate] 
    select
        sse1_0.screen_seat_id,
        sse1_0.screen_id,
        sse1_0.seat_row,
        sse1_0.seat_col 
    from
        screen_seat sse1_0 
    left join
        ticket_reservation tre1_0 
            on tre1_0.screen_seat_id=sse1_0.screen_seat_id 
            and tre1_0.screening_schedule_id=? 
    where
        sse1_0.screen_seat_id in (?, ?, ?, ?, ?) 
        and tre1_0.ticket_reservation_id is null for update
```

### 동시성 테스트 결과
```text
🔹 실행 시간(ms) : 4092
✅ 예약 성공 횟수 : 1
❌ 예약 실패 횟수 : 99
```

---

## AOP 분산 잠금 (AOP Distributed Locking)
> AOP(Aspect-Oriented Programming)를 활용하면, 비즈니스 로직과 락 처리 로직을 분리하여 코드의 가독성을 높일 수 있습니다.

### 적용 방법
- 어노테이션 정의
```java
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface DistributedLockAOP {

    String key();

    TimeUnit timeUnit() default TimeUnit.SECONDS;

    long waitTime() default 3L;

    long leaseTime() default 3L;
}
```

- AOP 적용
```java
@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class DistributedLockAspect {

    private static final String REDISSON_LOCK_PREFIX = "LOCK:";

    private final RedissonClient redissonClient;
    private final AopForTransaction aopForTransaction;

    @Around("@annotation(distributedLock)")
    public Object lock(final ProceedingJoinPoint joinPoint, DistributedLockAOP distributedLockAOP) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();

        String key = REDISSON_LOCK_PREFIX + distributedLockAOP.key();
        RLock rLock = redissonClient.getLock(key);

        try {
            boolean available = rLock.tryLock(distributedLockAOP.waitTime(), distributedLockAOP.leaseTime(), distributedLockAOP.timeUnit());
            if (!available) {
                return false;
            }
            return aopForTransaction.proceed(joinPoint);
        } catch (InterruptedException e) {
            throw new InterruptedException();
        } finally {
            try {
                rLock.unlock();
            } catch (IllegalMonitorStateException e) {
                log.info("Redisson Lock Already UnLock serviceName: {}, key: {}", method.getName(), key);
            }
        }
    }
}
```

- 적용 로직
```java
    @Override
    @DistributedLockAOP(key = "#command.scheduleId")
    public List<CreateReservationResult> createReservation(CreateReservationCommand command) {
        TicketReservation reservation = ReservationMapper.toDomain(command);
        reservationValidator.validate(reservation);
    
        List<Long> reservedIds = reservationPort.saveReservations(reservation);
    
        List<CreateReservationResult> reservations = reservationPort.findReservations(reservedIds);
    
        eventPublisher.publishEvent(new ReservedEvent(reservations));
    
        return reservations;
    }
```

### 동시성 테스트 결과
```text
🔹 실행 시간(ms) : 1530
✅ 예약 성공 횟수 : 1
❌ 예약 실패 횟수 : 99
```

## 함수형 분산 잠금 (Functional Distributed Locking)
> 함수형 분산 잠금은 락 획득과 비즈니스 로직 실행을 하나의 함수형 인터페이스로 캡슐화하는 방식입니다.   
> 기존 AOP 기반 분산락은 메서드 단위로 적용되므로, 유연성이 떨어지지만 함수형 분산락은 특정 로직 내부에서도 자유롭게 적용이 가능하다는 장점이 있습니다.   

### 적용 방법
- 함수형 분산 잠금 인터페이스
```java
package com.cinema.application.port.out;

import java.util.function.Supplier;

public interface DistributedLock {
    public <T> T executeWithLock(String key, long waitTime, long leaseTime, Supplier<T> task);
}
```

- 함수형 분산 잠금 구현체
```java
package com.cinema.adapter.out.persistence.lock;

@Component
@RequiredArgsConstructor
public class DistributedLockUtil implements DistributedLock {
    private static final String REDISSON_LOCK_PREFIX = "LOCK:";

    private final RedissonClient redissonClient;
    @Override
    public <T> T executeWithLock(String key, long waitTime, long leaseTime, Supplier<T> task) {
        RLock lock = redissonClient.getLock(REDISSON_LOCK_PREFIX + key);

        try {
            if (!lock.tryLock(waitTime, leaseTime, TimeUnit.SECONDS)) {
                throw new CoreException(ErrorType.LOCK_ACQUISITION_FAILED, "Lock key: " + lock.getName());
            }
            return task.get();

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("락 획득 중 오류 발생", e);
        } finally {
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }
}
```

- 적용 로직
```java
    @Override
    public List<CreateReservationResult> createReservation(CreateReservationCommand command) {
        TicketReservation reservation = ReservationMapper.toDomain(command);

        String lockKey = String.valueOf(command.scheduleId());
        return lock.executeWithLock(lockKey, 3, 3, () -> {
            reservationValidator.validate(reservation);

            List<Long> reservedIds = reservationPort.saveReservations(reservation);

            List<CreateReservationResult> reservations = reservationPort.findReservations(reservedIds);

            eventPublisher.publishEvent(new ReservedEvent(reservations));

            return reservations;
        });
    }
```

### 동시성 테스트 결과
```text
🔹 실행 시간(ms) : 864
✅ 예약 성공 횟수 : 1
❌ 예약 실패 횟수 : 99
```

## Redisson 분산락 사용 시 waitTime, leaseTime 설정에 대하여
- 평균 작업 시간이 1초 이내로, 락을 유지하는 최대 시간인 `leaseTime`을 3초로 설정했습니다.
- 예매 요청이 몰릴 때, 대기 시간이 길어지면 전체 시스템 부하가 커질 것을 우려하여 `waitTime`을 3초로 설정했습니다.