package com.csy.springbootauthbe.patient.dto;

import lombok.*;
import org.bson.types.ObjectId;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PatientDTO {

    private String id;

    private String patientReferenceId;

    private String patientName;

    private String medicalConditionType;

    private String status;
}
