package com.fizzah.lucky_draw_system.dto.response;

import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserResponse {
    private Long id;
    private String name;
    private String email;
    private String username;
    private String phone;
    private String department;
    private boolean guest;
}
