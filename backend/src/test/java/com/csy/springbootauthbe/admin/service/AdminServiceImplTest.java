package com.csy.springbootauthbe.admin.service;

import com.csy.springbootauthbe.admin.entity.*;
import com.csy.springbootauthbe.admin.mapper.AdminMapper;
import com.csy.springbootauthbe.admin.repository.AdminRepository;
import com.csy.springbootauthbe.booking.dto.BookingDTO;
import com.csy.springbootauthbe.booking.service.BookingServiceImpl;
import com.csy.springbootauthbe.student.dto.StudentDTO;
import com.csy.springbootauthbe.student.repository.StudentRepository;
import com.csy.springbootauthbe.tutor.repository.TutorRepository;
import com.csy.springbootauthbe.user.entity.*;
import com.csy.springbootauthbe.user.repository.UserRepository;
import com.csy.springbootauthbe.wallet.service.WalletService;
import org.junit.jupiter.api.*;
import org.mockito.*;
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
    @InjectMocks private AdminServiceImpl adminService;

    private Admin adminProfile;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        User mockAdmin = new User();
        mockAdmin.setId("admin1");
        mockAdmin.setRole(Role.ADMIN);
        mockAdmin.setStatus(AccountStatus.ACTIVE);

        adminProfile = Admin.builder()
            .id("1").userId("admin1")
            .permissions(List.of(Permissions.VIEW_STUDENTS, Permissions.DELETE_BOOKING))
            .build();

        when(userRepository.findById("admin1")).thenReturn(Optional.of(mockAdmin));
        when(adminRepository.findByUserId("admin1")).thenReturn(Optional.of(adminProfile));
    }

    @Test
    void testViewStudents_Success() {
        User student = new User();
        student.setId("s1");
        student.setFirstname("John");
        student.setLastname("Doe");
        student.setEmail("j@x.com");
        student.setRole(Role.STUDENT);
        student.setStatus(AccountStatus.ACTIVE);

        when(userRepository.findAllByRole(Role.STUDENT)).thenReturn(List.of(student));

        var responses = adminService.viewStudents("admin1");

        assertEquals(1, responses.size());
        assertEquals("s1", responses.get(0).getId());
        verify(userRepository).findAllByRole(Role.STUDENT);
    }

    @Test
    void testViewStudentDetail_Found() {
        User user = new User();
        user.setId("u1");
        user.setFirstname("Jane");
        user.setLastname("Doe");
        user.setEmail("jane@x.com");
        user.setStatus(AccountStatus.ACTIVE);
        when(userRepository.findById("u1")).thenReturn(Optional.of(user));
        when(studentRepository.findByUserId("u1")).thenReturn(Optional.of(
            com.csy.springbootauthbe.student.entity.Student.builder()
                .studentNumber("SN1").gradeLevel("A1").build()
        ));

        Optional<StudentDTO> result = adminService.viewStudentDetail("u1");
        assertTrue(result.isPresent());
        assertEquals("Jane", result.get().getFirstName());
    }

    @Test
    void testSuspendStudent_ChangesStatus() {
        User student = new User();
        student.setId("s1");
        student.setRole(Role.STUDENT);
        student.setStatus(AccountStatus.ACTIVE);
        when(userRepository.findById("s1")).thenReturn(Optional.of(student));
        adminProfile.setPermissions(List.of(Permissions.SUSPEND_STUDENT));

        String result = adminService.suspendStudent("admin1", "s1");
        assertEquals("s1", result);
        verify(userRepository).save(any(User.class));
    }

    @Test
    void testActivateStudent_ChangesStatus() {
        User student = new User();
        student.setId("s1");
        student.setRole(Role.STUDENT);
        student.setStatus(AccountStatus.SUSPENDED);
        when(userRepository.findById("s1")).thenReturn(Optional.of(student));
        adminProfile.setPermissions(List.of(Permissions.SUSPEND_STUDENT));

        String result = adminService.activateStudent("admin1", "s1");
        assertEquals("s1", result);
        verify(userRepository).save(any(User.class));
    }

    @Test
    void testDeleteBooking_CallsBookingService() {
        BookingDTO booking = new BookingDTO();
        when(bookingService.deleteBooking("b1")).thenReturn(booking);
        BookingDTO result = adminService.deleteBooking("admin1", "b1");
        assertEquals(booking, result);
        verify(bookingService).deleteBooking("b1");
    }

    @Test
    void testCheckAdminWithPermission_MissingPermission_ThrowsException() {
        adminProfile.setPermissions(List.of(Permissions.VIEW_STUDENTS));
        assertThrows(RuntimeException.class, () ->
            adminService.deleteBooking("admin1", "b1")
        );
    }

    @Test
    void testGetUserOrThrow_NotFound() {
        when(userRepository.findById("notfound")).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () ->
            adminService.deleteStudent("admin1", "notfound")
        );
    }
}
