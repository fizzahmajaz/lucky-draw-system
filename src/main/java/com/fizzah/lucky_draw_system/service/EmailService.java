package com.fizzah.lucky_draw_system.service;

import com.fizzah.lucky_draw_system.entity.*;

public interface EmailService {
    void sendRegistrationEmail(User user);
    void sendDrawJoinedEmail(User user, Draw draw);
    void sendWinnerEmail(User user, Draw draw, WinnerHistory history);
    void sendVoucherRedeemEmail(User user, Voucher voucher);
}
