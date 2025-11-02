package com.csy.springbootauthbe.notification.controller;

import com.csy.springbootauthbe.notification.service.NotificationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class NotificationSseControllerTest {

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private NotificationSseController controller;

    private MockMvc mockMvc;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    void testStreamNotifications_RegistersEmitterSuccessfully() throws Exception {
        String userId = "tutor42";

        mockMvc.perform(get("/api/v1/sse/notifications/stream/" + userId)
                .accept(MediaType.TEXT_EVENT_STREAM))
            .andExpect(status().isOk());

        verify(notificationService).registerEmitter(eq(userId), any(SseEmitter.class));
    }
}
