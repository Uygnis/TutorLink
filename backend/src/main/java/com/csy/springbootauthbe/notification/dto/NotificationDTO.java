package com.csy.springbootauthbe.notification.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationDTO {
    private String id;
    private String userId;
    private String type;
    private String bookingId;
    private String message;
    private boolean read;
    private LocalDateTime createdAt;
}
