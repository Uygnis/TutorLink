package com.csy.springbootauthbe.student.utils;

import lombok.Data;

@Data
public class TutorSearchRequest {
    private String name;        // partial match (firstname + lastname)
    private String subject;     // exact match
    private Double minPrice;
    private Double maxPrice;
    private String availability; // e.g. "MONDAY" key in availability map
}

