package com.csy.springbootauthbe.student.dto;

import lombok.Data;

import java.util.Map;

@Data
public class TutorProfileDTO {
    private String id;
    private String firstname;
    private String lastname;
    private String subject;
    private Double hourlyRate;
    private Map<String, Object> availability; // you can refine this later
}

