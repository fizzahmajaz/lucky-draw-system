package com.fizzah.lucky_draw_system.service.impl;

import com.fizzah.lucky_draw_system.entity.*;
import com.fizzah.lucky_draw_system.enums.PrizeType;
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
    private final VoucherRepository voucherRepository;

    /**
     * Executes the winner selection algorithm.
     * - Validates numberOfWinners <= participants count
     * - Uses SecureRandom for unique selection
     * - Persists WinnerHistory entries
     * - Sets participantDraw.isWinner = true
     * - Sets draw.status = ENDED
     * - Sends winner email notifications
     */
    @Override
    @Transactional
    public List<WinnerHistory> executeAlgorithm(Long drawId, int numberOfWinners, Long adminId) {

        Draw draw = drawRepository.findById(drawId)
                .orElseThrow(() -> new NotFoundException("Draw not found"));

        List<ParticipantDraw> participants = participantDrawRepository.findByDrawId(drawId);

        if (participants == null || participants.isEmpty()) {
            throw new ExceededWinnersLimitException("No participants available for this draw.");
        }

        int total = participants.size();
        if (numberOfWinners > total) {
            throw new ExceededWinnersLimitException("You are exceeding winners limit.");
        }

        // Secure unique random selection
        SecureRandom secureRandom;
        try {
            secureRandom = SecureRandom.getInstanceStrong();
        } catch (Exception e) {
            secureRandom = new SecureRandom();
        }

        // if numberOfWinners == total then everyone wins
        Set<Integer> winnerIndexes = new HashSet<>();
        while (winnerIndexes.size() < numberOfWinners) {
            int idx = secureRandom.nextInt(total);
            winnerIndexes.add(idx);
        }

        List<WinnerHistory> winnersSaved = new ArrayList<>();

        LocalDateTime now = LocalDateTime.now();

        // If draw has a voucher, fetch it (admin-provided)
        Voucher prizeVoucher = null;
        if (draw.getPrizeType() == PrizeType.VOUCHER && draw.getVoucher() != null) {
            prizeVoucher = voucherRepository.findById(draw.getVoucher().getId()).orElse(null);
        }

        for (Integer idx : winnerIndexes) {
            ParticipantDraw pd = participants.get(idx);

            // mark participant as winner
            pd.setWinner(true);
            participantDrawRepository.save(pd);

            WinnerHistory wh = new WinnerHistory();
            wh.setDraw(draw);
            wh.setUser(pd.getUser());
            if (draw.getPrizeType() == PrizeType.CASH) {
                wh.setPrizeAmount(draw.getPrizeAmount());
            } else if (draw.getPrizeType() == PrizeType.VOUCHER && prizeVoucher != null) {
                wh.setVoucher(prizeVoucher);
            }
            wh.setAnnouncedAt(now);
            wh.setRedeemed(false);
            wh.setAdminNote("Selected by algorithm");
            WinnerHistory saved = winnerHistoryRepository.save(wh);
            winnersSaved.add(saved);

            // send winner notification email (async if EmailService implements @Async)
            try {
                emailService.sendWinnerEmail(pd.getUser(), draw, saved);
            } catch (Exception ex) {
                // swallow emailing exception but log stacktrace
                ex.printStackTrace();
            }
        }

        // update draw status to ENDED
        draw.setStatus(com.fizzah.lucky_draw_system.enums.DrawStatus.ENDED);
        drawRepository.save(draw);

        return winnersSaved;
    }
}
