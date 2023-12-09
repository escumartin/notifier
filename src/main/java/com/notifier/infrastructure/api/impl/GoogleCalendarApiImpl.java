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
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
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
    public List<EventDTO> getTodayEvents() {
        try {
            NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
            Credential credential = getCredentials(HTTP_TRANSPORT);
            Calendar service = buildCalendarService(HTTP_TRANSPORT, credential);

            LocalDate currentDate = LocalDate.now();
            DateTime startDateTime = new DateTime(currentDate.atStartOfDay().toInstant(ZoneId.systemDefault().getRules().getOffset(currentDate.atStartOfDay())).toEpochMilli());
            DateTime endDateTime = new DateTime(currentDate.atTime(23, 59, 59).toInstant(ZoneId.systemDefault().getRules().getOffset(currentDate.atTime(23, 59, 59))).toEpochMilli());

            Events googleEvents = service.events().list("primary")
                    .setTimeMin(startDateTime)
                    .setTimeMax(endDateTime)
                    .execute();

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
        return googleCalendarEvents.stream()
                .map(this::adaptGoogleEvent)
                .collect(Collectors.toList());
    }

    private Event adaptGoogleEvent(com.google.api.services.calendar.model.Event googleEvent) {
        String title = googleEvent.getSummary();
        String description = googleEvent.getDescription();

        DateTime startDateTime = googleEvent.getStart().getDateTime();
        LocalDateTime dateTime;

        if (startDateTime != null) {
            // Si hay fecha y hora específicas
            dateTime = parseDateTime(startDateTime.toStringRfc3339());
        } else {
            // Si es un evento de todo el día
            dateTime = parseDateTime(googleEvent.getStart().getDate().toString());
        }

        boolean isAllDay = (googleEvent.getStart().getDateTime() == null);

        return new Event(title, description, dateTime, isAllDay);
    }

    private LocalDateTime parseDateTime(String dateTimeString) {
        try {
            // Intenta parsear la cadena de fecha y hora con el formateador ISO 8601
            return LocalDateTime.parse(dateTimeString, DateTimeFormatter.ISO_OFFSET_DATE_TIME);
        } catch (DateTimeParseException e) {
            // En caso de error, intenta parsear la cadena sin información de zona horaria
            return LocalDate.parse(dateTimeString).atStartOfDay();
        }
    }



    private boolean isEventToday(com.google.api.services.calendar.model.Event googleEvent, LocalDate currentDate) {
        DateTime eventDateTime = googleEvent.getStart().getDateTime();

        if (eventDateTime == null) {
            // Si no hay una fecha y hora específicas, consideramos la fecha
            eventDateTime = googleEvent.getStart().getDate();
        }

        // Convertir DateTime a Instant y luego a LocalDate
        Instant instant = Instant.ofEpochMilli(eventDateTime.getValue());
        LocalDate eventDate = instant.atZone(ZoneId.systemDefault()).toLocalDate();

        // Compara si la fecha del evento es igual o posterior a la fecha actual
        return !eventDate.isBefore(currentDate);
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
