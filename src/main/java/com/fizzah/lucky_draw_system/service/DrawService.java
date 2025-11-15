package com.fizzah.lucky_draw_system.service;

import com.fizzah.lucky_draw_system.dto.request.*;
import com.fizzah.lucky_draw_system.dto.response.*;
import com.fizzah.lucky_draw_system.entity.Draw;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface DrawService {

    Draw createDraw(CreateDrawRequest request, Long adminId);

    Draw updateDraw(Long drawId, UpdateDrawRequest request, Long adminId);

    Page<DrawResponse> listDraws(Pageable pageable);

    Page<DrawResponse> listActiveDraws(Pageable pageable);

    DrawResponse getDrawDetails(Long drawId, Long userId);

    ApiResponse<?> joinDraw(Long drawId, JoinDrawRequest request);

    Page<ParticipantResponse> listParticipants(Long drawId, Pageable pageable);

    Page<WinnerResponse> listWinners(Long drawId, Pageable pageable);
}
