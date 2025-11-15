package com.fizzah.lucky_draw_system.service;

import com.fizzah.lucky_draw_system.dto.request.*;
import com.fizzah.lucky_draw_system.dto.response.UserResponse;

public interface AuthService {

    UserResponse register(UserRegisterRequest request);

    UserResponse login(UserLoginRequest request);

    UserResponse guestRegister(GuestRegisterRequest request);
}
