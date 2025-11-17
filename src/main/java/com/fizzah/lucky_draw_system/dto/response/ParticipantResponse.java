package com.fizzah.lucky_draw_system.dto.response;

import lombok.*;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ParticipantResponse {

    private Long id;              // participantDraw ID
    private Long userId;          // user ID

    private String name;
    private String email;
    private String phone;
    private String department;
    private String externalId;
    private boolean guest;

    private Long drawId;          // draw ID
    private String drawName;      // draw name

    private String voucherUsed;
    private LocalDateTime joinedAt;

    private boolean winner;
    private boolean redeemed;
}
