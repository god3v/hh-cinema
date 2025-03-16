package com.cinema.adapter.out.persistence.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity(name = "screen")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ScreenEntity extends BaseEntity {

    @Id
    @Column(name = "screen_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 상영관 ID

    @Column(nullable = false)
    private String screenName; // 상영관 이름
}
