package com.fizzah.lucky_draw_system.controller;

import com.fizzah.lucky_draw_system.dto.request.CreateVoucherRequest;
import com.fizzah.lucky_draw_system.dto.request.ValidateVoucherRequest;
import com.fizzah.lucky_draw_system.dto.response.ApiResponse;
import com.fizzah.lucky_draw_system.entity.Voucher;
import com.fizzah.lucky_draw_system.service.VoucherService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/vouchers")
@RequiredArgsConstructor
public class VoucherController {

    private final VoucherService voucherService;

    // ADMIN CREATE VOUCHER
    @PostMapping
    public ApiResponse<?> createVoucher(@RequestBody CreateVoucherRequest req,
                                        @RequestParam Long adminId) {
        Voucher voucher = voucherService.createVoucher(req, adminId);
        return ApiResponse.success("Voucher created", voucher);
    }

    // VALIDATE
    @PostMapping("/validate")
    public ApiResponse<?> validate(@RequestBody ValidateVoucherRequest req) {
        Voucher v = voucherService.validateVoucher(req.getCode());
        return ApiResponse.success("Valid voucher", v);
    }

    // REDEEM
    @PostMapping("/redeem")
    public ApiResponse<?> redeem(@RequestParam Long userId,
                                 @RequestParam Long drawId,
                                 @RequestParam String code) {
        return voucherService.redeemPrizeVoucher(userId, drawId, code);
    }




}
