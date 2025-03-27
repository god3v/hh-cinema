package com.cinema.adapter.out.persistence.repository;

import com.cinema.adapter.out.persistence.entity.TicketReservationEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TicketReservationJpaRepository extends JpaRepository<TicketReservationEntity, Long> {

}
