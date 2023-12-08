package com.notifier.infrastructure.mapper;

import com.notifier.domain.model.Event;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

@Mapper
public interface GoogleEventMapper {

    GoogleEventMapper INSTANCE = Mappers.getMapper(GoogleEventMapper.class);

    @Mapping(target = "title", source = "summary")
    @Mapping(target = "description", source = "description")
    @Mapping(target = "dateTime", source = "start.dateTime")
    Event toDomain(com.google.api.services.calendar.model.Event googleEvent);

    default LocalDateTime toLocalDateTime(com.google.api.client.util.DateTime dateTime) {
        if (dateTime == null) {
            return null;
        }
        return Instant.ofEpochMilli(dateTime.getValue())
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();
    }
}