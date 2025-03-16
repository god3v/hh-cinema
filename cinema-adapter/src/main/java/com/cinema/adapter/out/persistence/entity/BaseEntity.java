package com.cinema.adapter.out.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Getter
@DynamicInsert
@DynamicUpdate
@MappedSuperclass
public class BaseEntity {

    @Column(updatable = false)
    private Long createdBy; // 작성자 ID

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt; // 작성 일시

    @Column(insertable = false)
    private Long updatedBy; // 수정자 ID

    @UpdateTimestamp
    @Column(insertable = false)
    private LocalDateTime updatedAt; // 수정 일시
}
