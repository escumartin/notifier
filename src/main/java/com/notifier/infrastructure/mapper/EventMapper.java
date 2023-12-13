package com.notifier.infrastructure.mapper;

import com.google.api.services.calendar.model.Event;
import com.notifier.application.dto.EventDTO;


public interface EventMapper {

    /**
     * Convierte un evento del dominio a un DTO de evento.
     *
     * @param googleEvent Evento del dominio.
     * @return DTO de evento.
     */
    EventDTO mapToDTO(Event googleEvent);
}