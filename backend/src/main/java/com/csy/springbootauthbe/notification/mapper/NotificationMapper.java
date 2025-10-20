package com.csy.springbootauthbe.notification.mapper;

import com.csy.springbootauthbe.notification.dto.NotificationDTO;
import com.csy.springbootauthbe.notification.entity.Notification;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface NotificationMapper {
    NotificationMapper INSTANCE = Mappers.getMapper(NotificationMapper.class);

    NotificationDTO toDto(Notification notification);

    Notification toEntity(NotificationDTO dto);
}

