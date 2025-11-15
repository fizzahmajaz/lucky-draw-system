package com.fizzah.lucky_draw_system.dto.request;

import lombok.Data;

@Data
public class AdminRegisterRequest {
    private String name;
    private String email;
    private String username;
    private String password;
}
