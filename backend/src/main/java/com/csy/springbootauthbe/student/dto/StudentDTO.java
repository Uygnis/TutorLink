package com.csy.springbootauthbe.student.dto;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StudentDTO {
    private String id;
    private String userId;
    private String studentNumber;
    private String gradeLevel;
}
