package com.csy.springbootauthbe.booking.dto;

import lombok.*;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BookingDTO {
    private String id;
    private String tutorId;
    private String tutorName;
    private String studentId;
    private String studentName;
    private String lessonType;
    private String date;
    private String start;
    private String end;
    private String status;
    private BigDecimal amount;
}
