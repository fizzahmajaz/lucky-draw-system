package com.fizzah.lucky_draw_system.controller;

import com.fizzah.lucky_draw_system.dto.response.ApiResponse;
import com.fizzah.lucky_draw_system.dto.response.UserResponse;
import com.fizzah.lucky_draw_system.entity.User;
import com.fizzah.lucky_draw_system.service.ExportService;
import com.fizzah.lucky_draw_system.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {

    private final UserService userService;
    private final ExportService exportService;

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
}
