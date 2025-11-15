package com.fizzah.lucky_draw_system.dto.request;

import lombok.Data;

@Data
public class UserRegisterRequest {
    private String name;
    private String email;
    private String username;
    private String phone;
    private String department;
    private String externalId;
    private String password;
    private String confirmPassword;
    private String voucherCode; // optional access voucher
}
