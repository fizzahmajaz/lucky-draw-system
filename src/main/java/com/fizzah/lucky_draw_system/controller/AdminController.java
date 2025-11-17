package com.fizzah.lucky_draw_system.controller;

import com.fizzah.lucky_draw_system.dto.response.DashboardResponse;
import com.fizzah.lucky_draw_system.dto.response.ParticipantResponse;
import com.fizzah.lucky_draw_system.dto.request.ExecuteAlgorithmRequest;
import com.fizzah.lucky_draw_system.dto.request.ParticipantFilterRequest;
import com.fizzah.lucky_draw_system.dto.response.ApiResponse;
import com.fizzah.lucky_draw_system.dto.response.WinnerResultResponse;
import com.fizzah.lucky_draw_system.entity.ParticipantDraw;
import com.fizzah.lucky_draw_system.entity.User;
import com.fizzah.lucky_draw_system.entity.WinnerHistory;
import com.fizzah.lucky_draw_system.repository.ParticipantDrawRepository;
import com.fizzah.lucky_draw_system.service.AdminDashboardService;
import com.fizzah.lucky_draw_system.service.AdminParticipantService;
import com.fizzah.lucky_draw_system.service.AlgorithmService;
import com.fizzah.lucky_draw_system.service.ExportService;
import org.springframework.http.HttpHeaders;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/admins")
@RequiredArgsConstructor
public class AdminController {

    private final AlgorithmService algorithmService;
    private final AdminDashboardService adminDashboardService;
    private final ExportService exportService;
    private final ParticipantDrawRepository participantDrawRepository;
    private final AdminParticipantService adminParticipantService;

    /**
     * Execute draw algorithm (admin)
     * POST /admins/draws/{id}/execute?adminId=1
     * body: { "numberOfWinners": 3 }
     */
    @PostMapping("/draws/{id}/execute")
    public ApiResponse<List<WinnerResultResponse>> executeDraw(@PathVariable("id") Long drawId,
            @RequestBody ExecuteAlgorithmRequest req,
            @RequestParam Long adminId) {

        List<WinnerHistory> winners = algorithmService.executeAlgorithm(drawId, req.getNumberOfWinners(), adminId);

        List<WinnerResultResponse> response = winners.stream().map(wh -> WinnerResultResponse.builder()
                .winnerHistoryId(wh.getId())
                .userId(wh.getUser().getId())
                .userName(wh.getUser().getName())
                .userEmail(wh.getUser().getEmail())
                .prizeAmount(wh.getPrizeAmount())
                .voucherCode(wh.getVoucher() != null ? wh.getVoucher().getCode() : null)
                .announcedAt(wh.getAnnouncedAt())
                .build()).collect(Collectors.toList());

        return ApiResponse.success("Winners selected", response);
    }

    @GetMapping("/dashboard")
    public ApiResponse<DashboardResponse> dashboard() {
        return ApiResponse.success(adminDashboardService.getDashboard());
    }

    // Export all participants
    @GetMapping("/participants/export")
    public ResponseEntity<ByteArrayResource> exportAllParticipants(@RequestParam(defaultValue = "csv") String format) {
        byte[] data;
        String filename;
        if ("pdf".equalsIgnoreCase(format)) {
            data = exportService.exportAllParticipantsPdf();
            filename = "participants_all.pdf";
        } else {
            data = exportService.exportAllParticipantsCsv();
            filename = "participants_all.csv";
        }
        ByteArrayResource resource = new ByteArrayResource(data);
        MediaType mediaType = "pdf".equalsIgnoreCase(format) ? MediaType.APPLICATION_PDF : MediaType.TEXT_PLAIN;
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .contentType(mediaType)
                .contentLength(data.length)
                .body(resource);
    }

    // Export draw participants
    @GetMapping("/draws/{id}/participants/export")
    public ResponseEntity<ByteArrayResource> exportDrawParticipants(@PathVariable Long id,
            @RequestParam(defaultValue = "csv") String format) {
        byte[] data;
        String filename;
        if ("pdf".equalsIgnoreCase(format)) {
            data = exportService.exportParticipantsPdf(id);
            filename = "draw_" + id + "_participants.pdf";
        } else {
            data = exportService.exportParticipantsCsv(id);
            filename = "draw_" + id + "_participants.csv";
        }
        ByteArrayResource resource = new ByteArrayResource(data);
        MediaType mediaType = "pdf".equalsIgnoreCase(format) ? MediaType.APPLICATION_PDF : MediaType.TEXT_PLAIN;
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .contentType(mediaType)
                .contentLength(data.length)
                .body(resource);
    }

    // Export draw winners
    @GetMapping("/draws/{id}/winners/export")
    public ResponseEntity<ByteArrayResource> exportDrawWinners(@PathVariable Long id,
            @RequestParam(defaultValue = "csv") String format) {
        byte[] data;
        String filename;
        if ("pdf".equalsIgnoreCase(format)) {
            data = exportService.exportWinnersPdf(id);
            filename = "draw_" + id + "_winners.pdf";
        } else {
            data = exportService.exportWinnersCsv(id);
            filename = "draw_" + id + "_winners.csv";
        }
        ByteArrayResource resource = new ByteArrayResource(data);
        MediaType mediaType = "pdf".equalsIgnoreCase(format) ? MediaType.APPLICATION_PDF : MediaType.TEXT_PLAIN;
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .contentType(mediaType)
                .contentLength(data.length)
                .body(resource);
    }

    // Participant detail modal
    @GetMapping("/participants/{id}")
    public ApiResponse<ParticipantResponse> participantDetail(@PathVariable Long id) {
        ParticipantResponse resp = participantDrawRepository.findById(id).map(pd -> {
            User u = pd.getUser();
            ParticipantResponse r = ParticipantResponse.builder()
                    .id(pd.getId())
                    .userId(u.getId())
                    .name(u.getName())
                    .email(u.getEmail())
                    .phone(u.getPhone())
                    .department(u.getDepartment())
                    .externalId(u.getExternalId())
                    .voucherUsed(pd.getVoucherUsed() != null ? pd.getVoucherUsed().getCode() : null)
                    .joinedAt(pd.getJoinedAt())
                    .winner(pd.isWinner())
                    .redeemed(pd.isRedeemed())
                    .build();
            return r;
        }).orElseThrow(() -> new com.fizzah.lucky_draw_system.exception.NotFoundException("Participant not found"));
        return ApiResponse.success(resp);
    }

    @PostMapping("/participants/filter")
    public ApiResponse<?> filterParticipants(@RequestBody ParticipantFilterRequest request) {
        return ApiResponse.success(adminParticipantService.filterParticipants(request));
    }

    @GetMapping("/participants/all")
public ApiResponse<?> getAllParticipants() {

    List<ParticipantDraw> list = participantDrawRepository.findAll();

    List<ParticipantResponse> resp = list.stream().map(pd -> {
        User u = pd.getUser();

        return ParticipantResponse.builder()
                .id(pd.getId())
                .userId(u.getId())
                .name(u.getName())
                .email(u.getEmail())
                .phone(u.getPhone())
                .department(u.getDepartment())
                .externalId(u.getExternalId())
                .guest(u.isGuest())
                .drawId(pd.getDraw().getId())
                .drawName(pd.getDraw().getName())
                .voucherUsed(pd.getVoucherUsed() != null ? pd.getVoucherUsed().getCode() : null)
                .joinedAt(pd.getJoinedAt())
                .winner(pd.isWinner())
                .redeemed(pd.isRedeemed())
                .build();

    }).toList();

    return ApiResponse.success(resp);
}

}
