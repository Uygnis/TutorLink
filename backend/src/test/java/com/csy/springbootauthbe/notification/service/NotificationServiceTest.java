package com.csy.springbootauthbe.notification.service;

import com.csy.springbootauthbe.notification.dto.NotificationDTO;
import com.csy.springbootauthbe.notification.entity.Notification;
import com.csy.springbootauthbe.notification.mapper.NotificationMapper;
import com.csy.springbootauthbe.notification.repository.NotificationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class NotificationServiceTest {

    @Mock
    private NotificationRepository repo;
    @Mock
    private NotificationMapper mapper;

    @InjectMocks
    private NotificationService service;

    @Captor
    private ArgumentCaptor<SseEmitter> emitterCaptor;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    // ✅ 1. registerEmitter — ensures emitter added and callbacks registered
    @Test
    void registerEmitter_addsEmitterAndRegistersCallbacks() {
        SseEmitter emitter = mock(SseEmitter.class);

        service.registerEmitter("U1", emitter);

        assertTrue(getEmitters("U1").contains(emitter));
        verify(emitter, times(1)).onCompletion(any());
        verify(emitter, times(1)).onTimeout(any());
    }

    // ✅ 2. getUserNotifications — normal successful mapping
    @Test
    void getUserNotifications_success_returnsDtos() {
        Notification n1 = new Notification();
        n1.setId("N1");
        NotificationDTO dto = new NotificationDTO();
        dto.setId("N1");

        when(repo.findByUserIdOrderByCreatedAtDesc("U1")).thenReturn(List.of(n1));
        when(mapper.toDto(n1)).thenReturn(dto);

        List<NotificationDTO> result = service.getUserNotifications("U1");

        assertEquals(1, result.size());
        assertEquals("N1", result.get(0).getId());
    }

    // ✅ 3. getUserNotifications — empty list returns empty
    @Test
    void getUserNotifications_whenEmpty_returnsEmptyList() {
        when(repo.findByUserIdOrderByCreatedAtDesc("U1")).thenReturn(Collections.emptyList());
        List<NotificationDTO> result = service.getUserNotifications("U1");
        assertTrue(result.isEmpty());
    }

    // ✅ 4. getUserNotifications — mapper throws exception, filters out null
    @Test
    void getUserNotifications_whenMapperThrows_skipsInvalid() {
        Notification n1 = new Notification();
        n1.setId("N1");
        when(repo.findByUserIdOrderByCreatedAtDesc("U1")).thenReturn(List.of(n1));
        when(mapper.toDto(n1)).thenThrow(new RuntimeException("Mapping failed"));

        List<NotificationDTO> result = service.getUserNotifications("U1");

        assertTrue(result.isEmpty());
    }

    // ✅ 5. getUserNotifications — repository throws exception, returns empty
    @Test
    void getUserNotifications_whenRepoThrows_returnsEmpty() {
        when(repo.findByUserIdOrderByCreatedAtDesc("U1"))
                .thenThrow(new RuntimeException("DB error"));
        List<NotificationDTO> result = service.getUserNotifications("U1");
        assertTrue(result.isEmpty());
    }

    // ✅ 6. createNotification — happy path
    @Test
    void createNotification_success_savesAndReturnsDto() {
        Notification saved = new Notification();
        saved.setId("N1");

        NotificationDTO dto = new NotificationDTO();
        dto.setId("N1");

        when(repo.save(any(Notification.class))).thenReturn(saved);
        when(mapper.toDto(saved)).thenReturn(dto);

        NotificationDTO result = service.createNotification("U1", "INFO", "B1", "Hello");

        assertNotNull(result);
        assertEquals("N1", result.getId());
        verify(repo, times(1)).save(any(Notification.class));
    }

    // ✅ 7. createNotification — repo throws exception, returns null
    @Test
    void createNotification_whenRepoThrows_returnsNull() {
        when(repo.save(any(Notification.class))).thenThrow(new RuntimeException("DB fail"));
        NotificationDTO result = service.createNotification("U1", "INFO", "B1", "Hello");
        assertNull(result);
    }

    // ✅ 8. markAsRead — finds and saves updated notification
    @Test
    void markAsRead_updatesNotificationAndSaves() {
        Notification n = new Notification();
        n.setId("N1");
        n.setRead(false);

        when(repo.findById("N1")).thenReturn(Optional.of(n));

        service.markAsRead("N1");

        assertTrue(n.isRead());
        verify(repo, times(1)).save(n);
    }

    // ✅ 9. markAsRead — notification not found throws exception
    @Test
    void markAsRead_whenNotFound_throwsRuntimeException() {
        when(repo.findById("N404")).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> service.markAsRead("N404"));
    }

    // ✅ 10. sendNotification — happy path (valid emitter)
    @Test
    void sendNotification_pushesToAllEmitters() throws Exception {
        // Arrange
        NotificationDTO dto = new NotificationDTO();
        dto.setId("N1");
        SseEmitter emitter = mock(SseEmitter.class);

        service.registerEmitter("U1", emitter);

        var sendMethod = NotificationService.class
                .getDeclaredMethod("sendNotification", String.class, NotificationDTO.class);
        sendMethod.setAccessible(true);

        // Act
        sendMethod.invoke(service, "U1", dto);

        // Assert
        verify(emitter, times(1)).send((Object) any()); // 👈 explicitly cast to Object
    }


    // ✅ 11. sendNotification — emitter throws IOException, should be removed
    @Test
    void sendNotification_whenEmitterFails_removesIt() throws Exception {
        SseEmitter badEmitter = mock(SseEmitter.class);
        doThrow(new IOException("stream closed")).when(badEmitter).send((Object) any());

        service.registerEmitter("U1", badEmitter);

        var sendMethod = NotificationService.class
                .getDeclaredMethod("sendNotification", String.class, NotificationDTO.class);
        sendMethod.setAccessible(true);

        NotificationDTO dto = new NotificationDTO();
        sendMethod.invoke(service, "U1", dto);

        assertFalse(getEmitters("U1").contains(badEmitter));
    }

    // ✅ 12. sendNotification — user has no emitters (no error)
    @Test
    void sendNotification_whenNoEmitters_doesNothing() throws Exception {
        var sendMethod = NotificationService.class
                .getDeclaredMethod("sendNotification", String.class, NotificationDTO.class);
        sendMethod.setAccessible(true);

        sendMethod.invoke(service, "U999", new NotificationDTO()); // no emitters registered
        // Should not throw
    }

    // Helper to access internal emitters for assertions
    @SuppressWarnings("unchecked")
    private CopyOnWriteArrayList<SseEmitter> getEmitters(String userId) {
        try {
            var field = NotificationService.class.getDeclaredField("emitters");
            field.setAccessible(true);
            var map = (Map<String, CopyOnWriteArrayList<SseEmitter>>) field.get(service);
            return map.computeIfAbsent(userId, k -> new CopyOnWriteArrayList<>());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
