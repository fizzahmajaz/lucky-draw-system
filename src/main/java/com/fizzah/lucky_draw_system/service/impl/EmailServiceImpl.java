package com.fizzah.lucky_draw_system.service.impl;

import com.fizzah.lucky_draw_system.entity.*;
import com.fizzah.lucky_draw_system.enums.EmailStatus;
import com.fizzah.lucky_draw_system.repository.EmailLogRepository;
import com.fizzah.lucky_draw_system.service.EmailService;

import jakarta.mail.internet.MimeMessage;
import jakarta.mail.MessagingException;

import lombok.RequiredArgsConstructor;

import org.springframework.mail.MailException;
import org.springframework.mail.javamail.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;
    private final EmailLogRepository emailLogRepository;

    // -----------------------
    // CORE EMAIL SENDER
    // -----------------------
    @Transactional
    public void sendEmail(String to, String subject, String body) {

        EmailLog log = new EmailLog();
        log.setToEmail(to);
        log.setSubject(subject);
        log.setBody(body);
        log.setSentAt(LocalDateTime.now());

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, "UTF-8");

            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(body, true); // HTML

            mailSender.send(message);

            log.setStatus(EmailStatus.SENT);
            log.setResponse("OK");

        } catch (MessagingException | MailException ex) {
            ex.printStackTrace();
            log.setStatus(EmailStatus.FAILED);
            log.setResponse(ex.getMessage());
        }

        emailLogRepository.save(log);
    }

    // -----------------------
    // REGISTRATION TEMPLATE
    // -----------------------
    private String buildRegistrationTemplate(User user) {
        return """
                <h2>Welcome to the Lucky Draw System!</h2>
                <p>Assalamualaikum <b>%s</b>,</p>
                <p>Your account has been created successfully.</p>
                <p>You can now join active draws.</p>
                <br>
                <p>JazakAllah Khair,<br>Lucky Draw System</p>
                """.formatted(user.getName());
    }

    @Override
    public void sendRegistrationEmail(User user) {
        sendEmail(user.getEmail(),
                "Registration Successful",
                buildRegistrationTemplate(user));
    }

    // -----------------------
    // DRAW JOINED TEMPLATE
    // -----------------------
    private String buildJoinedTemplate(User user, Draw draw) {
        return """
                <h2>You have joined a draw!</h2>
                <p>Assalamualaikum <b>%s</b>,</p>
                <p>You have successfully joined:</p>
                <h3>%s</h3>
                <p>Prize: <b>%s</b></p>
                <p>Start: %s</p>
                <p>End: %s</p>
                <br>
                <p>Best of luck!</p>
                """.formatted(
                user.getName(),
                draw.getName(),
                draw.getPrize(),
                draw.getStartDate(),
                draw.getEndDate()
        );
    }

    @Override
    public void sendDrawJoinedEmail(User user, Draw draw) {
        sendEmail(
                user.getEmail(),
                "You Joined a Lucky Draw",
                buildJoinedTemplate(user, draw)
        );
    }

    // -----------------------
    // WINNER TEMPLATE
    // -----------------------
    private String buildWinnerTemplate(User user, Draw draw, WinnerHistory history) {

        return """
                <h2>🎉 Congratulations! You are a WINNER 🎉</h2>
                <p>Assalamualaikum <b>%s</b>,</p>
                <p>You have won the lucky draw:</p>
                <h3>%s</h3>
                <p>Prize: <b>%s</b></p>
                <p>Announced At: %s</p>
                <br>
                <p>May Allah grant you more barakah.</p>
                """.formatted(
                user.getName(),
                draw.getName(),
                draw.getPrize(),
                history.getAnnouncedAt()
        );
    }

    @Override
    public void sendWinnerEmail(User user, Draw draw, WinnerHistory history) {
        sendEmail(
                user.getEmail(),
                "🎉 You are a Winner!",
                buildWinnerTemplate(user, draw, history)
        );
    }
}
