package com.csy.springbootauthbe.event.mapper;

import com.csy.springbootauthbe.event.dto.EventDTO;
import com.csy.springbootauthbe.event.entity.Event;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface EventMapper {

    EventDTO toDTO(Event event);

    Event toEntity(EventDTO eventDTO);
}