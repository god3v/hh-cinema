package com.cinema.adapter.in.web.dto.request;

import com.cinema.adapter.in.web.validation.EnumValue;
import com.cinema.domain.enums.MovieGenre;
import jakarta.validation.constraints.Size;

public record NowPlayingMovieRequest(
        @Size(max = 30, message = "{validation.title.size}")
        String title,
        @EnumValue(enumClass = MovieGenre.class, message = "{validation.genre.invalid}")
        String genre
) {
}
