package com.fizzah.lucky_draw_system.entity;

import java.time.LocalDateTime;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
@Table(name="participant_draw")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ParticipantDraw {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "draw_id")
    private Draw draw;

    private String voucherUsed;

    @Column(name = "joined_at")
    private LocalDateTime joinedAt;

    @Column(name = "is_winner")
    private boolean isWinner;

    private boolean redeemed;

    @Column(columnDefinition = "JSON")
    private String accountDetails;
    
}
