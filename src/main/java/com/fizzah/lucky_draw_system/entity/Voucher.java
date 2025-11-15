package com.fizzah.lucky_draw_system.entity;

import java.time.LocalDateTime;

import com.fizzah.lucky_draw_system.enums.AccessLevel;
import com.fizzah.lucky_draw_system.enums.VoucherType;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "vouchers")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Voucher {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = true)
    private String code;
    private String name;

    @Enumerated(EnumType.STRING)
    private VoucherType type;

    @Enumerated(EnumType.STRING)
    private AccessLevel accessLevel;
    private String departmentRestriction;
    private boolean isRedeemed;
    private Integer maxUses;

    @ManyToOne
    @JoinColumn(name = "created_by_admin_id")
    private Admin createdByAdmin;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

}
