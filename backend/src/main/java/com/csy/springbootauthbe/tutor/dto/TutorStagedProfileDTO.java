package com.csy.springbootauthbe.tutor.dto;

import java.util.List;
import java.util.Map;

import com.csy.springbootauthbe.tutor.entity.Availability;
import com.csy.springbootauthbe.tutor.entity.QualificationFile;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TutorStagedProfileDTO {
    private Double hourlyRate;
    private String firstName;
    private String lastName;
    private String email;
    private String status;
    private List<QualificationFile> qualifications;
    private Map<String, Availability> availability;
    private String subject;
    private String profileImageUrl;
    private List<String> lessonType;
    private String description;
}
