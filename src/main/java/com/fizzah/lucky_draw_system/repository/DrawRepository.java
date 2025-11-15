package com.fizzah.lucky_draw_system.repository;

import com.fizzah.lucky_draw_system.entity.Draw;
import com.fizzah.lucky_draw_system.enums.DrawStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Date;
import java.util.List;

public interface DrawRepository extends JpaRepository<Draw, Long>, JpaSpecificationExecutor<Draw> {
    Page<Draw> findByStatus(DrawStatus status, Pageable pageable);

    // active draws: where status = ACTIVE and current date in start-end optionally
    List<Draw> findByStatusAndStartDateBeforeAndEndDateAfter(DrawStatus status, Date now1, Date now2);

    // small helpers
    Page<Draw> findByCreatedByAdminId(Long adminId, Pageable pageable);
}
