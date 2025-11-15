package com.fizzah.lucky_draw_system.service.impl;

import com.fizzah.lucky_draw_system.entity.*;
import com.fizzah.lucky_draw_system.repository.*;
import com.fizzah.lucky_draw_system.service.ExportService;
import com.fizzah.lucky_draw_system.util.CsvGenerator;
import com.fizzah.lucky_draw_system.util.PdfGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ExportServiceImpl implements ExportService {

    private final ParticipantDrawRepository participantDrawRepository;
    private final WinnerHistoryRepository winnerHistoryRepository;

    // Helper to build rows
    private List<Map<String, String>> buildParticipantRows(List<ParticipantDraw> pds) {
        return pds.stream().map(pd -> {
            Map<String, String> r = new LinkedHashMap<>();
            User u = pd.getUser();
            r.put("ParticipantId", String.valueOf(u.getId()));
            r.put("Name", u.getName());
            r.put("Email", u.getEmail());
            r.put("Phone", u.getPhone() == null ? "" : u.getPhone());
            r.put("Department", u.getDepartment() == null ? "" : u.getDepartment());
            r.put("ExternalId", u.getExternalId() == null ? "" : u.getExternalId());
            r.put("VoucherUsed", pd.getVoucherUsed() != null ? pd.getVoucherUsed().getCode() : "");
            r.put("JoinedAt", pd.getJoinedAt() == null ? "" : pd.getJoinedAt().toString());
            r.put("IsWinner", String.valueOf(pd.isWinner()));
            r.put("Redeemed", String.valueOf(pd.isRedeemed()));
            return r;
        }).collect(Collectors.toList());
    }

    private List<Map<String, String>> buildWinnerRows(List<WinnerHistory> whs) {
        return whs.stream().map(wh -> {
            Map<String, String> r = new LinkedHashMap<>();
            User u = wh.getUser();
            Draw d = wh.getDraw();
            r.put("WinnerHistoryId", String.valueOf(wh.getId()));
            r.put("UserId", String.valueOf(u.getId()));
            r.put("Name", u.getName());
            r.put("Email", u.getEmail());
            r.put("DrawId", String.valueOf(d.getId()));
            r.put("DrawName", d.getName());
            r.put("PrizeAmount", wh.getPrizeAmount() == null ? "" : String.valueOf(wh.getPrizeAmount()));
            r.put("VoucherCode", wh.getVoucher() != null ? wh.getVoucher().getCode() : "");
            r.put("AnnouncedAt", wh.getAnnouncedAt() == null ? "" : wh.getAnnouncedAt().toString());
            r.put("Redeemed", String.valueOf(wh.isRedeemed()));
            return r;
        }).collect(Collectors.toList());
    }

    @Override
    public byte[] exportParticipantsCsv(Long drawId) {
        List<ParticipantDraw> pds = participantDrawRepository.findByDrawId(drawId);
        List<String> headers = Arrays.asList("ParticipantId","Name","Email","Phone","Department","ExternalId","VoucherUsed","JoinedAt","IsWinner","Redeemed");
        List<Map<String,String>> rows = buildParticipantRows(pds);
        return CsvGenerator.generateCsv(headers, rows);
    }

    @Override
    public byte[] exportParticipantsPdf(Long drawId) {
        List<ParticipantDraw> pds = participantDrawRepository.findByDrawId(drawId);
        List<String> headers = Arrays.asList("ParticipantId","Name","Email","Phone","Department","ExternalId","VoucherUsed","JoinedAt","IsWinner","Redeemed");
        List<Map<String,String>> rows = buildParticipantRows(pds);
        return PdfGenerator.generateTablePdf("Participants for Draw " + drawId, headers, rows);
    }

    @Override
    public byte[] exportAllParticipantsCsv() {
        List<ParticipantDraw> pds = participantDrawRepository.findAll();
        List<String> headers = Arrays.asList("ParticipantId","Name","Email","Phone","Department","ExternalId","VoucherUsed","JoinedAt","IsWinner","Redeemed");
        List<Map<String,String>> rows = buildParticipantRows(pds);
        return CsvGenerator.generateCsv(headers, rows);
    }

    @Override
    public byte[] exportAllParticipantsPdf() {
        List<ParticipantDraw> pds = participantDrawRepository.findAll();
        List<String> headers = Arrays.asList("ParticipantId","Name","Email","Phone","Department","ExternalId","VoucherUsed","JoinedAt","IsWinner","Redeemed");
        List<Map<String,String>> rows = buildParticipantRows(pds);
        return PdfGenerator.generateTablePdf("All Participants", headers, rows);
    }

    @Override
    public byte[] exportWinnersCsv(Long drawId) {
        List<WinnerHistory> whs = winnerHistoryRepository.findByDrawId(drawId);
        List<String> headers = Arrays.asList("WinnerHistoryId","UserId","Name","Email","DrawId","DrawName","PrizeAmount","VoucherCode","AnnouncedAt","Redeemed");
        List<Map<String,String>> rows = buildWinnerRows(whs);
        return CsvGenerator.generateCsv(headers, rows);
    }

    @Override
    public byte[] exportWinnersPdf(Long drawId) {
        List<WinnerHistory> whs = winnerHistoryRepository.findByDrawId(drawId);
        List<String> headers = Arrays.asList("WinnerHistoryId","UserId","Name","Email","DrawId","DrawName","PrizeAmount","VoucherCode","AnnouncedAt","Redeemed");
        List<Map<String,String>> rows = buildWinnerRows(whs);
        return PdfGenerator.generateTablePdf("Winners for Draw " + drawId, headers, rows);
    }

    @Override
    public byte[] exportUserHistoryCsv(Long userId) {
        List<ParticipantDraw> pds = participantDrawRepository.findByUserId(userId, org.springframework.data.domain.Pageable.unpaged()).getContent();
        List<String> headers = Arrays.asList("ParticipantId","DrawId","DrawName","JoinedAt","IsWinner","Redeemed","VoucherUsed");
        List<Map<String,String>> rows = pds.stream().map(pd -> {
            Map<String,String> r = new LinkedHashMap<>();
            r.put("ParticipantId", String.valueOf(pd.getId()));
            r.put("DrawId", String.valueOf(pd.getDraw().getId()));
            r.put("DrawName", pd.getDraw().getName());
            r.put("JoinedAt", pd.getJoinedAt() == null ? "" : pd.getJoinedAt().toString());
            r.put("IsWinner", String.valueOf(pd.isWinner()));
            r.put("Redeemed", String.valueOf(pd.isRedeemed()));
            r.put("VoucherUsed", pd.getVoucherUsed() != null ? pd.getVoucherUsed().getCode() : "");
            return r;
        }).collect(Collectors.toList());
        return CsvGenerator.generateCsv(headers, rows);
    }

    @Override
    public byte[] exportUserHistoryPdf(Long userId) {
        List<ParticipantDraw> pds = participantDrawRepository.findByUserId(userId, org.springframework.data.domain.Pageable.unpaged()).getContent();
        List<String> headers = Arrays.asList("ParticipantId","DrawId","DrawName","JoinedAt","IsWinner","Redeemed","VoucherUsed");
        List<Map<String,String>> rows = pds.stream().map(pd -> {
            Map<String,String> r = new LinkedHashMap<>();
            r.put("ParticipantId", String.valueOf(pd.getId()));
            r.put("DrawId", String.valueOf(pd.getDraw().getId()));
            r.put("DrawName", pd.getDraw().getName());
            r.put("JoinedAt", pd.getJoinedAt() == null ? "" : pd.getJoinedAt().toString());
            r.put("IsWinner", String.valueOf(pd.isWinner()));
            r.put("Redeemed", String.valueOf(pd.isRedeemed()));
            r.put("VoucherUsed", pd.getVoucherUsed() != null ? pd.getVoucherUsed().getCode() : "");
            return r;
        }).collect(Collectors.toList());
        return PdfGenerator.generateTablePdf("User Draw History - User " + userId, headers, rows);
    }
}
