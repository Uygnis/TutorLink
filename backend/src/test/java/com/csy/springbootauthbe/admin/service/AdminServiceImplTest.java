package com.csy.springbootauthbe.admin.service;

import com.csy.springbootauthbe.admin.dto.AdminDTO;
import com.csy.springbootauthbe.admin.dto.AdminDashboardDTO;
import com.csy.springbootauthbe.admin.entity.Admin;
import com.csy.springbootauthbe.admin.entity.Permissions;
import com.csy.springbootauthbe.admin.mapper.AdminMapper;
import com.csy.springbootauthbe.admin.repository.AdminRepository;
import com.csy.springbootauthbe.booking.dto.BookingDTO;
import com.csy.springbootauthbe.booking.service.BookingServiceImpl;
import com.csy.springbootauthbe.student.entity.Student;
import com.csy.springbootauthbe.student.repository.StudentRepository;
import com.csy.springbootauthbe.tutor.dto.TutorDTO;
import com.csy.springbootauthbe.tutor.entity.Tutor;
import com.csy.springbootauthbe.tutor.repository.TutorRepository;
import com.csy.springbootauthbe.user.entity.AccountStatus;
import com.csy.springbootauthbe.user.entity.Role;
import com.csy.springbootauthbe.user.entity.User;
import com.csy.springbootauthbe.user.repository.UserRepository;
import com.csy.springbootauthbe.user.utils.UserResponse;
import com.csy.springbootauthbe.wallet.service.WalletService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AdminServiceImplTest {

    @Mock private AdminMapper adminMapper;
    @Mock private AdminRepository adminRepository;
    @Mock private UserRepository userRepository;
    @Mock private StudentRepository studentRepository;
    @Mock private TutorRepository tutorRepository;
    @Mock private WalletService walletService;
    @Mock private BookingServiceImpl bookingService;

    @InjectMocks
    private AdminServiceImpl adminService;

    private User adminUser;
    private Admin adminProfile;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        adminUser = new User();
        adminUser.setId("A1");
        adminUser.setRole(Role.ADMIN);
        adminUser.setStatus(AccountStatus.ACTIVE);

        adminProfile = new Admin();
        adminProfile.setUserId("A1");
        adminProfile.setPermissions(List.of(Permissions.VIEW_STUDENTS, Permissions.VIEW_TUTORS, Permissions.VIEW_ADMIN, Permissions.DELETE_BOOKING));
    }

    // ------------------ Helpers ------------------

    @Test
    void testCheckAdminWithPermissionThrowsIfNotAdmin() {
        User notAdmin = new User();
        notAdmin.setId("U1");
        notAdmin.setRole(Role.STUDENT);

        when(userRepository.findById("U1")).thenReturn(Optional.of(notAdmin));

        RuntimeException ex = assertThrows(RuntimeException.class,
            () -> invokeCheckAdminWithPermission("U1", Permissions.VIEW_STUDENTS));
        assertTrue(ex.getMessage().contains("Not an admin"));
    }

    @Test
    void testCheckAdminWithPermissionThrowsIfMissingPermission() {
        adminProfile.setPermissions(List.of(Permissions.VIEW_TUTORS)); // Missing required
        when(userRepository.findById("A1")).thenReturn(Optional.of(adminUser));
        when(adminRepository.findByUserId("A1")).thenReturn(Optional.of(adminProfile));

        RuntimeException ex = assertThrows(RuntimeException.class,
            () -> invokeCheckAdminWithPermission("A1", Permissions.VIEW_STUDENTS));
        assertTrue(ex.getMessage().contains("Unauthorized"));
    }

    @Test
    void testGetUserOrThrow() {
        User student = new User();
        student.setId("S1");
        student.setRole(Role.STUDENT);
        when(userRepository.findById("S1")).thenReturn(Optional.of(student));

        User result = invokeGetUserOrThrow("S1", Role.STUDENT);
        assertEquals(student, result);
    }

    @Test
    void testGetUserOrThrowThrowsIfRoleMismatch() {
        User tutor = new User();
        tutor.setId("T1");
        tutor.setRole(Role.TUTOR);
        when(userRepository.findById("T1")).thenReturn(Optional.of(tutor));

        assertThrows(RuntimeException.class,
            () -> invokeGetUserOrThrow("T1", Role.STUDENT));
    }

    // ------------------ Student Management ------------------

    @Test
    void testViewStudents() {
        User studentUser = new User();
        studentUser.setId("S1");
        studentUser.setFirstname("Alice");
        studentUser.setLastname("Tan");
        studentUser.setEmail("alice@x.com");
        studentUser.setRole(Role.STUDENT);
        studentUser.setStatus(AccountStatus.ACTIVE);

        Student student = new Student();
        student.setStudentNumber("SN001");
        student.setGradeLevel("P5");

        when(userRepository.findById("A1")).thenReturn(Optional.of(adminUser));
        when(adminRepository.findByUserId("A1")).thenReturn(Optional.of(adminProfile));
        when(userRepository.findAllByRole(Role.STUDENT)).thenReturn(List.of(studentUser));
        when(studentRepository.findByUserId("S1")).thenReturn(Optional.of(student));

        List<UserResponse> responses = adminService.viewStudents("A1");

        assertEquals(1, responses.size());
        assertEquals("Alice Tan", responses.get(0).getName());
        assertEquals(Role.STUDENT, responses.get(0).getRole());
    }

    @Test
    void testSuspendStudent() {
        User student = new User();
        student.setId("S1");
        student.setRole(Role.STUDENT);

        when(userRepository.findById("A1")).thenReturn(Optional.of(adminUser));
        when(adminRepository.findByUserId("A1")).thenReturn(Optional.of(adminProfile));
        when(userRepository.findById("S1")).thenReturn(Optional.of(student));

        String result = adminService.suspendStudent("A1", "S1");
        assertEquals("S1", result);
        verify(userRepository).save(student);
    }

    @Test
    void testActivateStudent() {
        User student = new User();
        student.setId("S1");
        student.setRole(Role.STUDENT);

        when(userRepository.findById("A1")).thenReturn(Optional.of(adminUser));
        when(adminRepository.findByUserId("A1")).thenReturn(Optional.of(adminProfile));
        when(userRepository.findById("S1")).thenReturn(Optional.of(student));

        String result = adminService.activateStudent("A1", "S1");
        assertEquals("S1", result);
        verify(userRepository).save(student);
    }

    @Test
    void testDeleteStudent() {
        User student = new User();
        student.setId("S1");
        student.setRole(Role.STUDENT);

        adminProfile.setPermissions(List.of(Permissions.DELETE_STUDENT));
        when(userRepository.findById("A1")).thenReturn(Optional.of(adminUser));
        when(adminRepository.findByUserId("A1")).thenReturn(Optional.of(adminProfile));
        when(userRepository.findById("S1")).thenReturn(Optional.of(student));

        String result = adminService.deleteStudent("A1", "S1");
        assertEquals("S1", result);
        verify(userRepository).save(student);
    }

    // ------------------ Tutor Management ------------------

    @Test
    void testViewTutors() {
        User tutorUser = new User();
        tutorUser.setId("T1");
        tutorUser.setFirstname("Bob");
        tutorUser.setLastname("Lim");
        tutorUser.setEmail("bob@x.com");
        tutorUser.setRole(Role.TUTOR);
        tutorUser.setStatus(AccountStatus.ACTIVE);

        Tutor tutor = new Tutor();
        tutor.setSubject("Math");
        tutor.setHourlyRate(40.0);

        when(userRepository.findById("A1")).thenReturn(Optional.of(adminUser));
        when(adminRepository.findByUserId("A1")).thenReturn(Optional.of(adminProfile));
        when(userRepository.findAllByRole(Role.TUTOR)).thenReturn(List.of(tutorUser));
        when(tutorRepository.findByUserId("T1")).thenReturn(Optional.of(tutor));

        List<TutorDTO> tutors = adminService.viewTutors("A1");
        assertEquals(1, tutors.size());
        assertEquals("Math", tutors.get(0).getSubject());
    }

    @Test
    void testApproveTutorThrowsIfTutorNotFound() {
        when(userRepository.findById("A1")).thenReturn(Optional.of(adminUser));
        when(adminRepository.findByUserId("A1")).thenReturn(Optional.of(adminProfile));
        when(userRepository.findById("T1")).thenReturn(Optional.of(new User()));
        when(tutorRepository.findByUserId("T1")).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> adminService.approveTutor("A1", "T1"));
    }

    // ------------------ Admin Management ------------------

    @Test
    void testGetAdminByUserId() {
        User user = new User();
        user.setId("U1");
        user.setFirstname("Admin");
        user.setLastname("User");
        user.setEmail("admin@x.com");
        user.setRole(Role.ADMIN);
        user.setStatus(AccountStatus.ACTIVE);

        Admin admin = new Admin();
        admin.setUserId("U1");
        admin.setPermissions(List.of(Permissions.VIEW_TUTORS));

        when(userRepository.findById("U1")).thenReturn(Optional.of(user));
        when(adminRepository.findByUserId("U1")).thenReturn(Optional.of(admin));

        Optional<AdminDTO> result = adminService.getAdminByUserId("U1");
        assertTrue(result.isPresent());
        assertEquals("U1", result.get().getUserId());
    }

    @Test
    void testCreateAdmin() {
        AdminDTO dto = new AdminDTO();
        Admin admin = new Admin();
        when(adminMapper.toEntity(dto)).thenReturn(admin);
        when(adminRepository.save(admin)).thenReturn(admin);
        when(adminMapper.toDTO(admin)).thenReturn(new AdminDTO());

        adminService.createAdmin(dto);
        verify(adminMapper).toEntity(dto);
        verify(adminRepository).save(admin);
    }

    @Test
    void testDeleteBooking() {
        BookingDTO booking = new BookingDTO();
        when(userRepository.findById("A1")).thenReturn(Optional.of(adminUser));
        when(adminRepository.findByUserId("A1")).thenReturn(Optional.of(adminProfile));
        when(bookingService.deleteBooking("B1")).thenReturn(booking);

        BookingDTO result = adminService.deleteBooking("A1", "B1");
        assertEquals(booking, result);
    }

    @Test
    void testGetDashboardSummary() {
        when(userRepository.findById("A1")).thenReturn(Optional.of(adminUser));
        when(adminRepository.findByUserId("A1")).thenReturn(Optional.of(adminProfile));

        when(userRepository.findAll()).thenReturn(List.of(adminUser));
        when(userRepository.findAllByRole(Role.TUTOR)).thenReturn(List.of(adminUser));
        when(userRepository.findAllByRole(Role.STUDENT)).thenReturn(List.of(adminUser));
        when(userRepository.findAllByRole(Role.ADMIN)).thenReturn(List.of(adminUser));
        when(tutorRepository.count()).thenReturn(1L);
        when(studentRepository.count()).thenReturn(1L);
        when(adminRepository.count()).thenReturn(1L);
        when(walletService.getTransactionMetrics()).thenReturn(new AdminDashboardDTO.TransactionMetrics());

        AdminDashboardDTO dashboard = adminService.getDashboardSummary("A1");

        assertNotNull(dashboard);
        assertEquals(1, dashboard.getTotalTutors());
    }

    // ------------------ Private Helper Invokers ------------------

    private void invokeCheckAdminWithPermission(String userId, Permissions permission) {
        try {
            var method = AdminServiceImpl.class
                .getDeclaredMethod("checkAdminWithPermission", String.class, Permissions[].class);
            method.setAccessible(true);
            method.invoke(adminService, userId, new Permissions[]{permission});
        } catch (Exception e) {
            if (e.getCause() instanceof RuntimeException re) throw re;
        }
    }

    private User invokeGetUserOrThrow(String userId, Role role) {
        try {
            var method = AdminServiceImpl.class
                .getDeclaredMethod("getUserOrThrow", String.class, Role.class);
            method.setAccessible(true);
            return (User) method.invoke(adminService, userId, role);
        } catch (Exception e) {
            if (e.getCause() instanceof RuntimeException re) throw re;
            return null;
        }
    }
}
