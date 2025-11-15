package com.fizzah.lucky_draw_system.entity;

import java.util.Date;
import java.time.LocalDateTime;

import com.fizzah.lucky_draw_system.enums.DrawStatus;
import com.fizzah.lucky_draw_system.enums.PrizeType;

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
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Builder
@Table(name = "draws")
@AllArgsConstructor
@NoArgsConstructor

public class Draw {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;

    @Enumerated(EnumType.STRING)
    private PrizeType prizeType;
    private Double prizeAmount;

    @ManyToOne
    @JoinColumn(name = "voucher_id")
    private Voucher voucher;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "start_date")
    private Date startDate;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "end_date")
    private Date endDate;

    @Column(columnDefinition = "TEXT")
    private String Description;
    private String department;

    @Column(name = "timer_seconds")
    private Integer timerSeconds;

    @Enumerated(EnumType.STRING)
    private DrawStatus status;
    private Integer MaxWinner;

    @ManyToOne
    @JoinColumn(name = "created_by_admin")
    private Admin createdByAdmin;

    @Column(name = "created_at")
    private LocalDateTime createdat;

}
