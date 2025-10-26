package com.csy.springbootauthbe.tutor.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Review {
    private String bookingId;
    private String studentName;
    private int rating;
    private String comment;
}


