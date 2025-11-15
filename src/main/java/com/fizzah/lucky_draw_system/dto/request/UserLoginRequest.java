package com.fizzah.lucky_draw_system.dto.request;

import lombok.Data;

@Data
public class UserLoginRequest {
    private String usernameOrEmail;
    private String password;
}
