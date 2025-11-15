package com.fizzah.lucky_draw_system.service;

import com.fizzah.lucky_draw_system.dto.request.CreateVoucherRequest;
import com.fizzah.lucky_draw_system.dto.response.ApiResponse;
import com.fizzah.lucky_draw_system.entity.Voucher;

public interface VoucherService {
    Voucher createVoucher(CreateVoucherRequest request, Long adminId);

    Voucher validateVoucher(String code);

    ApiResponse<?> redeemPrizeVoucher(Long userId, Long drawId, String code);
}
