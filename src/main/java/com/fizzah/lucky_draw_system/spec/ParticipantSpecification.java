package com.fizzah.lucky_draw_system.spec;

import com.fizzah.lucky_draw_system.entity.ParticipantDraw;
import com.fizzah.lucky_draw_system.dto.request.ParticipantFilterRequest;

import java.time.LocalDate;

import org.springframework.data.jpa.domain.Specification;

public class ParticipantSpecification {

    public static Specification<ParticipantDraw> filter(ParticipantFilterRequest req) {
        return (root, query, cb) -> {

            var user = root.get("user");
            var predicates = cb.conjunction();

            // search
            if (req.getSearch() != null && !req.getSearch().isBlank()) {
                String like = "%" + req.getSearch().toLowerCase() + "%";
                predicates = cb.and(predicates, cb.or(
                        cb.like(cb.lower(user.get("name")), like),
                        cb.like(cb.lower(user.get("email")), like),
                        cb.like(cb.lower(user.get("phone")), like),
                        cb.like(cb.lower(user.get("username")), like)
                ));
            }

            // filter by voucher
            if (req.getVoucherCode() != null && !req.getVoucherCode().isBlank()) {
                predicates = cb.and(predicates,
                        cb.equal(root.get("voucherUsed").get("code"), req.getVoucherCode()));
            }

            // filter by prize type
            if (req.getPrizeType() != null) {
                predicates = cb.and(predicates,
                        cb.equal(root.get("draw").get("prizeType"), req.getPrizeType()));
            }



            // FILTER: voucher
            if (req.getVoucherCode() != null && !req.getVoucherCode().isBlank()) {
                predicates = cb.and(predicates,
                        cb.equal(root.get("voucherUsed").get("code"), req.getVoucherCode()));
            }

            // FILTER: draw name
            if (req.getDrawName() != null && !req.getDrawName().isBlank()) {
                predicates = cb.and(predicates,
                        cb.like(cb.lower(root.get("draw").get("name")), "%" + req.getDrawName().toLowerCase() + "%"));
            }

            // FILTER: prize type
            if (req.getPrizeType() != null && !req.getPrizeType().isBlank()) {
                predicates = cb.and(predicates,
                        cb.equal(root.get("draw").get("prizeType"), req.getPrizeType()));
            }

            // FILTER: department
            if (req.getDepartment() != null && !req.getDepartment().isBlank()) {
                predicates = cb.and(predicates,
                        cb.equal(root.get("department"), req.getDepartment()));
            }

            // FILTER: winner
            if (req.getWinner() != null) {
                predicates = cb.and(predicates,
                        cb.equal(root.get("isWinner"), req.getWinner()));
            }

            // DATE AFTER
            if (req.getJoinedAfter() != null) {
                predicates = cb.and(predicates,
                        cb.greaterThanOrEqualTo(root.get("joinedAt"), LocalDate.parse(req.getJoinedAfter()).atStartOfDay()));
            }

            // DATE BEFORE
            if (req.getJoinedBefore() != null) {
                predicates = cb.and(predicates,
                        cb.lessThanOrEqualTo(root.get("joinedAt"), LocalDate.parse(req.getJoinedBefore()).atTime(23, 59, 59)));
            }

            return predicates;
        };
        
    }
}
