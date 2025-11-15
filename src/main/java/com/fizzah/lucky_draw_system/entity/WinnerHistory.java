package com.fizzah.lucky_draw_system.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "winner_history")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WinnerHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "draw_id")
    private Draw draw;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "voucher_id")
    private Voucher voucher;

    private Double prizeAmount;

    @Column(name = "announced_at")
    private LocalDateTime announcedAt;

    private boolean redeemed;

    @Column(columnDefinition = "TEXT")
    private String adminNote;
}
