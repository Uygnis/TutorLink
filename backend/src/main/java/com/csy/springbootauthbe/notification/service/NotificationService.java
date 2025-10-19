package com.csy.springbootauthbe.notification.service;

import com.csy.springbootauthbe.notification.dto.NotificationDTO;
import com.csy.springbootauthbe.notification.entity.Notification;
import com.csy.springbootauthbe.notification.mapper.NotificationMapper;
import com.csy.springbootauthbe.notification.repository.NotificationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

@Service
public class NotificationService {

    private final NotificationRepository repo;
    private final NotificationMapper mapper;
    private final ConcurrentHashMap<String, CopyOnWriteArrayList<SseEmitter>> emitters = new ConcurrentHashMap<>();
    private static final Logger logger = LoggerFactory.getLogger(NotificationService.class);

    public NotificationService(NotificationRepository repo, NotificationMapper mapper) {
        this.repo = repo;
        this.mapper = mapper;
    }

    /** Register SSE emitter for a user */
    public void registerEmitter(String userId, SseEmitter emitter) {
        emitters.computeIfAbsent(userId, k -> new CopyOnWriteArrayList<>()).add(emitter);

        emitter.onCompletion(() -> emitters.get(userId).remove(emitter));
        emitter.onTimeout(() -> emitters.get(userId).remove(emitter));
    }

    /** Fetch all existing notifications for a user (initial load) */
    public List<NotificationDTO> getUserNotifications(String userId) {
        try {
            List<Notification> notifications = repo.findByUserIdOrderByCreatedAtDesc(userId);
            if (notifications == null || notifications.isEmpty()) return Collections.emptyList();

            return notifications.stream()
                    .map(n -> {
                        try {
                            return mapper.toDto(n);
                        } catch (Exception e) {
                            logger.error("Failed to map notification {}: {}", n.getId(), e.getMessage(), e);
                            return null;
                        }
                    })
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            logger.error("Error fetching notifications for user {}: {}", userId, e.getMessage(), e);
            return Collections.emptyList();
        }
    }

    /** Create a notification and push it via SSE */
    public NotificationDTO createNotification(String userId, String type, String bookingId, String message) {
        try {
            Notification n = new Notification();
            n.setUserId(userId);
            n.setType(type);
            n.setBookingId(bookingId);
            n.setMessage(message);
            n = repo.save(n);

            NotificationDTO dto = mapper.toDto(n);
            sendNotification(userId, dto); // push to SSE
            return dto;
        } catch (Exception e) {
            logger.error("Failed to create notification for user {}: {}", userId, e.getMessage(), e);
            return null;
        }
    }

    /** Mark a notification as read */
    public void markAsRead(String notificationId) {
        Notification n = repo.findById(notificationId).orElseThrow(() -> new RuntimeException("Notification not found"));
        n.setRead(true);
        repo.save(n);
    }

    /** Internal: push notification to SSE emitters */
    private void sendNotification(String userId, NotificationDTO dto) {
        if (!emitters.containsKey(userId)) return;

        for (SseEmitter emitter : emitters.get(userId)) {
            try {
                emitter.send(dto);
            } catch (IOException e) {
                emitter.completeWithError(e);
                emitters.get(userId).remove(emitter);
            }
        }
    }
}
