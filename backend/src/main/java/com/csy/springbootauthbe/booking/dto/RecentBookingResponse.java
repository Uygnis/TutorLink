package com.csy.springbootauthbe.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RecentBookingResponse {
    private long totalCount; // total sessions
    private List<BookingDTO> recentSessions;
}
