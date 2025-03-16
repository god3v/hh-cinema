package com.cinema.adapter.out.persistence.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity(name = "screen_seat")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ScreenSeatEntity extends BaseEntity {

    @Id
    @Column(name = "screen_seat_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 상영관 좌석 ID

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "screen_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private ScreenEntity screen; // 상영관 ID

    @Column(name = "seat_row", nullable = false)
    private int row; // 좌석 위치 행

    @Column(name = "seat_col", nullable = false)
    private int col; // 좌석 위치 열
}
