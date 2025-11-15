package com.fizzah.lucky_draw_system.dto.request;

import lombok.Data;

@Data
public class AdminLoginRequest {
    private String username;
    private String password;
}
