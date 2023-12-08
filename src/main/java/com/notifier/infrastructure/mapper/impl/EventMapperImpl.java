//package com.notifier.infrastructure.mapper.impl;
//
//import com.notifier.application.dto.EventDTO;
//import com.notifier.domain.model.Event;
//import com.notifier.infrastructure.mapper.EventMapper;
//
//import java.time.Instant;
//
//public class EventMapperImpl implements EventMapper {
//    @Override
//    public EventDTO toDTO(Event event) {
//        // Lógica de mapeo de Event a EventDTO
//        EventDTO eventDTO = new EventDTO();
//        eventDTO.setSummary(event.getSummary());
//        eventDTO.setStart(event.getStart());
//        // ... otros campos
//        return eventDTO;
//    }
//
//    @Override
//    public Event toDomain(com.google.api.services.calendar.model.Event googleEvent) {
//        // Lógica de mapeo de com.google.api.services.calendar.model.Event a Event
//        Event event = new Event();
//
//        // Asegúrate de convertir EventDateTime a Instant
//        Instant startInstant = Instant.ofEpochMilli(googleEvent.getStart().getDateTime().getValue());
//        event.setStart(startInstant);
//
//        event.setSummary(googleEvent.getSummary());
//        // ... otros campos
//        return event;
//    }
//}