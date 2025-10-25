package com.csy.springbootauthbe.notification.controller;

import com.csy.springbootauthbe.common.utils.SanitizedLogger;
import com.csy.springbootauthbe.notification.dto.NotificationDTO;
import com.csy.springbootauthbe.notification.service.NotificationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;

@RestController
@RequestMapping("/api/v1/notifications")
public class NotificationController {

    private final NotificationService service;
    private static final SanitizedLogger logger = SanitizedLogger.getLogger(NotificationController.class);

    public NotificationController(NotificationService service) {
        this.service = service;
    }

    /** SSE stream endpoint */
    @GetMapping(value = "/stream/{userId}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter streamNotifications(@PathVariable String userId) {
        SseEmitter emitter = new SseEmitter(Long.MAX_VALUE);
        service.registerEmitter(userId, emitter);
        logger.info("SSE registered for userId: {}", userId);
        return emitter;
    }

    /** Initial fetch of existing notifications */
    @GetMapping
    public List<NotificationDTO> getNotifications(@RequestParam String userId) {
        logger.info("Fetching notifications for userId: {}", userId);
        return service.getUserNotifications(userId);
    }

    /** Mark notification as read */
    @PutMapping("/{id}/read")
    public void markAsRead(@PathVariable String id) {
        logger.info("Marking notification {} as read", id);
        service.markAsRead(id);
    }
}

