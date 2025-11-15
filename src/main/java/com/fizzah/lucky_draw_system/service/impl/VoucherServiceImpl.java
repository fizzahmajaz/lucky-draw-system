package com.fizzah.lucky_draw_system.service.impl;

import com.fizzah.lucky_draw_system.dto.request.CreateVoucherRequest;
import com.fizzah.lucky_draw_system.dto.response.ApiResponse;
import com.fizzah.lucky_draw_system.entity.*;
import com.fizzah.lucky_draw_system.enums.VoucherType;
import com.fizzah.lucky_draw_system.exception.BadRequestException;
import com.fizzah.lucky_draw_system.exception.NotFoundException;
import com.fizzah.lucky_draw_system.exception.VoucherInvalidException;
import com.fizzah.lucky_draw_system.repository.*;
import com.fizzah.lucky_draw_system.service.VoucherService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class VoucherServiceImpl implements VoucherService {

    private final VoucherRepository voucherRepository;
    private final AdminRepository adminRepository;
    private final ParticipantDrawRepository participantDrawRepository;
    private final WinnerHistoryRepository winnerHistoryRepository;

    // ---------------------------------------------------
    // CREATE VOUCHER (Admin Only)
    // ---------------------------------------------------
    @Override
    public Voucher createVoucher(CreateVoucherRequest req, Long adminId) {

        if (voucherRepository.findByCode(req.getCode()).isPresent()) {
            throw new BadRequestException("Voucher code already exists");
        }

        Admin admin = adminRepository.findById(adminId)
                .orElseThrow(() -> new NotFoundException("Admin not found"));

        Voucher voucher = Voucher.builder()
                .code(req.getCode())
                .name(req.getName())
                .type(req.getType())
                .accessLevel(req.getAccessLevel())
                .departmentRestriction(req.getDepartmentRestriction())
                .isRedeemed(false)
                .maxUses(req.getMaxUses() == null ? 1 : req.getMaxUses())
                .createdByAdmin(admin)
                .createdAt(LocalDateTime.now())
                .build();

        return voucherRepository.save(voucher);
    }

    // ---------------------------------------------------
    // VALIDATE VOUCHER (User/Guest register)
    // ---------------------------------------------------
    @Override
    public Voucher validateVoucher(String code) {
        return voucherRepository.findByCode(code)
                .orElseThrow(() -> new VoucherInvalidException("Invalid or expired voucher"));
    }

    // ---------------------------------------------------
    // REDEEM PRIZE VOUCHER (Winner Only)
    // ---------------------------------------------------
    @Override
    public ApiResponse<?> redeemPrizeVoucher(Long userId, Long drawId, String code) {

        Voucher voucher = voucherRepository.findByCode(code)
                .orElseThrow(() -> new NotFoundException("Voucher not found"));

        if (voucher.getType() != VoucherType.PRIZE) {
            throw new BadRequestException("Not a prize voucher");
        }

        ParticipantDraw pd = participantDrawRepository.findByUserIdAndDrawId(userId, drawId)
                .orElseThrow(() -> new NotFoundException("You did not participate in this draw"));

        if (!pd.isWinner()) {
            throw new BadRequestException("Only winners can redeem prize vouchers");
        }

        if (pd.isRedeemed()) {
            throw new BadRequestException("You already redeemed your voucher");
        }

        // Check max uses count
        long redeemedCount = winnerHistoryRepository.findByDrawId(drawId).stream()
                .filter(WinnerHistory::isRedeemed)
                .count();

        if (redeemedCount >= voucher.getMaxUses()) {
            throw new BadRequestException("Voucher redeem limit reached");
        }

        // update ParticipantDraw
        pd.setRedeemed(true);
        participantDrawRepository.save(pd);

        // update WinnerHistory
        winnerHistoryRepository.findByDrawId(drawId).forEach(history -> {
            if (history.getUser().getId().equals(userId)) {
                history.setRedeemed(true);
                history.setAdminNote("Voucher redeemed successfully");
                winnerHistoryRepository.save(history);
            }
        });

        if (redeemedCount + 1 >= voucher.getMaxUses()) {
            voucher.setRedeemed(true);
            voucherRepository.save(voucher);
        }

        return ApiResponse.success("Voucher redeemed successfully", voucher.getCode());
    }
}
