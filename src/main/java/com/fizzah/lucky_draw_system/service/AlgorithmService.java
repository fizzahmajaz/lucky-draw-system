package com.fizzah.lucky_draw_system.service;

import com.fizzah.lucky_draw_system.entity.WinnerHistory;

import java.util.List;

public interface AlgorithmService {

    List<WinnerHistory> executeAlgorithm(Long drawId, int numberOfWinners, Long adminId);
}
