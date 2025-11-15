package com.fizzah.lucky_draw_system.dto.response;

import lombok.*;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WinnerResponse {
    private Long id;
    private Long userId;
    private String name;
    private String email;
    private Double prizeAmount; // for cash
    private String voucherCode; // for voucher draws (only returned to admin or winner checks)
    private LocalDateTime announcedAt;
    private boolean redeemed;
}
