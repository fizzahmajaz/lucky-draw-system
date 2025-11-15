package com.fizzah.lucky_draw_system.dto.response;

import lombok.*;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WinnerResultResponse {
    private Long winnerHistoryId;
    private Long userId;
    private String userName;
    private String userEmail;
    private Double prizeAmount;   // for cash
    private String voucherCode;   // for voucher prize (admin-provided)
    private LocalDateTime announcedAt;
}
