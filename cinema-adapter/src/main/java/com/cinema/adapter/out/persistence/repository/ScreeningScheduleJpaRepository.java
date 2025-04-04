package com.cinema.adapter.out.persistence.repository;

import com.cinema.adapter.out.persistence.entity.ScreeningScheduleEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ScreeningScheduleJpaRepository extends JpaRepository<ScreeningScheduleEntity, Long> {
}
