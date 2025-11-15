package com.fizzah.lucky_draw_system.service;

import com.fizzah.lucky_draw_system.dto.request.AdminRegisterRequest;
import com.fizzah.lucky_draw_system.dto.request.AdminLoginRequest;
import com.fizzah.lucky_draw_system.dto.response.AdminResponse;

public interface AdminService {

    AdminResponse register(AdminRegisterRequest req);

    AdminResponse login(AdminLoginRequest req);
}
