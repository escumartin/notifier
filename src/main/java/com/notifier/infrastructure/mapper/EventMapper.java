package com.notifier.infrastructure.mapper;

import com.notifier.application.dto.EventDTO;
import com.notifier.domain.model.Event;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;


@Mapper
public interface EventMapper {

    EventMapper INSTANCE = Mappers.getMapper(EventMapper.class);

    @Mapping(target = "title", source = "title")
    @Mapping(target = "description", source = "description")
    @Mapping(target = "dateTime", source = "dateTime")
    EventDTO toDTO(Event event);

    @Mapping(target = "title", source = "title")
    @Mapping(target = "description", source = "description")
    @Mapping(target = "dateTime", source = "dateTime")
    Event toDomain(EventDTO eventDTO);

}