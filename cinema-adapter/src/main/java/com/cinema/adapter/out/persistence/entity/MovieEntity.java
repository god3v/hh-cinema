package com.cinema.adapter.out.persistence.entity;

import com.cinema.domain.enums.MovieGenre;
import com.cinema.domain.enums.MovieRating;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(
        name = "movie",
        indexes = {
                @Index(name = "idx_movie_release_date", columnList = "release_date"),
                @Index(name = "idx_movie_genre", columnList = "genre")
        }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MovieEntity extends BaseEntity {

    @Id
    @Column(name = "movie_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 영화 ID

    @Column(nullable = false)
    private String title; // 영화 제목

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private MovieRating movieRating; // 영상물 등급

    @Column(nullable = false)
    private LocalDate releaseDate; // 개봉일

    private String thumbnailUrl; // 썸네일 이미지 URL

    @Column(nullable = false)
    private int runningTime; // 러닝 타임(분)

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private MovieGenre genre; // 영화 장르
}
