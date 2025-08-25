package com.csy.springbootauthbe.tutor.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QualificationFile {
    private String name;
    private String type;   // "pdf", "jpg", "png"
    private String path;        // S3/Cloud URL or GridFS ID
    private Date uploadedAt;
}
