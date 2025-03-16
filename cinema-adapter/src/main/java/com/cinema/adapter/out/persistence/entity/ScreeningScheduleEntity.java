package com.cinema.adapter.out.persistence.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity(name = "screening_schedule")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ScreeningScheduleEntity extends BaseEntity {

    @Id
    @Column(name = "screening_schedule_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 상영 일정 ID

    @Column(nullable = false)
    private LocalDateTime startAt; // 상영 시작 시간

    @Column(nullable = false)
    private LocalDateTime endAt; // 상영 종료 시간

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "movie_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private MovieEntity movie; // 영화 ID

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "screen_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private ScreenEntity screen; // 상영관 ID
}
