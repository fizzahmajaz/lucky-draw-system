package com.fizzah.lucky_draw_system.repository;

import com.fizzah.lucky_draw_system.entity.WinnerHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface WinnerHistoryRepository extends JpaRepository<WinnerHistory, Long> {
    List<WinnerHistory> findByDrawId(Long drawId);
    List<WinnerHistory> findByUserId(Long userId);

    // For dashboard: find most winning participant(s)
    @Query(value = "SELECT user_id, COUNT(*) as wins FROM winner_history GROUP BY user_id ORDER BY wins DESC LIMIT 1", nativeQuery = true)
    Object findTopWinnerNative(); // returns Object[] or a single record mapping (handle in service)
}
