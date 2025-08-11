package com.csy.springbootauthbe.student.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "students")
public class Student {

    @Id
    private String id;

    // Reference to User document for login credentials
    private String userId;

    // Student-specific details
    private String studentNumber;
    private String gradeLevel;

}

