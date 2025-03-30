package com.cinema.domain.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum MovieRating {

    G("전체 관람가"),
    PG("12세 이상 관람가"),
    PG13("15세 이상 관람가"),
    R("18세 이상 관람가");

    private final String description;
}
