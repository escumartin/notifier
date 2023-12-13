package com.notifier.domain.service;

import com.notifier.application.dto.EventDTO;

import java.util.List;

public interface GoogleCalendarService {

    /**
     * Gets events from Google Calendar based on the specified days offset.
     *
     * @param daysOffset Number of days offset from the current date. Use positive values for future days, negative values for past days, and 0 for events on the current day.
     * @return List of events based on the specified days offset.
     */
    List<EventDTO> getEventsByDaysOffset(int daysOffset);
}
