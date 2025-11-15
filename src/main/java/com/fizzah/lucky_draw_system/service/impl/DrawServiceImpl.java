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
    private final VoucherRepository voucherRepository;
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
        draw.setPrizeType(req.getPrizeType());
        draw.setPrizeAmount(req.getPrizeAmount());
        draw.setStartDate(req.getStartDate());
        draw.setEndDate(req.getEndDate());
        draw.setDescription(req.getDescription());
        draw.setDepartment(req.getDepartment());
        draw.setTimerSeconds(req.getTimerSeconds());
        draw.setMaxWinner(req.getMaxWinners() == null ? 1 : req.getMaxWinners());
        draw.setStatus(DrawStatus.DRAFT);
        draw.setCreatedByAdmin(admin);
        draw.setCreatedat(LocalDateTime.now());

        // attach voucher if provided (must exist)
        if (req.getVoucherCode() != null && !req.getVoucherCode().isEmpty()) {
            Voucher v = voucherRepository.findByCode(req.getVoucherCode())
                    .orElseThrow(() -> new NotFoundException("Voucher not found"));
            draw.setVoucher(v);
        }

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
        if (req.getPrizeType() != null) draw.setPrizeType(req.getPrizeType());
        if (req.getPrizeAmount() != null) draw.setPrizeAmount(req.getPrizeAmount());
        if (req.getStartDate() != null) draw.setStartDate(req.getStartDate());
        if (req.getEndDate() != null) draw.setEndDate(req.getEndDate());
        if (req.getDescription() != null) draw.setDescription(req.getDescription());
        if (req.getDepartment() != null) draw.setDepartment(req.getDepartment());
        if (req.getTimerSeconds() != null) draw.setTimerSeconds(req.getTimerSeconds());
        if (req.getMaxWinners() != null) draw.setMaxWinner(req.getMaxWinners());
        if (req.getStatus() != null) draw.setStatus(DrawStatus.valueOf(req.getStatus()));
        if (req.getVoucherCode() != null) {
            Voucher v = voucherRepository.findByCode(req.getVoucherCode())
                    .orElseThrow(() -> new NotFoundException("Voucher not found"));
            draw.setVoucher(v);
        }

        return drawRepository.save(draw);
    }

    // -----------------------------
    // LIST DRAWS (paged)
    // -----------------------------
    @Override
    public Page<DrawResponse> listDraws(Pageable pageable) {
        Page<Draw> page = drawRepository.findAll(pageable);
        return page.map(this::toDrawResponse);
    }

    // -----------------------------
    // LIST ACTIVE DRAWS
    // -----------------------------
    @Override
    public Page<DrawResponse> listActiveDraws(Pageable pageable) {
        Page<Draw> page = drawRepository.findByStatus(DrawStatus.ACTIVE, pageable);
        return page.map(this::toDrawResponse);
    }

    // -----------------------------
    // GET DRAW DETAILS (modal)
    // If userId provided, include whether user joined and whether winner
    // -----------------------------
    @Override
    public DrawResponse getDrawDetails(Long drawId, Long userId) {
        Draw draw = drawRepository.findById(drawId)
                .orElseThrow(() -> new NotFoundException("Draw not found"));

        DrawResponse resp = toDrawResponse(draw);
        long totalParticipants = participantDrawRepository.countByDrawId(draw.getId());
        resp.setTotalParticipants(totalParticipants);

        // If userId provided, optionally augment (frontend will request separate endpoints for join status)
        return resp;
    }

    // -----------------------------
    // JOIN DRAW
    // -----------------------------
    @Override
    @Transactional
    public com.fizzah.lucky_draw_system.dto.response.ApiResponse<?> joinDraw(Long drawId, JoinDrawRequest req) {
        Draw draw = drawRepository.findById(drawId)
                .orElseThrow(() -> new NotFoundException("Draw not found"));

        // check date window
        Date now = new Date();
        if (draw.getStartDate() != null && now.before(draw.getStartDate())) {
            throw new BadRequestException("Draw has not started yet");
        }
        if (draw.getEndDate() != null && now.after(draw.getEndDate())) {
            throw new BadRequestException("Draw has already ended");
        }

        // user must exist
        User user = userRepository.findById(req.getUserId())
                .orElseThrow(() -> new NotFoundException("User not found"));

        // check if already joined
        participantDrawRepository.findByUserIdAndDrawId(user.getId(), draw.getId()).ifPresent(pd -> {
            throw new BadRequestException("User already joined this draw");
        });

        // Validate voucher rules:
        if (draw.getVoucher() != null) {
            // the draw expects a specific voucher code to join
            if (req.getVoucherCode() == null || !req.getVoucherCode().equals(draw.getVoucher().getCode())) {
                throw new BadRequestException("This draw requires voucher: " + draw.getVoucher().getCode());
            }
        } else {
            // draw has no prize voucher attached: allow any unless access voucher logic is enforced globally
            // Optionally, if user provided a voucher code, check it is an ACCESS voucher and valid
            if (req.getVoucherCode() != null && !req.getVoucherCode().isEmpty()) {
                Voucher access = voucherRepository.findByCode(req.getVoucherCode())
                        .orElseThrow(() -> new NotFoundException("Voucher not found"));
                // if access voucher is SPECIFIC ensure department matches
                if (access.getAccessLevel() != null && access.getAccessLevel().name().equals("SPECIFIC")
                        && access.getDepartmentRestriction() != null
                        && !access.getDepartmentRestriction().contains(user.getDepartment())) {
                    throw new BadRequestException("Your voucher does not allow joining this draw");
                }
            }
        }

        ParticipantDraw pd = ParticipantDraw.builder()
                .user(user)
                .draw(draw)
                .voucherUsed(req.getVoucherCode() != null ? voucherRepository.findByCode(req.getVoucherCode()).orElse(null) : null)
                .joinedAt(LocalDateTime.now())
                .isWinner(false)
                .redeemed(false)
                .accountDetails(req.getAccountDetailsJson())
                .build();

        participantDrawRepository.save(pd);

        // send confirmation email
        emailService.sendDrawJoinedEmail(user, draw);

        return com.fizzah.lucky_draw_system.dto.response.ApiResponse.success("Joined draw successfully", null);
    }

    // -----------------------------
    // LIST PARTICIPANTS (paged) - ADMIN
    // -----------------------------
    @Override
    public Page<ParticipantResponse> listParticipants(Long drawId, Pageable pageable) {
        Page<ParticipantDraw> pgs = participantDrawRepository.findByDrawId(drawId, pageable);
        return pgs.map(this::toParticipantResponse);
    }

    // -----------------------------
    // LIST WINNERS
    // -----------------------------
    @Override
    public Page<WinnerResponse> listWinners(Long drawId, Pageable pageable) {
        List<WinnerHistory> winners = winnerHistoryRepository.findByDrawId(drawId);
        List<WinnerResponse> list = winners.stream().map(this::toWinnerResponse).collect(Collectors.toList());
        int start = Math.min((int)pageable.getOffset(), list.size());
        int end = Math.min(start + pageable.getPageSize(), list.size());
        return new PageImpl<>(list.subList(start, end), pageable, list.size());
    }

    // -----------------------------
    // Helper mappers
    // -----------------------------
    private DrawResponse toDrawResponse(Draw d) {
        return DrawResponse.builder()
                .id(d.getId())
                .name(d.getName())
                .prizeType(d.getPrizeType())
                .prizeAmount(d.getPrizeAmount())
                .voucherCode(d.getVoucher() != null ? d.getVoucher().getCode() : null)
                .startDate(d.getStartDate())
                .endDate(d.getEndDate())
                .description(d.getDescription())
                .department(d.getDepartment())
                .timerSeconds(d.getTimerSeconds())
                .status(d.getStatus())
                .maxWinners(d.getMaxWinner())
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
                .voucherUsed(pd.getVoucherUsed() != null ? pd.getVoucherUsed().getCode() : null)
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
                .prizeAmount(wh.getPrizeAmount())
                .voucherCode(wh.getVoucher() != null ? wh.getVoucher().getCode() : null)
                .announcedAt(wh.getAnnouncedAt())
                .redeemed(wh.isRedeemed())
                .build();
    }
}
