package com.fizzah.lucky_draw_system.service.impl;

import com.fizzah.lucky_draw_system.dto.request.UserGuestRequest;
import com.fizzah.lucky_draw_system.dto.request.UserLoginRequest;
import com.fizzah.lucky_draw_system.dto.request.UserRegisterRequest;
import com.fizzah.lucky_draw_system.entity.User;
import com.fizzah.lucky_draw_system.exception.BadRequestException;
import com.fizzah.lucky_draw_system.exception.NotFoundException;
import com.fizzah.lucky_draw_system.repository.UserRepository;
import com.fizzah.lucky_draw_system.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public User findById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("User not found"));
    }

    @Override
    public User register(UserRegisterRequest req) {

        if (!req.getPassword().equals(req.getConfirmPassword())) {
            throw new BadRequestException("Passwords do not match");
        }

        if (userRepository.findByEmail(req.getEmail()).isPresent()) {
            throw new BadRequestException("Email already exists");
        }

        if (userRepository.findByUsername(req.getUsername()).isPresent()) {
            throw new BadRequestException("Username already exists");
        }

        User user = User.builder()
                .name(req.getName())
                .email(req.getEmail())
                .username(req.getUsername())
                .phone(req.getPhone())
                .department(req.getDepartment())
                .externalId(req.getExternalId())
                .password(req.getPassword())
                .isGuest(false)
                .createdAt(LocalDateTime.now())
                .build();

        return userRepository.save(user);
    }

    @Override
    public User login(UserLoginRequest req) {

        User user = userRepository.findByUsername(req.getUsernameOrEmail())
                .orElseGet(() ->
                        userRepository.findByEmail(req.getUsernameOrEmail())
                                .orElseThrow(() -> new NotFoundException("User not found"))
                );

        if (!user.getPassword().equals(req.getPassword())) {
            throw new BadRequestException("Invalid password");
        }

        return user;
    }

    @Override
    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("User not found with email: " + email));
    }

    @Override
    public User findByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new NotFoundException("User not found with username: " + username));
    }

    @Override
public User createGuest(UserGuestRequest req) {

    User guest = User.builder()
            .name(req.getName())
            .email(req.getEmail())
            .phone(req.getPhone())
            .department(req.getDepartment())
            .username(null)
            .password(null)
            .isGuest(true)
            .createdAt(LocalDateTime.now())
            .build();

    return userRepository.save(guest);
}

@Override
public List<User> findAllUsers() {
    return userRepository.findAll();
}

}
