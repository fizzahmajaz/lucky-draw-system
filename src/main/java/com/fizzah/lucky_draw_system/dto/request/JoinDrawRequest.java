package com.fizzah.lucky_draw_system.dto.request;

import lombok.Data;

@Data
public class JoinDrawRequest {
    private Long userId;         // required if registered user
    private String voucherCode;  // optional (access voucher or prize voucher check)
    private String accountDetailsJson; // optional JSON string for cash payout account
}
