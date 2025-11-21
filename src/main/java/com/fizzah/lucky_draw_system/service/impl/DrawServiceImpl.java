package com.fizzah.lucky_draw_system.service.impl;

import com.fizzah.lucky_draw_system.dto.request.*;
import com.fizzah.lucky_draw_system.dto.response.*;
import com.fizzah.lucky_draw_system.entity.*;
import com.fizzah.lucky_draw_system.enums.DrawStatus;
import com.fizzah.lucky_draw_system.exception.BadRequestException;
import com.fizzah.lucky_draw_system.exception.NotFoundException;
import com.fizzah.lucky_draw_system.repository.*;
import com.fizzah.lucky_draw_system.service.DrawService;
import com.fizzah.lucky_draw_system.service.EmailService;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DrawServiceImpl implements DrawService {

    private final DrawRepository drawRepository;
    private final AdminRepository adminRepository;
    private final UserRepository userRepository;
    private final ParticipantDrawRepository participantDrawRepository;
    private final WinnerHistoryRepository winnerHistoryRepository;
    private final EmailService emailService;

    // -----------------------------
    // CREATE DRAW
    // -----------------------------
    @Override
    public Draw createDraw(CreateDrawRequest req, Long adminId) {

        Admin admin = adminRepository.findById(adminId)
                .orElseThrow(() -> new NotFoundException("Admin not found"));

        Draw draw = new Draw();
        draw.setName(req.getName());
        draw.setPrize(req.getPrize());                  // STRING prize
        draw.setVoucherCode(req.getVoucherCode());      // STRING or null
        draw.setVoucherAccessLevel(req.getVoucherAccessLevel());
        draw.setStartDate(req.getStartDate());
        draw.setEndDate(req.getEndDate());
        draw.setDescription(req.getDescription());
        draw.setDepartment(req.getDepartment());
        draw.setTimerSeconds(req.getTimerSeconds());
        draw.setMaxWinners(req.getMaxWinners());
        draw.setStatus(DrawStatus.DRAFT);
        draw.setCreatedByAdmin(admin);
        draw.setCreatedAt(LocalDateTime.now());

        return drawRepository.save(draw);
    }

    // -----------------------------
    // UPDATE DRAW
    // -----------------------------
    @Override
    @Transactional
    public Draw updateDraw(Long drawId, UpdateDrawRequest req, Long adminId) {

        Draw draw = drawRepository.findById(drawId)
                .orElseThrow(() -> new NotFoundException("Draw not found"));

        if (req.getName() != null) draw.setName(req.getName());
        if (req.getPrize() != null) draw.setPrize(req.getPrize());
        if (req.getVoucherCode() != null) draw.setVoucherCode(req.getVoucherCode());
        if (req.getVoucherAccessLevel() != null) draw.setVoucherAccessLevel(req.getVoucherAccessLevel());
        if (req.getStartDate() != null) draw.setStartDate(req.getStartDate());
        if (req.getEndDate() != null) draw.setEndDate(req.getEndDate());
        if (req.getDescription() != null) draw.setDescription(req.getDescription());
        if (req.getDepartment() != null) draw.setDepartment(req.getDepartment());
        if (req.getTimerSeconds() != null) draw.setTimerSeconds(req.getTimerSeconds());
        if (req.getMaxWinners() != null) draw.setMaxWinners(req.getMaxWinners());
        if (req.getStatus() != null) draw.setStatus(DrawStatus.valueOf(req.getStatus()));

        return drawRepository.save(draw);
    }

    // -----------------------------
    // LIST ALL DRAWS
    // -----------------------------
    @Override
    public Page<DrawResponse> listDraws(Pageable pageable) {
        Page<Draw> page = drawRepository.findAll(pageable);
        return page.map(this::toDrawResponse);
    }

    // -----------------------------
    // LIST ACTIVE
    // -----------------------------
    @Override
    public Page<DrawResponse> listActiveDraws(Pageable pageable) {
        Page<Draw> page = drawRepository.findByStatus(DrawStatus.ACTIVE, pageable);
        return page.map(this::toDrawResponse);
    }

    // -----------------------------
    // GET DRAW DETAILS
    // -----------------------------
    @Override
    public DrawResponse getDrawDetails(Long drawId, Long userId) {
        Draw draw = drawRepository.findById(drawId)
                .orElseThrow(() -> new NotFoundException("Draw not found"));

        DrawResponse resp = toDrawResponse(draw);
        long totalParticipants = participantDrawRepository.countByDrawId(draw.getId());
        resp.setTotalParticipants(totalParticipants);

        return resp;
    }

    // -----------------------------
    // JOIN DRAW
    // -----------------------------
    @Override
    @Transactional
    public ApiResponse<?> joinDraw(Long drawId, JoinDrawRequest req) {

        Draw draw = drawRepository.findById(drawId)
                .orElseThrow(() -> new NotFoundException("Draw not found"));

        Date now = new Date();
        if (draw.getStartDate() != null && now.before(draw.getStartDate()))
            throw new BadRequestException("Draw has not started yet");

        if (draw.getEndDate() != null && now.after(draw.getEndDate()))
            throw new BadRequestException("Draw has already ended");

        User user = userRepository.findById(req.getUserId())
                .orElseThrow(() -> new NotFoundException("User not found"));

        participantDrawRepository.findByUserIdAndDrawId(user.getId(), drawId)
                .ifPresent(p -> {
                    throw new BadRequestException("Already joined this draw");
                });

        ParticipantDraw pd = ParticipantDraw.builder()
                .user(user)
                .draw(draw)
                .voucherUsed(req.getVoucherCode())    // JUST store string
                .joinedAt(LocalDateTime.now())
                .isWinner(false)
                .redeemed(false)
                .accountDetails(req.getAccountDetailsJson())
                .build();

        participantDrawRepository.save(pd);

        emailService.sendDrawJoinedEmail(user, draw);

        return ApiResponse.success("Joined draw successfully", null);
    }

    // -----------------------------
    // LIST PARTICIPANTS
    // -----------------------------
    @Override
    public Page<ParticipantResponse> listParticipants(Long drawId, Pageable pageable) {
        Page<ParticipantDraw> page = participantDrawRepository.findByDrawId(drawId, pageable);
        return page.map(this::toParticipantResponse);
    }

    // -----------------------------
    // LIST WINNERS
    // -----------------------------
    @Override
    public Page<WinnerResponse> listWinners(Long drawId, Pageable pageable) {
        List<WinnerHistory> winners = winnerHistoryRepository.findByDrawId(drawId);
        List<WinnerResponse> list = winners.stream().map(this::toWinnerResponse).collect(Collectors.toList());

        int start = Math.min((int) pageable.getOffset(), list.size());
        int end = Math.min(start + pageable.getPageSize(), list.size());

        return new PageImpl<>(list.subList(start, end), pageable, list.size());
    }

    // -----------------------------
    // MAPPERS
    // -----------------------------
    private DrawResponse toDrawResponse(Draw d) {
        return DrawResponse.builder()
                .id(d.getId())
                .name(d.getName())
                .prize(d.getPrize())
                .voucherCode(d.getVoucherCode())
                .voucherAccessLevel(d.getVoucherAccessLevel())
                .startDate(d.getStartDate())
                .endDate(d.getEndDate())
                .description(d.getDescription())
                .department(d.getDepartment())
                .timerSeconds(d.getTimerSeconds())
                .status(d.getStatus())
                .maxWinners(d.getMaxWinners())
                .totalParticipants(participantDrawRepository.countByDrawId(d.getId()))
                .build();
    }

    private ParticipantResponse toParticipantResponse(ParticipantDraw pd) {
        User u = pd.getUser();
        return ParticipantResponse.builder()
                .id(pd.getId())
                .userId(u.getId())
                .name(u.getName())
                .email(u.getEmail())
                .phone(u.getPhone())
                .department(u.getDepartment())
                .externalId(u.getExternalId())
                .voucherUsed(pd.getVoucherUsed())
                .joinedAt(pd.getJoinedAt())
                .winner(pd.isWinner())
                .redeemed(pd.isRedeemed())
                .build();
    }

    private WinnerResponse toWinnerResponse(WinnerHistory wh) {
    return WinnerResponse.builder()
            .id(wh.getId())
            .userId(wh.getUser().getId())
            .name(wh.getUser().getName())
            .email(wh.getUser().getEmail())
            .prize(wh.getDraw().getPrize())
            .announcedAt(wh.getAnnouncedAt())
            .redeemed(wh.isRedeemed())
            .build();
}

}
