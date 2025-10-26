package com.csy.springbootauthbe.event.utils;


import com.csy.springbootauthbe.student.dto.StudentDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventRequest {
    private String start;
    private String end;
    private String description;
    private String tutorId; // userId of tutor
}