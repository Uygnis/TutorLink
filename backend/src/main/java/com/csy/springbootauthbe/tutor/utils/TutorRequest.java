package com.csy.springbootauthbe.tutor.utils;

import com.csy.springbootauthbe.tutor.entity.QualificationFile;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TutorRequest {
    private String userId;
    private Double hourlyRate;
    private List<QualificationFile> qualifications;
}

