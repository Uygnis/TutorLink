package com.csy.springbootauthbe.tutor.utils;

import com.csy.springbootauthbe.tutor.entity.QualificationFile;
import com.csy.springbootauthbe.user.entity.AccountStatus;
import com.csy.springbootauthbe.user.entity.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TutorResponse {
    private String id;
    private List<QualificationFile> qualifications;
    private Double hourlyRate;
}
