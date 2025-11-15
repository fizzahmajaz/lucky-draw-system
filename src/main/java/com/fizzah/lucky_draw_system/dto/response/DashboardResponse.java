package com.fizzah.lucky_draw_system.dto.response;

import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DashboardResponse {
    private long totalDraws;
    private long totalActiveDraws;
    private long totalParticipants;
    private long totalVouchers;
    private Long mostWinningUserId;
    private String mostWinningUserName;
    private long mostWinningCount;
    private Long mostPopularDrawId;
    private String mostPopularDrawName;
    private long mostPopularDrawParticipants;
}
