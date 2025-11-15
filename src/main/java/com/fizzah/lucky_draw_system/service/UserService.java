package com.fizzah.lucky_draw_system.service;

import com.fizzah.lucky_draw_system.entity.User;

public interface UserService {

    User findById(Long id);

    User findByEmail(String email);

    User findByUsername(String username);
}
