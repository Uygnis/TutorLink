package com.csy.springbootauthbe.event.utils;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventResponse {
    private String id;
    private Long eventId;
    private String start;
    private String end;
    private String description;
    private String tutorId; // userId of tutor
}