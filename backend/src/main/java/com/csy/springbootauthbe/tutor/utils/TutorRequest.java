package com.csy.springbootauthbe.tutor.utils;

import com.csy.springbootauthbe.tutor.entity.Availability;
import com.csy.springbootauthbe.tutor.entity.QualificationFile;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TutorRequest {
    private String userId;
    private Double hourlyRate;
    private List<QualificationFile> qualifications;
    private Map<String, Availability> availability;
}

