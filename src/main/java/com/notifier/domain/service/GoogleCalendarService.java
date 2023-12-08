package com.notifier.domain.service;

import com.notifier.application.dto.EventDTO;

import java.io.IOException;
import java.util.List;

public interface GoogleCalendarService {
    List<EventDTO> getUpcomingEvents() throws IOException;
}
