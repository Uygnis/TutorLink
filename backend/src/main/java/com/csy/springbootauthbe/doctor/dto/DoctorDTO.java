package com.csy.springbootauthbe.doctor.dto;

import lombok.*;
import org.bson.types.ObjectId;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DoctorDTO {

    private String id;

    private String docId;

    private String name;

    private String email;

    private String status;
}
