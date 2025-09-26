package com.csy.springbootauthbe.booking.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "bookings")
public class Booking {

    @Id
    private String id; // MongoDB _id

    private String tutorId;
    private String studentId;

    private String date;   // yyyy-MM-dd
    private String start;  // HH:mm
    private String end;    // HH:mm

    private String status; // confirmed, pending, cancelled
}
