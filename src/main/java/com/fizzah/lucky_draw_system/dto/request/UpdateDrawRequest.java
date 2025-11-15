package com.fizzah.lucky_draw_system.dto.request;

import com.fizzah.lucky_draw_system.enums.PrizeType;
import lombok.Data;

import java.util.Date;

@Data
public class UpdateDrawRequest {
    private String name;
    private PrizeType prizeType;
    private Double prizeAmount;
    private String voucherCode;
    private Date startDate;
    private Date endDate;
    private String description;
    private String department;
    private Integer timerSeconds;
    private Integer maxWinners;
    private String status; // optional: DRAFT, ACTIVE, ENDED
}
