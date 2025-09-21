package com.csy.springbootauthbe.event.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "events")
public class Event {

    @Id
    private String id;
    private Long eventId;
    private String start;
    private String end;
    private String description;
    private String tutorId; // userId of tutor


}

