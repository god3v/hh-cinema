package com.cinema.adapter.in.web.controller;

import com.cinema.adapter.in.web.dto.mapper.ReservationMapper;
import com.cinema.adapter.in.web.dto.request.ReservationRequest;
import com.cinema.adapter.in.web.dto.response.ReservationResponse;
import com.cinema.application.dto.CreateReservationResult;
import com.cinema.application.port.in.CreateReservationUseCase;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/reservations")
@RequiredArgsConstructor
public class ReservationController {

    private final CreateReservationUseCase createReservationUseCase;

    /**
     * 영화 예매 API
     */
    @PostMapping
    public ResponseEntity<List<ReservationResponse>> createReservation(@Valid @RequestBody ReservationRequest request) {
        List<CreateReservationResult> reservation = createReservationUseCase.createReservation(ReservationMapper.toCommand(request));
        return ResponseEntity.ok(reservation.stream().map(ReservationMapper::toResponse).toList());
    }
}
