package com.cinema.domain.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum MovieGenre {

    ACTION("액션"),
    COMEDY("코미디"),
    DRAMA("드라마"),
    HORROR("공포"),
    ROMANCE("로맨스");

    private final String description;

    public static MovieGenre fromDescription(String description) {
        for (MovieGenre genre : MovieGenre.values()) {
            if (genre.getDescription().equalsIgnoreCase(description)) {
                return genre;
            }
        }
        throw new IllegalArgumentException("Unknown genre: " + description);
    }
}
