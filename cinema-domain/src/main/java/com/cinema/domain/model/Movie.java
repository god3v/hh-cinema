package com.cinema.domain.model;

import com.cinema.domain.enums.MovieGenre;
import com.cinema.domain.enums.MovieRating;

import java.time.LocalDate;

public record Movie(
        Long id, // 영화 ID
        String title, // 영화 제목
        MovieRating movieRating, // 영상물 등급
        LocalDate releaseDate, // 개봉일
        String thumbnailUrl, // 썸네일 이미지 URL
        int  runningTime, // 러닝 타임(분)
        MovieGenre genre // 영화 장르
) {
}
