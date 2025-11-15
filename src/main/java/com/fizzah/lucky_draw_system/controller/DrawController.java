package com.fizzah.lucky_draw_system.controller;

import com.fizzah.lucky_draw_system.dto.request.*;
import com.fizzah.lucky_draw_system.dto.response.*;
import com.fizzah.lucky_draw_system.entity.Draw;
import com.fizzah.lucky_draw_system.service.DrawService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/draws")
@RequiredArgsConstructor
public class DrawController {

    private final DrawService drawService;

    // Create draw (adminId as param for now)
    @PostMapping
    public ApiResponse<Draw> createDraw(@RequestBody CreateDrawRequest req,
                                        @RequestParam Long adminId) {
        Draw d = drawService.createDraw(req, adminId);
        return ApiResponse.success("Draw created", d);
    }

    // Update draw
    @PutMapping("/{id}")
    public ApiResponse<Draw> updateDraw(@PathVariable Long id,
                                        @RequestBody UpdateDrawRequest req,
                                        @RequestParam Long adminId) {
        Draw d = drawService.updateDraw(id, req, adminId);
        return ApiResponse.success("Draw updated", d);
    }

    // List draws (paged)
    @GetMapping
    public ApiResponse<Page<DrawResponse>> listDraws(@RequestParam(defaultValue = "0") int page,
                                                     @RequestParam(defaultValue = "10") int size,
                                                     @RequestParam(defaultValue = "id") String sort) {
        Pageable p = PageRequest.of(page, size, Sort.by(sort).descending());
        Page<DrawResponse> list = drawService.listDraws(p);
        return ApiResponse.success(list);
    }

    // Active draws
    @GetMapping("/active")
    public ApiResponse<Page<DrawResponse>> activeDraws(@RequestParam(defaultValue = "0") int page,
                                                       @RequestParam(defaultValue = "10") int size) {
        Pageable p = PageRequest.of(page, size, Sort.by("startDate").ascending());
        return ApiResponse.success(drawService.listActiveDraws(p));
    }

    // Draw details
    @GetMapping("/{id}")
    public ApiResponse<DrawResponse> drawDetails(@PathVariable Long id,
                                                 @RequestParam(required = false) Long userId) {
        return ApiResponse.success(drawService.getDrawDetails(id, userId));
    }

    // Join draw
    @PostMapping("/{id}/join")
    public ApiResponse<?> joinDraw(@PathVariable Long id, @RequestBody JoinDrawRequest req) {
        return drawService.joinDraw(id, req);
    }

    // Participants (admin)
    @GetMapping("/{id}/participants")
    public ApiResponse<Page<ParticipantResponse>> participants(@PathVariable Long id,
                                                               @RequestParam(defaultValue = "0") int page,
                                                               @RequestParam(defaultValue = "20") int size) {
        Pageable p = PageRequest.of(page, size, Sort.by("joinedAt").descending());
        return ApiResponse.success(drawService.listParticipants(id, p));
    }

    // Winners
    @GetMapping("/{id}/winners")
    public ApiResponse<Page<WinnerResponse>> winners(@PathVariable Long id,
                                                     @RequestParam(defaultValue = "0") int page,
                                                     @RequestParam(defaultValue = "20") int size) {
        Pageable p = PageRequest.of(page, size);
        return ApiResponse.success(drawService.listWinners(id, p));
    }
}
