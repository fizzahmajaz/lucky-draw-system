package com.fizzah.lucky_draw_system.service;

import com.fizzah.lucky_draw_system.dto.request.ParticipantFilterRequest;
import com.fizzah.lucky_draw_system.dto.response.ParticipantResponse;
import org.springframework.data.domain.Page;

public interface AdminParticipantService {

    Page<ParticipantResponse> filterParticipants(ParticipantFilterRequest req);
}
