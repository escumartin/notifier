package com.notifier.infrastructure.controller;

import com.notifier.application.dto.EventDTO;
import com.notifier.infrastructure.api.impl.GoogleCalendarApiImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;

@RestController
@RequestMapping("/events")
public class EventController {

    private final GoogleCalendarApiImpl googleCalendarApi;

    @Autowired
    public EventController(GoogleCalendarApiImpl googleCalendarApi) {
        this.googleCalendarApi = googleCalendarApi;
    }

    @GetMapping("/upcoming")
    public List<EventDTO> getUpcomingEvents() throws IOException, GeneralSecurityException {
        return googleCalendarApi.getUpcomingEvents();
    }
}
