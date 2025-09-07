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
    private String id; // // MongoDB _id
    private String userId; // Reference to User document for login credentials
    private String studentNumber;
    private String gradeLevel;
    private String profileImageUrl; // S3 URL of profile picture

}

