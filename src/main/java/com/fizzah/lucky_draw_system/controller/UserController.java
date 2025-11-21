package com.fizzah.lucky_draw_system.controller;

import com.fizzah.lucky_draw_system.dto.request.UserGuestRequest;
import com.fizzah.lucky_draw_system.dto.request.UserLoginRequest;
import com.fizzah.lucky_draw_system.dto.request.UserRegisterRequest;
import com.fizzah.lucky_draw_system.dto.response.ApiResponse;
import com.fizzah.lucky_draw_system.dto.response.UserResponse;
import com.fizzah.lucky_draw_system.entity.User;
import com.fizzah.lucky_draw_system.service.ExportService;
import com.fizzah.lucky_draw_system.service.UserService;
import lombok.RequiredArgsConstructor;

import java.util.List;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {

    private final UserService userService;
    private final ExportService exportService;

    // -------------------------------
    // USER REGISTER
    // -------------------------------
    @PostMapping("/register")
    public ApiResponse<UserResponse> register(@RequestBody UserRegisterRequest req) {
        User user = userService.register(req);

        UserResponse resp = UserResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .username(user.getUsername())
                .phone(user.getPhone())
                .department(user.getDepartment())
                .guest(user.isGuest())
                .build();

        return ApiResponse.success("User registered", resp);
    }

    // -------------------------------
    // USER LOGIN
    // -------------------------------
    @PostMapping("/login")
    public ApiResponse<UserResponse> login(@RequestBody UserLoginRequest req) {
        User user = userService.login(req);

        UserResponse resp = UserResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .username(user.getUsername())
                .phone(user.getPhone())
                .department(user.getDepartment())
                .guest(user.isGuest())
                .build();

        return ApiResponse.success("Login successful", resp);
    }

    // -------------------------------
    // USER PROFILE
    // -------------------------------
    @GetMapping("/{id}/profile")
    public ApiResponse<UserResponse> profile(@PathVariable Long id) {

        User u = userService.findById(id);

        UserResponse resp = UserResponse.builder()
                .id(u.getId())
                .name(u.getName())
                .email(u.getEmail())
                .username(u.getUsername())
                .phone(u.getPhone())
                .department(u.getDepartment())
                .guest(u.isGuest())
                .build();

        return ApiResponse.success(resp);
    }

    // -------------------------------
    // EXPORT USER HISTORY
    // -------------------------------
    @GetMapping("/{id}/draws/history/export")
    public ResponseEntity<ByteArrayResource> exportUserHistory(
            @PathVariable Long id,
            @RequestParam(defaultValue = "csv") String format) {

        byte[] data;
        String filename;

        if (format.equalsIgnoreCase("pdf")) {
            data = exportService.exportUserHistoryPdf(id);
            filename = "user_" + id + "_history.pdf";
        } else {
            data = exportService.exportUserHistoryCsv(id);
            filename = "user_" + id + "_history.csv";
        }

        ByteArrayResource resource = new ByteArrayResource(data);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename)
                .contentType(format.equalsIgnoreCase("pdf") ? MediaType.APPLICATION_PDF : MediaType.TEXT_PLAIN)
                .body(resource);
    }

    // -------------------------------
// GET ALL USERS
// -------------------------------
@GetMapping("/all")
public ApiResponse<?> getAllUsers() {

    List<User> list = userService.findAllUsers();

    List<UserResponse> resp = list.stream().map(u ->
            UserResponse.builder()
                    .id(u.getId())
                    .name(u.getName())
                    .email(u.getEmail())
                    .username(u.getUsername())
                    .phone(u.getPhone())
                    .department(u.getDepartment())
                    .guest(u.isGuest())
                    .build()
    ).toList();

    return ApiResponse.success(resp);
}


    @PostMapping("/guest")
public ApiResponse<UserResponse> guestAccess(@RequestBody UserGuestRequest req) {

    User guest = userService.createGuest(req);

    UserResponse resp = UserResponse.builder()
            .id(guest.getId())
            .name(guest.getName())
            .email(guest.getEmail())
            .username(null)
            .phone(guest.getPhone())
            .department(guest.getDepartment())
            .guest(true)
            .build();

    return ApiResponse.success("Guest access granted", resp);
}

}
