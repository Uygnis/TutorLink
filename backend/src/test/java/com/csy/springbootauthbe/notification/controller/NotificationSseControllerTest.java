package com.csy.springbootauthbe.notification.controller;

import com.csy.springbootauthbe.notification.service.NotificationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(
        controllers = NotificationSseController.class,
        excludeAutoConfiguration = {
                org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration.class,
                org.springframework.boot.autoconfigure.security.servlet.SecurityFilterAutoConfiguration.class
        }
)
@AutoConfigureMockMvc(addFilters = false)
class NotificationSseControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private NotificationService notificationService;

    // mock security beans (to avoid context errors)
    @MockBean private com.csy.springbootauthbe.config.JWTAuthenticationFilter jwtAuthenticationFilter;
    @MockBean private com.csy.springbootauthbe.config.JWTService jwtService;
    @MockBean private com.csy.springbootauthbe.common.wrapper.UserDetailsServiceWrapper userDetailsServiceWrapper;

    @Test
    void streamNotifications_registersEmitterAndReturnsSseStream() throws Exception {
        doNothing().when(notificationService).registerEmitter(eq("U123"), any(SseEmitter.class));

        mockMvc.perform(get("/api/v1/sse/notifications/stream/{userId}", "U123")
                        .accept(MediaType.TEXT_EVENT_STREAM))
                .andExpect(status().isOk());

        verify(notificationService, times(1))
                .registerEmitter(eq("U123"), any(SseEmitter.class));
    }
}
