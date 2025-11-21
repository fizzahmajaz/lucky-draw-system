package com.fizzah.lucky_draw_system.dto.response;

import com.fizzah.lucky_draw_system.enums.DrawStatus;
import lombok.*;

import java.util.Date;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DrawResponse {
    private Long id;
    private String name;
    private String prize;              // ✅ string prize
    private String voucherCode;        // ✅ string voucher
    private String voucherAccessLevel; // ✅ ALL / RESTRICTED
    private Date startDate;
    private Date endDate;
    private String description;
    private String department;
    private Integer timerSeconds;
    private DrawStatus status;
    private Integer maxWinners;
    private Long totalParticipants;
}
