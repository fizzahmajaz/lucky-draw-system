package com.fizzah.lucky_draw_system.entity;

import java.util.Date;
import java.time.LocalDateTime;
import com.fizzah.lucky_draw_system.enums.DrawStatus;
import jakarta.persistence.*;
import lombok.*;

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

    // FRONTEND: "Win iPhone", "Win 2 Lakh", etc.
    private String prize;

    // FRONTEND: voucher code user typed OR null
    private String voucherCode;

    // FRONTEND: ALL / RESTRICTED
    private String voucherAccessLevel;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "start_date")
    private Date startDate;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "end_date")
    private Date endDate;

    @Column(columnDefinition = "TEXT")
    private String description;

    private String department;

    @Column(name = "timer_seconds")
    private Integer timerSeconds;

    @Enumerated(EnumType.STRING)
    private DrawStatus status;

    private Integer maxWinners;

    @ManyToOne
    @JoinColumn(name = "created_by_admin")
    private Admin createdByAdmin;

    @Column(name = "created_at")
    private LocalDateTime createdAt;
}
