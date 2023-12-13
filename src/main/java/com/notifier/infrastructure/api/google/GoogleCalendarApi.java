package com.notifier.infrastructure.api.google;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.Events;
import com.notifier.application.dto.EventDTO;
import com.notifier.domain.service.GoogleCalendarService;
import com.notifier.infrastructure.exception.GoogleCalendarIOException;
import com.notifier.infrastructure.exception.GoogleCalendarSecurityException;
import com.notifier.infrastructure.mapper.EventMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

import static com.notifier.infrastructure.api.google.GoogleAuthentication.JSON_FACTORY;

@Service
public class GoogleCalendarApi implements GoogleCalendarService {

    Logger log = LogManager.getLogger(LogManager.ROOT_LOGGER_NAME);

    private static final String APPLICATION_NAME = "Notifier";


    private final EventMapper eventMapper;
    private final GoogleAuthentication googleAuthentication;

    @Autowired
    public GoogleCalendarApi(EventMapper eventMapper, GoogleAuthentication googleAuthentication) {
        this.eventMapper = eventMapper;
        this.googleAuthentication = googleAuthentication;
    }

    @Override
    public List<EventDTO> getEventsByDaysOffset(int daysOffset) {
        try {
            final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
            final Credential credential = googleAuthentication.getCredentials(HTTP_TRANSPORT);
            final Calendar service = buildCalendarService(HTTP_TRANSPORT, credential);

            LocalDate targetDate = LocalDate.now().plusDays(daysOffset);
            final DateTime startDateTime = new DateTime(targetDate.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli());
            final DateTime endDateTime = new DateTime(targetDate.atTime(23, 59, 59).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli());

            final Events googleEvents = service.events().list("primary")
                    .setTimeMin(startDateTime)
                    .setTimeMax(endDateTime)
                    .execute();

            final List<Event> googleCalendarEvents = googleEvents.getItems();
            final List<EventDTO> targetDateEvents = adaptGoogleEvents(googleCalendarEvents);

            return targetDateEvents;

        } catch (IOException e) {
            log.error("Error fetching events from Google Calendar: {}", e.getMessage(), e);
            throw new GoogleCalendarIOException("Error fetching events from Google Calendar", e);
        } catch (GeneralSecurityException e) {
            log.error("Security error while fetching events from Google Calendar: {}", e.getMessage(), e);
            throw new GoogleCalendarSecurityException("Security error while fetching events from Google Calendar", e);
        }
    }

    private List<EventDTO> adaptGoogleEvents(List<Event> googleCalendarEvents) {
        return googleCalendarEvents.stream()
                .map(eventMapper::mapToDTO)
                .toList();
    }

    private Calendar buildCalendarService(NetHttpTransport HTTP_TRANSPORT, Credential credential) {
        return new Calendar.Builder(HTTP_TRANSPORT, JSON_FACTORY, credential)
                .setApplicationName(APPLICATION_NAME)
                .build();
    }

}