package com.cinema.adapter.out.persistence.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity(name = "ticket_reservation")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TicketReservationEntity extends BaseEntity {

    @Id
    @Column(name = "ticket_reservation_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 영화 예매 ID

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "screening_schedule_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private ScreeningScheduleEntity screeningSchedule; // 상영 일정 ID

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "screen_seat_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private ScreenSeatEntity screenSeat; // 상영관 좌석 ID

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private UserEntity user; // 회원 ID
}
