package com.fizzah.lucky_draw_system.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

import com.fizzah.lucky_draw_system.enums.EmailStatus;

@Entity
@Table(name = "email_logs")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmailLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String toEmail;
    private String subject;

    @Column(columnDefinition = "LONGTEXT")
    private String body;

    @Column(name = "sent_at")
    private LocalDateTime sentAt;

    @Enumerated(EnumType.STRING)
    private EmailStatus status;

    @Column(columnDefinition = "TEXT")
    private String response;
}
