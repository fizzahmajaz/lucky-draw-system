package com.fizzah.lucky_draw_system.dto.response;

import com.fizzah.lucky_draw_system.enums.PrizeType;
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
    private PrizeType prizeType;
    private Double prizeAmount;
    private String voucherCode; // if voucher-based
    private Date startDate;
    private Date endDate;
    private String description;
    private String department;
    private Integer timerSeconds;
    private DrawStatus status;
    private Integer maxWinners;
    private Long totalParticipants;
}
