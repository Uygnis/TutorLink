package com.csy.springbootauthbe.tutor.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "tutors")
public class Tutor {

    @Id
    private String id;
    private String subject;
    // Reference to User document for login credentials, composite
    private String userId;
    private Double hourlyRate;

    // List of uploaded qualifications
    private List<QualificationFile> qualifications;
    private Map<String, Availability> availability;

    private String profileImageUrl;
    private List<String> lessonType;
    private String description;
}

