package com.fizzah.lucky_draw_system.controller;

import com.fizzah.lucky_draw_system.dto.request.AdminRegisterRequest;
import com.fizzah.lucky_draw_system.dto.request.AdminLoginRequest;
import com.fizzah.lucky_draw_system.dto.response.ApiResponse;
import com.fizzah.lucky_draw_system.dto.response.AdminResponse;
import com.fizzah.lucky_draw_system.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admins")
@RequiredArgsConstructor
public class AdminAuthController {

    private final AdminService adminService;

    @PostMapping("/register")
    public ApiResponse<AdminResponse> register(@RequestBody AdminRegisterRequest request) {
        AdminResponse response = adminService.register(request);
        return ApiResponse.success("Admin Registered", response);
    }

    @PostMapping("/login")
    public ApiResponse<AdminResponse> login(@RequestBody AdminLoginRequest request) {
        AdminResponse response = adminService.login(request);
        return ApiResponse.success("Login Successful", response);
    }
}
