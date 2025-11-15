package com.fizzah.lucky_draw_system.dto.response;

import com.fizzah.lucky_draw_system.enums.AccessLevel;
import com.fizzah.lucky_draw_system.enums.VoucherType;
import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class VoucherResponse {
    private Long id;
    private String code;
    private String name;
    private VoucherType type;
    private AccessLevel accessLevel;
    private boolean redeemed;
    private Integer maxUses;
}
