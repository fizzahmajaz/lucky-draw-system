package com.fizzah.lucky_draw_system.dto.request;

import lombok.Data;
import java.util.Date;

@Data
public class UpdateDrawRequest {

    private String name;

    private String prize;              // Updated prize text
    private String voucherCode;        // Optional
    private String voucherAccessLevel; // ALL / RESTRICTED

    private Date startDate;
    private Date endDate;

    private String description;
    private String department;

    private Integer timerSeconds;
    private Integer maxWinners;

    private String status; // optional: DRAFT, ACTIVE, ENDED
}
