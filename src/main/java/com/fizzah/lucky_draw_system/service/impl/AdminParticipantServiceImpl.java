package com.fizzah.lucky_draw_system.service.impl;

import com.fizzah.lucky_draw_system.dto.request.ParticipantFilterRequest;
import com.fizzah.lucky_draw_system.dto.response.ParticipantResponse;
import com.fizzah.lucky_draw_system.entity.ParticipantDraw;
import com.fizzah.lucky_draw_system.repository.ParticipantDrawRepository;
import com.fizzah.lucky_draw_system.service.AdminParticipantService;
import com.fizzah.lucky_draw_system.spec.ParticipantSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AdminParticipantServiceImpl implements AdminParticipantService {

    private final ParticipantDrawRepository participantDrawRepository;

    @Override
    public Page<ParticipantResponse> filterParticipants(ParticipantFilterRequest req) {

        Pageable pageable = PageRequest.of(
                req.getPage(), 
                req.getSize(),
                req.getSortDirection().equals("desc")
                        ? Sort.by(req.getSortBy()).descending()
                        : Sort.by(req.getSortBy()).ascending()
        );

        Page<ParticipantDraw> page =
                participantDrawRepository.findAll(ParticipantSpecification.filter(req), pageable);

        return page.map(pd -> ParticipantResponse.builder()
                .id(pd.getId())
                .userId(pd.getUser().getId())
                .name(pd.getUser().getName())
                .email(pd.getUser().getEmail())
                .phone(pd.getUser().getPhone())
                .department(pd.getUser().getDepartment())
                .externalId(pd.getUser().getExternalId())
                .voucherUsed(pd.getVoucherUsed())
                .winner(pd.isWinner())
                .redeemed(pd.isRedeemed())
                .build());
    }
}
