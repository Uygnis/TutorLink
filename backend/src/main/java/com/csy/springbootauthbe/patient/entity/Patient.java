package com.csy.springbootauthbe.patient.entity;

import lombok.*;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "patients") // MongoDB collection name
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Patient {

    @Id
    private String id;
    private String patientReferenceId;
    private String patientName;
    private String medicalConditionType;
    private String status;
}
