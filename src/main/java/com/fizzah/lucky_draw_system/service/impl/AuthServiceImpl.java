package com.fizzah.lucky_draw_system.service.impl;

import com.fizzah.lucky_draw_system.dto.request.GuestRegisterRequest;
import com.fizzah.lucky_draw_system.dto.request.UserLoginRequest;
import com.fizzah.lucky_draw_system.dto.request.UserRegisterRequest;
import com.fizzah.lucky_draw_system.dto.response.UserResponse;
import com.fizzah.lucky_draw_system.entity.User;
import com.fizzah.lucky_draw_system.entity.Voucher;
import com.fizzah.lucky_draw_system.enums.AccessLevel;
import com.fizzah.lucky_draw_system.enums.VoucherType;
import com.fizzah.lucky_draw_system.exception.BadRequestException;
import com.fizzah.lucky_draw_system.exception.NotFoundException;
import com.fizzah.lucky_draw_system.repository.UserRepository;
import com.fizzah.lucky_draw_system.repository.VoucherRepository;
import com.fizzah.lucky_draw_system.service.AuthService;
import com.fizzah.lucky_draw_system.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final VoucherRepository voucherRepository;
    private final EmailService emailService;

    // ---------------------------------------------
    // USER REGISTER
    // ---------------------------------------------
    @Override
    public UserResponse register(UserRegisterRequest req) {

        if (!req.getPassword().equals(req.getConfirmPassword())) {
            throw new BadRequestException("Passwords do not match");
        }

        if (userRepository.findByEmail(req.getEmail()).isPresent()) {
            throw new BadRequestException("Email already registered");
        }

        if (userRepository.findByUsername(req.getUsername()).isPresent()) {
            throw new BadRequestException("Username already taken");
        }

        // Voucher validation
        if (req.getVoucherCode() != null && !req.getVoucherCode().isEmpty()) {
            Voucher voucher = voucherRepository.findByCode(req.getVoucherCode())
                    .orElseThrow(() -> new NotFoundException("Invalid voucher code"));

            if (voucher.getType() != VoucherType.ACCESS) {
                throw new BadRequestException("This is not an access voucher");
            }

            if (voucher.getAccessLevel() == AccessLevel.SPECIFIC &&
                    voucher.getDepartmentRestriction() != null &&
                    !voucher.getDepartmentRestriction().contains(req.getDepartment())) {
                throw new BadRequestException("This voucher is restricted for other departments");
            }
        }

        User user = User.builder()
                .name(req.getName())
                .email(req.getEmail())
                .username(req.getUsername())
                .password(req.getPassword())
                .phone(req.getPhone())
                .department(req.getDepartment())
                .externalId(req.getExternalId())
                .isGuest(false)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        userRepository.save(user);

        emailService.sendRegistrationEmail(user);

        return UserResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .username(user.getUsername())
                .phone(user.getPhone())
                .department(user.getDepartment())
                .guest(user.isGuest())
                .build();
    }

    // ---------------------------------------------
    // USER LOGIN
    // ---------------------------------------------
    @Override
    public UserResponse login(UserLoginRequest req) {

        Optional<User> userOpt;

        if (req.getUsernameOrEmail().contains("@")) {
            userOpt = userRepository.findByEmail(req.getUsernameOrEmail());
        } else {
            userOpt = userRepository.findByUsername(req.getUsernameOrEmail());
        }

        User user = userOpt.orElseThrow(() -> new NotFoundException("User not found"));

        if (!user.getPassword().equals(req.getPassword())) {
            throw new BadRequestException("Invalid credentials");
        }

        return UserResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .username(user.getUsername())
                .phone(user.getPhone())
                .department(user.getDepartment())
                .guest(user.isGuest())
                .build();
    }

    // ---------------------------------------------
    // GUEST REGISTER
    // ---------------------------------------------
    @Override
    public UserResponse guestRegister(GuestRegisterRequest req) {

        // Voucher required
        Voucher voucher = voucherRepository.findByCode(req.getVoucherCode())
                .orElseThrow(() -> new NotFoundException("Invalid guest voucher"));

        if (voucher.getType() != VoucherType.ACCESS ||
            voucher.getAccessLevel() != AccessLevel.GUEST) {
            throw new BadRequestException("This voucher does not allow guest login");
        }

        User guest = User.builder()
                .name(req.getName())
                .email(req.getEmail())
                .username("guest_" + UUID.randomUUID().toString().substring(0, 8))
                .phone(req.getPhone())
                .department(req.getDepartment())
                .password(null) // guests have no password
                .isGuest(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        userRepository.save(guest);

        emailService.sendRegistrationEmail(guest);

        return UserResponse.builder()
                .id(guest.getId())
                .name(guest.getName())
                .email(guest.getEmail())
                .username(guest.getUsername())
                .phone(guest.getPhone())
                .department(guest.getDepartment())
                .guest(true)
                .build();
    }
}
