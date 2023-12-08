package com.notifier.infrastructure.api.impl;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.CalendarScopes;
import com.google.api.services.calendar.model.Events;
import com.notifier.application.dto.EventDTO;
import com.notifier.domain.model.Event;
import com.notifier.domain.service.GoogleCalendarService;
import com.notifier.infrastructure.mapper.EventMapper;
import com.notifier.infrastructure.mapper.GoogleEventMapper;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class GoogleCalendarApiImpl implements GoogleCalendarService {

    private static final String APPLICATION_NAME = "Notifier";
    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
    private static final String TOKENS_DIRECTORY_PATH = "tokens";
    private static final List<String> SCOPES = Collections.singletonList(CalendarScopes.CALENDAR_READONLY);
    private final EventMapper eventMapper = Mappers.getMapper(EventMapper.class);
    private final GoogleEventMapper googleEventMapper = Mappers.getMapper(GoogleEventMapper.class);
    private static final String CREDENTIALS_PATH = "/credentials.json";


    @Override
    public List<EventDTO> getUpcomingEvents() {
        try {
            NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
            Credential credential = getCredentials(HTTP_TRANSPORT);
            Calendar service = buildCalendarService(HTTP_TRANSPORT, credential);

            Events googleEvents = service.events().list("primary").execute();
            List<com.google.api.services.calendar.model.Event> googleCalendarEvents = googleEvents.getItems();

            List<Event> todayEvents = adaptGoogleEvents(googleCalendarEvents);

            return todayEvents.stream()
                    .map(eventMapper::toDTO)
                    .toList();
        } catch (IOException | GeneralSecurityException e) {
            throw new RuntimeException("Error al obtener eventos de Google Calendar", e);
        }
    }

    private List<Event> adaptGoogleEvents(List<com.google.api.services.calendar.model.Event> googleCalendarEvents) {
        return filterTodayEvents(googleCalendarEvents)
                .stream()
                .map(googleEventMapper::toDomain)
                .collect(Collectors.toList());
    }

    private List<com.google.api.services.calendar.model.Event> filterTodayEvents(List<com.google.api.services.calendar.model.Event> events) {
        LocalDate currentDate = LocalDate.now();

        return events.stream()
                .filter(event -> isEventToday(event, currentDate))
                .collect(Collectors.toList());
    }

    private boolean isEventToday(com.google.api.services.calendar.model.Event googleEvent, LocalDate currentDate) {
        DateTime eventDateTime = googleEvent.getStart().getDateTime();
        if (eventDateTime == null) {
            // Si no hay una fecha y hora específicas, consideramos la fecha
            eventDateTime = googleEvent.getStart().getDate();
        }

        // Convertir DateTime a LocalDate
        LocalDate eventDate = LocalDate.ofInstant(
                Instant.ofEpochMilli(eventDateTime.getValue()),
                ZoneId.systemDefault()
        );

        // Compara si la fecha del evento es igual a la fecha actual
        return eventDate.isEqual(currentDate);
    }

    private Calendar buildCalendarService(NetHttpTransport HTTP_TRANSPORT, Credential credential) {
        return new Calendar.Builder(HTTP_TRANSPORT, JSON_FACTORY, credential)
                .setApplicationName(APPLICATION_NAME)
                .build();
    }

    private Credential getCredentials(final NetHttpTransport HTTP_TRANSPORT) throws IOException {
        try (InputStream in = GoogleCalendarApiImpl.class.getResourceAsStream(CREDENTIALS_PATH)) {
            if (in == null) {
                throw new IOException("No se pudo encontrar el archivo de credenciales: " + CREDENTIALS_PATH);
            }
            GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

            GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                    HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
                    .setDataStoreFactory(new com.google.api.client.util.store.FileDataStoreFactory(new java.io.File(TOKENS_DIRECTORY_PATH)))
                    .setAccessType("offline")
                    .build();
            LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(8888).build();
            return new AuthorizationCodeInstalledApp(flow, receiver).authorize("user");
        }
    }
}
