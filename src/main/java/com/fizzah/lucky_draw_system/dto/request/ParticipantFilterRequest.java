package com.fizzah.lucky_draw_system.dto.request;

import lombok.Data;

@Data
public class ParticipantFilterRequest {

    // TEXT SEARCH
    private String search; // name, email, phone, username, voucher

    // FILTERS
    private String voucherCode;
    private String drawName;
    private String prizeType; // CASH or VOUCHER
    private String department;
    private Boolean winner; // true = winners, false = losers

    // SORTING OPTIONS
    private String sortBy; // name, wins, losses, dateJoined
    private String sortDirection = "asc"; // asc or desc

    // DATE RANGE FILTER
    private String joinedAfter; // yyyy-mm-dd
    private String joinedBefore;

    // PAGINATION
    private Integer page = 0;
    private Integer size = 20;
}
