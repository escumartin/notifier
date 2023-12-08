//package com.notifier.application.impl;
//
//import com.notifier.application.ReminderService;
//import com.notifier.application.dto.EventDTO;
//import com.notifier.domain.model.Event;
//import com.notifier.domain.model.WhatsAppMessage;
//import com.notifier.domain.repository.EventRepository;
//import com.notifier.domain.service.GoogleCalendarService;
//import com.notifier.infrastructure.api.WhatsAppApi;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.scheduling.annotation.Scheduled;
//import org.springframework.stereotype.Service;
//
//import java.io.IOException;
//import java.time.Instant;
//import java.time.LocalDateTime;
//import java.util.List;
//
//@Service
//public class ReminderServiceImpl implements ReminderService {
//
//    private final GoogleCalendarService googleCalendarApi;
//    private final WhatsAppApi whatsAppApi;
//    private final EventRepository eventRepository;
//
//
//    @Autowired
//    public ReminderServiceImpl(GoogleCalendarService googleCalendarApi, WhatsAppApi whatsAppApi, EventRepository eventRepository) {
//        this.googleCalendarApi = googleCalendarApi;
//        this.whatsAppApi = whatsAppApi;
//        this.eventRepository = eventRepository;
//    }
//
//
//    @Scheduled(fixedDelay = 3600000) // Ejecutar cada hora
//    @Override
//    public void remindEvents() {
//        // Obtener eventos próximos desde la API de Google Calendar
//        List<EventDTO> upcomingEvents = null;
//        try {
//            upcomingEvents = googleCalendarApi.getUpcomingEvents();
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
//
//        // Obtener la hora actual
//        LocalDateTime now = LocalDateTime.now();
//
//        // Iterar sobre los eventos y enviar recordatorio por WhatsApp
//        for (EventDTO event : upcomingEvents) {
//            // Verificar si el evento está a una hora de distancia
//            if (event.getDate().isBefore(Instant.from(now.plusHours(1))) && event.getDate().isAfter(Instant.from(now))) {
//                // Verificar si ya se envió un recordatorio para este evento
//                if (!eventRepository.hasSentReminder(event.getId())) {
//                    // Enviar recordatorio y marcar como enviado
//                    WhatsAppMessage message = buildMessage(event);
//                    sendWhatsAppMessage(message);
//                    eventRepository.markReminderAsSent(event.getId());
//                }
//            }
//        }
//    }
//
//
//    private WhatsAppMessage buildMessage(Event event) {
//        // Lógica para construir el mensaje de WhatsApp basado en el evento
//        String content = "Recuerda tu evento: " + event.getTitle() + " el " + event.getDate();
//        return new WhatsAppMessage(event.getRecipient(), content);
//    }
//
//
//    private void sendWhatsAppMessage(WhatsAppMessage message) {
//        // Lógica para enviar el mensaje de WhatsApp usando la API correspondiente
//        whatsAppApi.sendMessage(message);
//    }
//}
