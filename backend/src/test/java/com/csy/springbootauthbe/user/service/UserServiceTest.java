package com.csy.springbootauthbe.user.service;

import com.csy.springbootauthbe.user.entity.Role;
import com.csy.springbootauthbe.user.entity.User;
import com.csy.springbootauthbe.user.repository.UserRepository;
import com.csy.springbootauthbe.user.utils.RegisterRequest;
import com.csy.springbootauthbe.user.utils.UserResponse;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock UserRepository userRepository;
    @Mock PasswordEncoder passwordEncoder;

    @InjectMocks UserService userService;

    @AfterEach
    void clearContext() {
        SecurityContextHolder.clearContext();
    }

    // ---------------- getCurrentAdmin ----------------
    @Test
    void getCurrentAdmin_adminUser_returnsUserResponse() {
        // Arrange: put admin email into SecurityContext
        setAuthEmail("admin@example.com");

        User admin = user("A1", "Alice", "Admin", "admin@example.com", Role.ADMIN);
        when(userRepository.findByEmail("admin@example.com")).thenReturn(Optional.of(admin));

        // Act
        UserResponse resp = userService.getCurrentAdmin();

        // Assert
        assertEquals("A1", resp.getId());
        assertEquals("Alice Admin", resp.getName());
        assertEquals("admin@example.com", resp.getEmail());
        assertEquals(Role.ADMIN, resp.getRole());
    }

    @Test
    void getCurrentAdmin_nonAdmin_throwsAccessDenied() {
        setAuthEmail("user@example.com");
        when(userRepository.findByEmail("user@example.com"))
                .thenReturn(Optional.of(user("U1", "Uni", "User", "user@example.com", Role.USER)));

        assertThrows(AccessDeniedException.class, () -> userService.getCurrentAdmin());
    }

    // ---------------- getCurrentUser ----------------
    @Test
    void getCurrentUser_anyKnownRole_returnsUser() {
        setAuthEmail("tutor@example.com");
        when(userRepository.findByEmail("tutor@example.com"))
                .thenReturn(Optional.of(user("T1", "Tina", "Tutor", "tutor@example.com", Role.TUTOR)));

        UserResponse resp = userService.getCurrentUser();

        assertEquals("T1", resp.getId());
        assertEquals(Role.TUTOR, resp.getRole());
    }

    @Test
    void getCurrentUser_notFound_throws() {
        setAuthEmail("missing@example.com");
        when(userRepository.findByEmail("missing@example.com")).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> userService.getCurrentUser());
    }

    // ---------------- getCurrentStudent ----------------
    @Test
    void getCurrentStudent_studentRole_ok() {
        setAuthEmail("stud@example.com");
        when(userRepository.findByEmail("stud@example.com"))
                .thenReturn(Optional.of(user("S1", "Stu", "Dent", "stud@example.com", Role.STUDENT)));

        UserResponse resp = userService.getCurrentStudent();

        assertEquals("S1", resp.getId());
        assertEquals(Role.STUDENT, resp.getRole());
    }

    @Test
    void getCurrentStudent_wrongRole_throwsAccessDenied() {
        setAuthEmail("notstudent@example.com");
        when(userRepository.findByEmail("notstudent@example.com"))
                .thenReturn(Optional.of(user("X1", "No", "Student", "notstudent@example.com", Role.USER)));

        assertThrows(AccessDeniedException.class, () -> userService.getCurrentStudent());
    }

    // ---------------- getAllAdmins ----------------
    @Test
    void getAllAdmins_mapsToUserResponse() {
        when(userRepository.findAllByRole(Role.ADMIN)).thenReturn(List.of(
                user("A1", "Alice", "Admin", "a@e.com", Role.ADMIN),
                user("A2", "Andy", "Admin", "b@e.com", Role.ADMIN)
        ));

        var list = userService.getAllAdmins();
        assertEquals(2, list.size());
        assertEquals("Alice Admin", list.get(0).getName());
        assertEquals(Role.ADMIN, list.get(0).getRole());
    }

    // ---------------- getUserById ----------------
    @Test
    void getUserById_found_returnsResponse() {
        when(userRepository.findById("U7"))
                .thenReturn(Optional.of(user("U7", "Carl", "User", "c@e.com", Role.USER)));

        UserResponse resp = userService.getUserById("U7");
        assertEquals("U7", resp.getId());
        assertEquals("Carl User", resp.getName());
    }

    @Test
    void getUserById_notFound_throws() {
        when(userRepository.findById("NOPE")).thenReturn(Optional.empty());
        assertThrows(UsernameNotFoundException.class, () -> userService.getUserById("NOPE"));
    }

    // ---------------- updateUser ----------------
    @Test
    void updateUser_updatesEmail_only_whenPasswordBlank() {
        var existing = user("U1", "Eva", "User", "old@e.com", Role.USER);
        when(userRepository.findById("U1")).thenReturn(Optional.of(existing));

        RegisterRequest req = mock(RegisterRequest.class);
        when(req.getEmail()).thenReturn("new@e.com");
        when(req.getPassword()).thenReturn(""); // blank => no encode

        var resp = userService.updateUser("U1", req);

        assertEquals("new@e.com", resp.getEmail());
        verify(passwordEncoder, never()).encode(anyString());
        verify(userRepository).save(existing);
    }

    @Test
    void updateUser_updatesEmail_andEncodesPassword_whenProvided() {
        var existing = user("U2", "Phil", "User", "old@e.com", Role.USER);
        when(userRepository.findById("U2")).thenReturn(Optional.of(existing));

        RegisterRequest req = mock(RegisterRequest.class);
        when(req.getEmail()).thenReturn("new@e.com");
        when(req.getPassword()).thenReturn("Secret!");
        when(passwordEncoder.encode("Secret!")).thenReturn("ENC");

        var resp = userService.updateUser("U2", req);

        assertEquals("new@e.com", resp.getEmail());
        verify(passwordEncoder).encode("Secret!");
        verify(userRepository).save(existing);
    }

    @Test
    void updateUser_missingUser_throws() {
        when(userRepository.findById("UNKNOWN")).thenReturn(Optional.empty());
        RegisterRequest req = mock(RegisterRequest.class);
        assertThrows(UsernameNotFoundException.class, () -> userService.updateUser("UNKNOWN", req));
    }

    // ---------------- deleteUser ----------------
    @Test
    void deleteUser_found_deletes() {
        var u = user("U9", "Del", "User", "d@e.com", Role.USER);
        when(userRepository.findById("U9")).thenReturn(Optional.of(u));

        userService.deleteUser("U9");

        verify(userRepository).delete(u);
    }

    @Test
    void deleteUser_notFound_throws() {
        when(userRepository.findById("BAD")).thenReturn(Optional.empty());
        assertThrows(UsernameNotFoundException.class, () -> userService.deleteUser("BAD"));
    }

    // ---------------- helpers ----------------
    private static void setAuthEmail(String email) {
        TestingAuthenticationToken auth =
                new TestingAuthenticationToken(email, "x"); // principal name = email
        auth.setAuthenticated(true);
        SecurityContext ctx = new org.springframework.security.core.context.SecurityContextImpl(auth);
        SecurityContextHolder.setContext(ctx);
    }

    private static User user(String id, String first, String last, String email, Role role) {
        User u = new User();
        u.setId(id);
        u.setFirstname(first);
        u.setLastname(last);
        u.setEmail(email);
        u.setRole(role);
        return u;
    }
}
