package com.fizzah.lucky_draw_system.repository;

import com.fizzah.lucky_draw_system.entity.Voucher;
import com.fizzah.lucky_draw_system.enums.VoucherType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface VoucherRepository extends JpaRepository<Voucher, Long> {
    Optional<Voucher> findByCode(String code);

    Page<Voucher> findByType(VoucherType type, Pageable pageable);

    // find by access level (for listing access vouchers)
    Page<Voucher> findByAccessLevel(String accessLevel, Pageable pageable);
}
