package com.csy.springbootauthbe.tutor.dto;

import com.csy.springbootauthbe.tutor.entity.Availability;
import lombok.*;

import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TutorDTO {
    private String id;
    private String userId;
    private Double hourlyRate;
    private Map<String, Availability> availability;
}
