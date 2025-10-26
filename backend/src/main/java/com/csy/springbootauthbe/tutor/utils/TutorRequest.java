package com.csy.springbootauthbe.tutor.utils;

import com.csy.springbootauthbe.tutor.entity.Availability;
import com.csy.springbootauthbe.tutor.entity.QualificationFile;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TutorRequest {
    private String userId;
    private String subject;
    private Double hourlyRate;
    private String description;
    private List<String> lessonType;
    private List<MultipartFile> fileUploads;
    private List<QualificationFile> qualifications;
    private Map<String, Availability> availability;
}

