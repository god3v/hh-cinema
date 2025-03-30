package com.cinema.adapter.out.persistence;

import com.cinema.adapter.out.persistence.entity.ScreeningScheduleEntity;
import com.cinema.adapter.out.persistence.repository.ScreeningScheduleJpaRepository;
import com.cinema.application.port.out.ScreeningSchedulePort;
import com.cinema.common.exception.CoreException;
import com.cinema.common.exception.ErrorType;
import com.cinema.domain.model.ScreeningSchedule;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ScreeningScheduleAdapter implements ScreeningSchedulePort {

    private final ScreeningScheduleJpaRepository repository;

    @Override
    public ScreeningSchedule findById(Long scheduleId) {
        return repository.findById(scheduleId)
                .map(ScreeningScheduleEntity::of)
                .orElseThrow(() -> new CoreException(ErrorType.RESOURCE_NOT_FOUND, "검색한 상영 일정 ID: " + scheduleId));
    }
}
