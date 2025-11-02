package com.csy.springbootauthbe.admin.controller;

import com.csy.springbootauthbe.admin.dto.AdminDTO;
import com.csy.springbootauthbe.admin.dto.AdminDashboardDTO;
import com.csy.springbootauthbe.admin.service.AdminService;
import com.csy.springbootauthbe.booking.dto.BookingDTO;
import com.csy.springbootauthbe.tutor.dto.TutorDTO;
import com.csy.springbootauthbe.user.entity.Role;
import com.csy.springbootauthbe.user.utils.UserResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AdminControllerTest {

    @Mock
    private AdminService adminService;

    @InjectMocks
    private AdminController adminController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetAdminByUserId_Found() {
        AdminDTO dto = new AdminDTO();
        when(adminService.getAdminByUserId("user1")).thenReturn(Optional.of(dto));

        ResponseEntity<AdminDTO> response = adminController.getAdminByUserId("user1");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getStatusCode().is2xxSuccessful());
        assertEquals(dto, response.getBody());
    }

    @Test
    void testGetAdminByUserId_NotFound() {
        when(adminService.getAdminByUserId("u2")).thenReturn(Optional.empty());

        ResponseEntity<AdminDTO> response = adminController.getAdminByUserId("u2");

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    void testGetDashboardSummary() {
        AdminDashboardDTO dashboard = new AdminDashboardDTO();
        when(adminService.getDashboardSummary("a1")).thenReturn(dashboard);

        ResponseEntity<AdminDashboardDTO> response = adminController.getDashboardSummary("a1");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(dashboard, response.getBody());
    }

    @Test
    void testSuspendAdmin() {
        when(adminService.suspendAdmin("a1", "u1")).thenReturn("u1");

        ResponseEntity<UserResponse> response = adminController.suspendAdmin("a1", "u1");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(Role.ADMIN, Objects.requireNonNull(response.getBody()).getRole());
        assertEquals("u1", response.getBody().getId());
    }

    @Test
    void testActivateAdmin() {
        when(adminService.activateAdmin("a1", "u1")).thenReturn("u1");

        ResponseEntity<UserResponse> response = adminController.activateAdmin("a1", "u1");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(Role.ADMIN, Objects.requireNonNull(response.getBody()).getRole());
        assertEquals("u1", response.getBody().getId());
    }

    @Test
    void testDeleteAdmin() {
        when(adminService.deleteAdmin("a1", "u1")).thenReturn("u1");

        ResponseEntity<UserResponse> response = adminController.deleteAdmin("a1", "u1");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("u1", Objects.requireNonNull(response.getBody()).getId());
        assertEquals(Role.ADMIN, response.getBody().getRole());
    }

    @Test
    void testGetAllTutors() {
        List<TutorDTO> tutors = List.of(new TutorDTO());
        when(adminService.viewTutors("a1")).thenReturn(tutors);

        ResponseEntity<List<TutorDTO>> response = adminController.getAllTutors("a1");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(tutors, response.getBody());
    }

    @Test
    void testGetAllStudents() {
        List<UserResponse> students = List.of(UserResponse.builder().id("s1").build());
        when(adminService.viewStudents("a1")).thenReturn(students);

        ResponseEntity<List<UserResponse>> response = adminController.getAllStudents("a1");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(students, response.getBody());
    }

    @Test
    void testDeleteBooking() {
        BookingDTO dto = new BookingDTO();
        when(adminService.deleteBooking("a1", "b1")).thenReturn(dto);

        ResponseEntity<BookingDTO> response = adminController.deleteBookingById("a1", "b1");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(dto, response.getBody());
    }
}
