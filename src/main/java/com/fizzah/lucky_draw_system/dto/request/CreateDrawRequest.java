package com.fizzah.lucky_draw_system.dto.request;

import lombok.Data;
import java.util.Date;

@Data
public class CreateDrawRequest {

    private String name;               // Draw Name
    private String prize;              // "Win iPhone", "Win 2 Lakh"
    private String voucherCode;        // Optional (string)
    private String voucherAccessLevel; // ALL / RESTRICTED

    private Date startDate;
    private Date endDate;

    private String description;
    private String department;

    private Integer timerSeconds;
    private Integer maxWinners;
}
