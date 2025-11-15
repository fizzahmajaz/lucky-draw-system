package com.fizzah.lucky_draw_system.service.impl;

import com.fizzah.lucky_draw_system.dto.request.AdminRegisterRequest;
import com.fizzah.lucky_draw_system.dto.request.AdminLoginRequest;
import com.fizzah.lucky_draw_system.dto.response.AdminResponse;
import com.fizzah.lucky_draw_system.entity.Admin;
import com.fizzah.lucky_draw_system.exception.NotFoundException;
import com.fizzah.lucky_draw_system.repository.AdminRepository;
import com.fizzah.lucky_draw_system.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {

    private final AdminRepository adminRepository;

    @Override
    public AdminResponse register(AdminRegisterRequest req) {

        Admin admin = new Admin();
        admin.setName(req.getName());
        admin.setEmail(req.getEmail());
        admin.setUsername(req.getUsername());
        admin.setPassword(req.getPassword());

        adminRepository.save(admin);

        return AdminResponse.builder()
                .id(admin.getId())
                .name(admin.getName())
                .email(admin.getEmail())
                .username(admin.getUsername())
                .build();
    }

    @Override
    public AdminResponse login(AdminLoginRequest req) {

        Admin admin = adminRepository.findByUsername(req.getUsername()
        )
                .orElseThrow(() -> new NotFoundException("Admin not found"));

        if (!admin.getPassword().equals(req.getPassword())) {
            throw new RuntimeException("Invalid credentials");
        }

        return AdminResponse.builder()
                .id(admin.getId())
                .name(admin.getName())
                .email(admin.getEmail())
                .username(admin.getUsername())
                .build();
    }
}
