package com.fizzah.lucky_draw_system.repository;

import com.fizzah.lucky_draw_system.entity.ParticipantDraw;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ParticipantDrawRepository extends JpaRepository<ParticipantDraw, Long>, JpaSpecificationExecutor<ParticipantDraw> {

    Optional<ParticipantDraw> findByUserIdAndDrawId(Long userId, Long drawId);

    List<ParticipantDraw> findByDrawId(Long drawId);

    Page<ParticipantDraw> findByDrawId(Long drawId, Pageable pageable);

    long countByDrawId(Long drawId);

    Page<ParticipantDraw> findByUserId(Long userId, Pageable pageable);

    List<ParticipantDraw> findByUserIdAndIsWinnerTrue(Long userId);

    @Query("SELECT pd.user.id FROM ParticipantDraw pd WHERE pd.draw.id = :drawId")
    List<Long> findUserIdsByDrawId(Long drawId);

    @Query("SELECT pd FROM ParticipantDraw pd WHERE pd.isWinner = true AND pd.draw.id = ?1")
    List<ParticipantDraw> findWinnersByDrawId(Long drawId);

        @Query(value = "SELECT pd.draw.id as drawId, COUNT(pd.id) as cnt FROM ParticipantDraw pd GROUP BY pd.draw.id ORDER BY cnt DESC")
    List<Object[]> getDrawCounts();

}
