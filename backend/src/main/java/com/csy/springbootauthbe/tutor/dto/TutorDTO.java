package com.csy.springbootauthbe.tutor.dto;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TutorDTO {
    private String id;
    private String userId;
}
