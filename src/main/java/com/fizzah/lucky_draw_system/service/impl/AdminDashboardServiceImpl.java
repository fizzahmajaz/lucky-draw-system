package com.fizzah.lucky_draw_system.service.impl;

import com.fizzah.lucky_draw_system.dto.response.DashboardResponse;
import com.fizzah.lucky_draw_system.repository.*;
import com.fizzah.lucky_draw_system.service.AdminDashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminDashboardServiceImpl implements AdminDashboardService {

    private final DrawRepository drawRepository;
    private final ParticipantDrawRepository participantDrawRepository;
    private final VoucherRepository voucherRepository;
    private final WinnerHistoryRepository winnerHistoryRepository;

    @Override
    public DashboardResponse getDashboard() {
        long totalDraws = drawRepository.count();
        long totalActive = drawRepository.findByStatus(com.fizzah.lucky_draw_system.enums.DrawStatus.ACTIVE, org.springframework.data.domain.Pageable.unpaged()).getTotalElements();
        long totalParticipants = participantDrawRepository.count();
        long totalVouchers = voucherRepository.count();

        // Most winning participant
        Object top = winnerHistoryRepository.findTopWinnerNative(); // object[] expected from native query
        Long topUserId = null;
        String topUserName = null;
        long topWins = 0;
        if (top != null) {
            if (top instanceof Object[]) {
                Object[] arr = (Object[]) top;
                topUserId = arr[0] == null ? null : ((Number)arr[0]).longValue();
                topWins = arr[1] == null ? 0 : ((Number)arr[1]).longValue();
            } else {
                // fallback
            }
        }

        // Most popular draw (by participants)
        List<Object[]> drawCounts = participantDrawRepository.getDrawCounts(); // create this custom query below
        Long popularDrawId = null;
        String popularDrawName = null;
        long popularCount = 0;
        if (drawCounts != null && !drawCounts.isEmpty()) {
            Object[] first = drawCounts.get(0);
            popularDrawId = ((Number)first[0]).longValue();
            popularCount = ((Number)first[1]).longValue();
            // get name
            popularDrawName = drawRepository.findById(popularDrawId).map(d -> d.getName()).orElse(null);
        }

        DashboardResponse resp = DashboardResponse.builder()
                .totalDraws(totalDraws)
                .totalActiveDraws(totalActive)
                .totalParticipants(totalParticipants)
                .totalVouchers(totalVouchers)
                .mostWinningUserId(topUserId)
                .mostWinningUserName(topUserName)
                .mostWinningCount(topWins)
                .mostPopularDrawId(popularDrawId)
                .mostPopularDrawName(popularDrawName)
                .mostPopularDrawParticipants(popularCount)
                .build();
        return resp;
    }
}
