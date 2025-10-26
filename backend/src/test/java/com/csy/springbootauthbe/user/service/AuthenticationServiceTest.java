package com.csy.springbootauthbe.user.service;

import com.csy.springbootauthbe.config.JWTService;
import com.csy.springbootauthbe.student.dto.StudentDTO;
import com.csy.springbootauthbe.student.service.StudentService;
import com.csy.springbootauthbe.tutor.dto.TutorDTO;
import com.csy.springbootauthbe.tutor.service.TutorService;
import com.csy.springbootauthbe.user.entity.Role;
import com.csy.springbootauthbe.user.entity.User;
import com.csy.springbootauthbe.user.repository.UserRepository;
import com.csy.springbootauthbe.user.utils.AuthenticationResponse;
import com.csy.springbootauthbe.user.utils.LoginRequest;
import com.csy.springbootauthbe.user.utils.RegisterRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthenticationServiceTest {

    @Mock UserRepository repository;
    @Mock PasswordEncoder passwordEncoder;
    @Mock JWTService jwtService;
    @Mock AuthenticationManager authenticationManager;
    @Mock StudentService studentService;
    @Mock TutorService tutorService;

    @InjectMocks AuthenticationService authService;

    // ---------- REGISTER: STUDENT ----------
    @Test
    void register_student_createsUser_andStudent_andReturnsToken() {
        RegisterRequest req = mockStudentRegisterRequest();
        when(repository.existsByEmail("alice@student.com")).thenReturn(false);
        when(passwordEncoder.encode("Secret123!")).thenReturn("hashed");
        when(jwtService.generateToken(any(User.class))).thenReturn("jwt-student");
        when(repository.save(any(User.class))).thenAnswer(inv -> {
            User u = inv.getArgument(0);
            u.setId("U123"); // simulate DB-set id
            return u;
        });

        AuthenticationResponse resp = authService.register(req);

        assertNotNull(resp);
        assertEquals("jwt-student", resp.getUser().getToken());
        assertEquals("U123", resp.getUser().getId());
        assertEquals(Role.STUDENT, resp.getUser().getRole());

        // password encoded
        verify(passwordEncoder).encode("Secret123!");
        // student was created with the new user's id
        ArgumentCaptor<StudentDTO> cap = ArgumentCaptor.forClass(StudentDTO.class);
        verify(studentService).createStudent(cap.capture());
        assertEquals("U123", cap.getValue().getUserId());
        // tutor path not called
        verify(tutorService, never()).createTutor(any());
    }

    // ---------- REGISTER: TUTOR ----------
    @Test
    void register_tutor_createsUser_andTutor_andReturnsToken() {
        RegisterRequest req = mockTutorRegisterRequest();
        when(repository.existsByEmail("bob@tutor.com")).thenReturn(false);
        when(passwordEncoder.encode("TopSecret!")).thenReturn("hashed2");
        when(jwtService.generateToken(any(User.class))).thenReturn("jwt-tutor");
        when(repository.save(any(User.class))).thenAnswer(inv -> {
            User u = inv.getArgument(0);
            u.setId("U999");
            return u;
        });

        AuthenticationResponse resp = authService.register(req);

        assertEquals("jwt-tutor", resp.getUser().getToken());
        assertEquals("U999", resp.getUser().getId());
        assertEquals(Role.TUTOR, resp.getUser().getRole());
        verify(studentService, never()).createStudent(any());

        ArgumentCaptor<TutorDTO> cap = ArgumentCaptor.forClass(TutorDTO.class);
        verify(tutorService).createTutor(cap.capture());
        assertEquals("U999", cap.getValue().getUserId());
    }

    // ---------- REGISTER: DUPLICATE EMAIL ----------
    @Test
    void register_duplicateEmail_throwsDataIntegrityViolationException() {
        RegisterRequest req = mock(RegisterRequest.class);
        when(req.getEmail()).thenReturn("dup@example.com");

        when(repository.existsByEmail("dup@example.com")).thenReturn(true);

        assertThrows(DataIntegrityViolationException.class, () -> authService.register(req));
        verify(repository, never()).save(any());
    }

    // ---------- REGISTER: INVALID ROLE ----------
    @Test
    void register_invalidRole_throwsIllegalArgumentException() {
        RegisterRequest req = mock(RegisterRequest.class);
        when(req.getEmail()).thenReturn("x@example.com");
        when(req.getRole()).thenReturn("NotARole"); // triggers invalid role path

        when(repository.existsByEmail("x@example.com")).thenReturn(false);

        assertThrows(IllegalArgumentException.class, () -> authService.register(req));
        verify(repository, never()).save(any());
    }

    // ---------- LOGIN: HAPPY PATH ----------
    @Test
    void login_valid_authenticates_andReturnsToken() {
        LoginRequest req = mock(LoginRequest.class);
        when(req.getEmail()).thenReturn("login@ok.com");
        when(req.getPassword()).thenReturn("pw");

        User user = new User();
        user.setId("U77");
        user.setFirstname("Log");
        user.setLastname("In");
        user.setEmail("login@ok.com");
        user.setRole(Role.USER);

        when(authenticationManager.authenticate(any()))
                .thenReturn(new UsernamePasswordAuthenticationToken("login@ok.com", "pw"));
        when(repository.findByEmail("login@ok.com")).thenReturn(Optional.of(user));
        when(jwtService.generateToken(user)).thenReturn("jwt-login");

        AuthenticationResponse resp = authService.login(req);

        assertNotNull(resp);
        assertEquals("jwt-login", resp.getUser().getToken());
        assertEquals("U77", resp.getUser().getId());
        assertEquals("Log In", resp.getUser().getName());
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
    }

    // ---------- LOGIN: USER NOT FOUND ----------
    @Test
    void login_userNotFound_throwsNoSuchElementException() {
        LoginRequest req = mock(LoginRequest.class);
        when(req.getEmail()).thenReturn("nobody@nowhere.com");
        when(req.getPassword()).thenReturn("pw");

        when(authenticationManager.authenticate(any()))
                .thenReturn(new UsernamePasswordAuthenticationToken("nobody@nowhere.com", "pw"));
        when(repository.findByEmail("nobody@nowhere.com")).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> authService.login(req));
    }

    // ---- helpers ----
    private RegisterRequest mockStudentRegisterRequest() {
        RegisterRequest req = mock(RegisterRequest.class);
        when(req.getEmail()).thenReturn("alice@student.com");
        when(req.getPassword()).thenReturn("Secret123!");
        when(req.getFirstname()).thenReturn("Alice");
        when(req.getLastname()).thenReturn("Student");
        when(req.getRole()).thenReturn("Student");
        when(req.getStudentNumber()).thenReturn("S-001");
        when(req.getGradeLevel()).thenReturn("G10");
        return req;
    }

    private RegisterRequest mockTutorRegisterRequest() {
        RegisterRequest req = mock(RegisterRequest.class);
        when(req.getEmail()).thenReturn("bob@tutor.com");
        when(req.getPassword()).thenReturn("TopSecret!");
        when(req.getFirstname()).thenReturn("Bob");
        when(req.getLastname()).thenReturn("Tutor");
        when(req.getRole()).thenReturn("Tutor");
        return req;
    }
}
