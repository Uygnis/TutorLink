package com.csy.springbootauthbe.tutor.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Availability {
    private boolean enabled;   // "true",
    private String start; // "09:00"
    private String end;   // "17:00"
}
