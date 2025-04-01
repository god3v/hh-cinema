package com.cinema.application.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class ReservedEvent {

    private List<CreateReservationResult> reservations;
}
