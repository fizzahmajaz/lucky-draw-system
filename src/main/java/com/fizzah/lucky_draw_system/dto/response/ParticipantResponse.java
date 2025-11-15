package com.fizzah.lucky_draw_system.dto.response;

import lombok.*;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ParticipantResponse {
    private Long id;
    private Long userId;
    private String name;
    private String email;
    private String phone;
    private String department;
    private String externalId;
    private String voucherUsed;
    private LocalDateTime joinedAt;
    private boolean winner;
    private boolean redeemed;
}
