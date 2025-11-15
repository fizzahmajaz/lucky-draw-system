package com.fizzah.lucky_draw_system.repository;

import com.fizzah.lucky_draw_system.entity.EmailLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmailLogRepository extends JpaRepository<EmailLog, Long> {
    // simple CRUD is enough; extend if you want search by status/date
}
