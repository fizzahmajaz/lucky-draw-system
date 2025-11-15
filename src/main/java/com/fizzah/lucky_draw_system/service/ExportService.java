package com.fizzah.lucky_draw_system.service;

public interface ExportService {

    byte[] exportParticipantsCsv(Long drawId);

    byte[] exportParticipantsPdf(Long drawId);

    byte[] exportAllParticipantsCsv();

    byte[] exportAllParticipantsPdf();

    byte[] exportWinnersCsv(Long drawId);

    byte[] exportWinnersPdf(Long drawId);

    byte[] exportUserHistoryCsv(Long userId);

    byte[] exportUserHistoryPdf(Long userId);
}
