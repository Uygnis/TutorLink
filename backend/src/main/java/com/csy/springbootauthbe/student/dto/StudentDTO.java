package com.csy.springbootauthbe.student.dto;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StudentDTO {
    private String id;
    private String userId;
    private String firstName;
    private String lastName;
    private String email;
    private String role;
    private String status;
    private String studentNumber;
    private String gradeLevel;
    private String profileImageUrl;
}
