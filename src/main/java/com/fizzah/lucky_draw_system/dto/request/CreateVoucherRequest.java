package com.fizzah.lucky_draw_system.dto.request;

import com.fizzah.lucky_draw_system.enums.AccessLevel;
import com.fizzah.lucky_draw_system.enums.VoucherType;
import lombok.Data;

@Data
public class CreateVoucherRequest {
    private String code;
    private String name;
    private VoucherType type;          // ACCESS or PRIZE
    private AccessLevel accessLevel;   // ALL, GUEST, SPECIFIC (if ACCESS)
    private String departmentRestriction; // optional
    private Integer maxUses; // for prize voucher
}
