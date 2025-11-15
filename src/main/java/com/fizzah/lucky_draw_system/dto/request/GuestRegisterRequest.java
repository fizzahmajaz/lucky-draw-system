package com.fizzah.lucky_draw_system.dto.request;

import lombok.Data;

@Data
public class GuestRegisterRequest {
    private String name;
    private String email;
    private String phone;
    private String department;
    private String voucherCode; // required for GUEST access
}
