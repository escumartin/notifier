package com.notifier.infrastructure.mapper.impl;

import com.google.api.services.calendar.model.Event;
import com.notifier.application.dto.EventDTO;
import com.notifier.infrastructure.mapper.EventMapper;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

@Component
public class EventMapperImpl implements EventMapper {

    private final ModelMapper modelMapper;

    public EventMapperImpl(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    @Override
    public EventDTO mapToDTO(Event googleEvent) {
        EventDTO eventDTO = modelMapper.map(googleEvent, EventDTO.class);

        // Map the start date time field
        if (googleEvent.getStart() != null) {
            if (googleEvent.getStart().getDateTime() != null) {
                eventDTO.setStartDateTime(toLocalDateTime(googleEvent.getStart().getDateTime().getValue()));
            } else if (googleEvent.getStart().getDate() != null) {
                // If it's an all-day event, set the start date without time
                eventDTO.setStartDateTime(toLocalDateTime(googleEvent.getStart().getDate().getValue()));
                eventDTO.setAllDay(true);
            }
        }

        // Map the end date time field
        if (googleEvent.getEnd() != null && googleEvent.getEnd().getDateTime() != null) {
            eventDTO.setEndDateTime(toLocalDateTime(googleEvent.getEnd().getDateTime().getValue()));
        }

        return eventDTO;
    }

    private LocalDateTime toLocalDateTime(long milliseconds) {
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(milliseconds), ZoneId.systemDefault());
    }
}
