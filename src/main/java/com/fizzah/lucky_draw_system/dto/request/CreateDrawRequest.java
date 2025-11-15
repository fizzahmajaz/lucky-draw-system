package com.fizzah.lucky_draw_system.dto.request;

import com.fizzah.lucky_draw_system.enums.PrizeType;
import lombok.Data;

import java.util.Date;

@Data
public class CreateDrawRequest {
    private String name;
    private PrizeType prizeType; // CASH or VOUCHER
    private Double prizeAmount;  // if CASH
    private String voucherCode;  // code of voucher if VOUCHER
    private Date startDate;
    private Date endDate;
    private String description;
    private String department;
    private Integer timerSeconds;
    private Integer maxWinners;
}
