package com.fizzah.lucky_draw_system.service.impl;

import com.fizzah.lucky_draw_system.entity.*;
import com.fizzah.lucky_draw_system.exception.ExceededWinnersLimitException;
import com.fizzah.lucky_draw_system.exception.NotFoundException;
import com.fizzah.lucky_draw_system.repository.*;
import com.fizzah.lucky_draw_system.service.AlgorithmService;
import com.fizzah.lucky_draw_system.service.EmailService;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class AlgorithmServiceImpl implements AlgorithmService {

    private final DrawRepository drawRepository;
    private final ParticipantDrawRepository participantDrawRepository;
    private final WinnerHistoryRepository winnerHistoryRepository;
    private final EmailService emailService;

    @Override
    @Transactional
    public List<WinnerHistory> executeAlgorithm(Long drawId, int numberOfWinners, Long adminId) {

        // Fetch draw
        Draw draw = drawRepository.findById(drawId)
                .orElseThrow(() -> new NotFoundException("Draw not found"));

        // Fetch participants
        List<ParticipantDraw> participants = participantDrawRepository.findByDrawId(drawId);

        if (participants == null || participants.isEmpty()) {
            throw new ExceededWinnersLimitException("No participants available for this draw.");
        }

        int total = participants.size();
        if (numberOfWinners > total) {
            throw new ExceededWinnersLimitException("You are exceeding winners limit.");
        }

        // Generate secure unique random numbers
        SecureRandom secureRandom;
        try {
            secureRandom = SecureRandom.getInstanceStrong();
        } catch (Exception e) {
            secureRandom = new SecureRandom();
        }

        Set<Integer> winnerIndexes = new HashSet<>();
        while (winnerIndexes.size() < numberOfWinners) {
            int idx = secureRandom.nextInt(total);
            winnerIndexes.add(idx);
        }

        List<WinnerHistory> winnersSaved = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();

        // ----------------------------------
        // CREATE WINNER HISTORY ENTRIES
        // ----------------------------------
        for (Integer idx : winnerIndexes) {
            ParticipantDraw pd = participants.get(idx);

            // Mark participant as winner
            pd.setWinner(true);
            participantDrawRepository.save(pd);

            // Save winner record
            WinnerHistory wh = new WinnerHistory();
            wh.setDraw(draw);
            wh.setUser(pd.getUser());
            wh.setPrizeAmount(null); // No cash system, using STRING prize
            wh.setAnnouncedAt(now);
            wh.setRedeemed(false);
            wh.setAdminNote("Selected by algorithm");

            WinnerHistory saved = winnerHistoryRepository.save(wh);
            winnersSaved.add(saved);

            // Send email notification
            try {
                emailService.sendWinnerEmail(pd.getUser(), draw, saved);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        // Mark draw as ended
        draw.setStatus(com.fizzah.lucky_draw_system.enums.DrawStatus.ENDED);
        drawRepository.save(draw);

        return winnersSaved;
    }
}
