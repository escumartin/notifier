package com.notifier.infrastructure.controller.impl;

import com.notifier.application.dto.EventDTO;
import com.notifier.infrastructure.api.google.GoogleCalendarApi;
import com.notifier.infrastructure.controller.EventController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/events")
public class EventControllerImpl implements EventController {

    private final GoogleCalendarApi googleCalendarApi;

    @Autowired
    public EventControllerImpl(GoogleCalendarApi googleCalendarApi) {
        this.googleCalendarApi = googleCalendarApi;
    }

    @GetMapping("/today")
    @Override
    public List<EventDTO> getEventsByDaysOffset() {
        return googleCalendarApi.getEventsByDaysOffset(0);
    }

}
