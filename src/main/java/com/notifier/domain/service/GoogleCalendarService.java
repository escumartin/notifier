package com.notifier.domain.service;

import com.notifier.application.dto.EventDTO;

import java.util.List;

public interface GoogleCalendarService {

    List<EventDTO> getTodayEvents();
}
