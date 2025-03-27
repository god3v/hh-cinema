package com.cinema.application.port.in;

import com.cinema.application.dto.CreateReservationCommand;
import com.cinema.application.dto.CreateReservationResult;

import java.util.List;

public interface CreateReservationUseCase {

    List<CreateReservationResult> createReservation(CreateReservationCommand command);
}
