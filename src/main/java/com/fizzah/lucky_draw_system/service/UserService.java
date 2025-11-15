package com.fizzah.lucky_draw_system.service;

import com.fizzah.lucky_draw_system.dto.request.UserGuestRequest;
import com.fizzah.lucky_draw_system.dto.request.UserLoginRequest;
import com.fizzah.lucky_draw_system.dto.request.UserRegisterRequest;
import com.fizzah.lucky_draw_system.entity.User;

public interface UserService {

    User findById(Long id);

    User register(UserRegisterRequest req);

    User login(UserLoginRequest req);

    User findByEmail(String email);

    User findByUsername(String username);

    User createGuest(UserGuestRequest req);
}
