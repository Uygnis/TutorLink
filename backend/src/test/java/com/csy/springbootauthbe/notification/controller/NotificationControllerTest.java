package com.csy.springbootauthbe.notification.controller;

import com.csy.springbootauthbe.notification.dto.NotificationDTO;
import com.csy.springbootauthbe.notification.service.NotificationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(
        controllers = NotificationController.class,
        excludeAutoConfiguration = {
                org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration.class,
                org.springframework.boot.autoconfigure.security.servlet.SecurityFilterAutoConfiguration.class
        }
)
@AutoConfigureMockMvc(addFilters = false)
class NotificationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private NotificationService notificationService;

    // ✅ Mock security filters to prevent context errors
    @MockBean private com.csy.springbootauthbe.config.JWTAuthenticationFilter jwtAuthenticationFilter;
    @MockBean private com.csy.springbootauthbe.config.JWTService jwtService;
    @MockBean private com.csy.springbootauthbe.common.wrapper.UserDetailsServiceWrapper userDetailsServiceWrapper;

    // ✅ Test 1: SSE stream registration
    @Test
    void streamNotifications_registersEmitter_returnsSseEmitter() throws Exception {
        doNothing().when(notificationService).registerEmitter(eq("U1"), any(SseEmitter.class));

        mockMvc.perform(get("/api/v1/notifications/stream/{userId}", "U1")
                        .accept(MediaType.TEXT_EVENT_STREAM))
                .andExpect(status().isOk());


        verify(notificationService, times(1)).registerEmitter(eq("U1"), any(SseEmitter.class));
    }

    // ✅ Test 2: Fetch notifications list
    @Test
    void getNotifications_returnsListOfNotifications() throws Exception {
        var dto1 = new NotificationDTO();
        dto1.setId("N1");
        dto1.setMessage("Hello World");
        var dto2 = new NotificationDTO();
        dto2.setId("N2");
        dto2.setMessage("Booking confirmed");

        when(notificationService.getUserNotifications("U1"))
                .thenReturn(List.of(dto1, dto2));

        mockMvc.perform(get("/api/v1/notifications")
                        .param("userId", "U1")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$[0].id").value("N1"))
                .andExpect(jsonPath("$[1].id").value("N2"));

        verify(notificationService, times(1)).getUserNotifications("U1");
    }

    // ✅ Test 3: Mark notification as read
    @Test
    void markAsRead_callsServiceAndReturns200() throws Exception {
        doNothing().when(notificationService).markAsRead("N123");

        mockMvc.perform(put("/api/v1/notifications/{id}/read", "N123"))
                .andExpect(status().isOk());

        verify(notificationService, times(1)).markAsRead("N123");
    }
}
