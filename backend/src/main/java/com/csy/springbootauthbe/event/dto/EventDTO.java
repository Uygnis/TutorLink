package com.csy.springbootauthbe.event.dto;

import lombok.*;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventDTO {
    private String id;
    private Long eventId;
    private String start;
    private String end;
    private String description;
    private String tutorId; // userId of tutor
}
