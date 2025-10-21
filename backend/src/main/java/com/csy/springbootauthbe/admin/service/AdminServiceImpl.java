package com.csy.springbootauthbe.admin.service;

import com.csy.springbootauthbe.admin.dto.AdminDTO;
import com.csy.springbootauthbe.admin.entity.Admin;
import com.csy.springbootauthbe.admin.entity.Permissions;
import com.csy.springbootauthbe.admin.mapper.AdminMapper;
import com.csy.springbootauthbe.admin.repository.AdminRepository;
import com.csy.springbootauthbe.admin.util.AdminResponse;
import com.csy.springbootauthbe.student.dto.StudentDTO;
import com.csy.springbootauthbe.student.repository.StudentRepository;
import com.csy.springbootauthbe.student.utils.StudentResponse;
import com.csy.springbootauthbe.tutor.dto.TutorDTO;
import com.csy.springbootauthbe.tutor.repository.TutorRepository;
import com.csy.springbootauthbe.user.entity.AccountStatus;
import com.csy.springbootauthbe.user.entity.Role;
import com.csy.springbootauthbe.user.entity.User;
import com.csy.springbootauthbe.user.repository.UserRepository;
import com.csy.springbootauthbe.user.utils.UserResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {

    private final AdminMapper adminMapper;
    private final AdminRepository adminRepository;
    private final UserRepository userRepository;
    private final StudentRepository studentRepository;
    private final TutorRepository tutorRepository;


    // -------------------------------
    // Student Management
    // -------------------------------
    @Override
    public List<UserResponse> viewStudents(String adminUserId) {
        checkAdminWithPermission(adminUserId, Permissions.VIEW_STUDENTS);
        List<User> students = userRepository.findAllByRole(Role.STUDENT);
        return students.stream()
            .map(user -> {
                UserResponse.UserResponseBuilder builder = UserResponse.builder()
                    .id(user.getId())
                    .name(user.getFirstname() + " " + user.getLastname())
                    .email(user.getEmail())
                    .role(user.getRole())
                    .status(user.getStatus());

                    studentRepository.findByUserId(user.getId()).ifPresent(student ->
                        builder.student(StudentResponse.builder()
                            .studentNumber(student.getStudentNumber())
                            .gradeLevel(student.getGradeLevel())
                            .build()
                        )
                    );
                return builder.build();
            })
            .toList();

    }

    @Override
    public Optional<StudentDTO> viewStudentDetail(String studentId) {
        return userRepository.findById(studentId)
                .flatMap(user ->
                        // Find the Tutor by userId
                        studentRepository.findByUserId(user.getId())
                                .map(student -> StudentDTO.builder()
                                        .userId(user.getId())
                                        .firstName(user.getFirstname())
                                        .lastName(user.getLastname())
                                        .email(user.getEmail())
                                        .status(String.valueOf(user.getStatus()))
                                        .studentNumber(student.getStudentNumber())
                                        .gradeLevel(student.getGradeLevel())
                                        .profileImageUrl(student.getProfileImageUrl())
                                        .build()
                                )
                );
    }

    @Override
    public String suspendStudent(String adminUserId, String studentId) {
        checkAdminWithPermission(adminUserId, Permissions.SUSPEND_STUDENT);
        User student = getUserOrThrow(studentId, Role.STUDENT);
        student.setStatus(AccountStatus.SUSPENDED);
        userRepository.save(student);
        return studentId;
    }

    @Override
    public String activateStudent(String adminUserId, String studentId) {
        checkAdminWithPermission(adminUserId, Permissions.SUSPEND_STUDENT);
        User student = getUserOrThrow(studentId, Role.STUDENT);
        student.setStatus(AccountStatus.ACTIVE);
        userRepository.save(student);
        return studentId;
    }

    @Override
    public String deleteStudent(String adminUserId, String studentId) {
        checkAdminWithPermission(adminUserId, Permissions.DELETE_STUDENT);
        User student = getUserOrThrow(studentId, Role.STUDENT);
        student.setStatus(AccountStatus.DELETED);
        userRepository.save(student);
        return studentId;
    }

    // -------------------------------
    // Tutor Management
    // -------------------------------
    @Override
    public List<TutorDTO> viewTutors(String adminUserId) {
        checkAdminWithPermission(adminUserId, Permissions.VIEW_TUTORS);
        List<User> tutors = userRepository.findAllByRole(Role.TUTOR);

        return tutors.stream()
            .map(user -> tutorRepository.findByUserId(user.getId())
                .map(tutor -> TutorDTO.builder()
                    .userId(user.getId())
                    .firstName(user.getFirstname())
                    .lastName(user.getLastname())
                    .email(user.getEmail())
                    .status(String.valueOf(user.getStatus()))
                    .subject(tutor.getSubject())
                    .hourlyRate(tutor.getHourlyRate())
                    .availability(tutor.getAvailability())
                    .description(tutor.getDescription())
                    .lessonType(tutor.getLessonType())
                    .profileImageUrl(tutor.getProfileImageUrl())
                    .qualifications(tutor.getQualifications())
                    .build()
                )
                .orElse(null) // or skip if tutor not found
            )
            .filter(Objects::nonNull)
            .toList();
    }

    @Override
    public Optional<TutorDTO> viewTutorDetail(String tutorId) {
        return userRepository.findById(tutorId)
            .flatMap(user ->
                // Find the Tutor by userId
                tutorRepository.findByUserId(user.getId())
                    .map(tutor -> TutorDTO.builder()
                        .userId(user.getId())
                        .firstName(user.getFirstname())
                        .lastName(user.getLastname())
                        .email(user.getEmail())
                        .status(String.valueOf(user.getStatus()))
                        .subject(tutor.getSubject())
                        .hourlyRate(tutor.getHourlyRate())
                        .availability(tutor.getAvailability())
                        .description(tutor.getDescription())
                        .lessonType(tutor.getLessonType())
                        .profileImageUrl(tutor.getProfileImageUrl())
                        .qualifications(tutor.getQualifications())
                        .build()
                    )
            );
    }

    @Override
    public String approveTutor(String adminUserId, String tutorId) {
        checkAdminWithPermission(adminUserId, Permissions.APPROVE_TUTOR);
        User tutor = getUserOrThrow(tutorId, Role.TUTOR);
        tutor.setStatus(AccountStatus.ACTIVE);
        userRepository.save(tutor);
        return adminUserId;
    }

    @Override
    public String rejectTutor(String adminUserId, String tutorId) {
        checkAdminWithPermission(adminUserId, Permissions.REJECT_TUTOR);
        User tutor = getUserOrThrow(tutorId, Role.TUTOR);
        tutor.setStatus(AccountStatus.REJECTED);
        userRepository.save(tutor);
        return adminUserId;
    }

    @Override
    public String suspendTutor(String adminUserId, String tutorId) {
        checkAdminWithPermission(adminUserId, Permissions.SUSPEND_TUTOR);
        User tutor = getUserOrThrow(tutorId, Role.TUTOR);
        tutor.setStatus(AccountStatus.SUSPENDED);
        userRepository.save(tutor);
        return tutorId;
    }

    @Override
    public String activateTutor(String adminUserId, String tutorId) {
        checkAdminWithPermission(adminUserId, Permissions.SUSPEND_TUTOR);
        User tutor = getUserOrThrow(tutorId, Role.TUTOR);
        tutor.setStatus(AccountStatus.ACTIVE);
        userRepository.save(tutor);
        return tutorId;
    }

    @Override
    public String deleteTutor(String adminUserId, String tutorId) {
        checkAdminWithPermission(adminUserId, Permissions.DELETE_TUTOR);
        User tutor = getUserOrThrow(tutorId, Role.TUTOR);
        tutor.setStatus(AccountStatus.DELETED);
        userRepository.save(tutor);
        return tutorId;
    }

    // -------------------------------
    //  Admin Management
    // -------------------------------
    @Override
    public Optional<AdminDTO> getAdminByUserId(String userId) {
        return userRepository.findById(userId)
            .flatMap(user -> adminRepository.findByUserId(user.getId())
                .map(admin -> AdminDTO.builder()
                    .userId(user.getId())
                    .firstName(user.getFirstname())
                    .lastName(user.getLastname())
                    .email(user.getEmail())
                    .role(user.getRole().toString())
                    .status(user.getStatus().toString())
                    .permissions(admin.getPermissions())
                    .build()
                )
            );
    }

    @Override
    public List<UserResponse> viewAdmins(String adminUserId) {
        checkAdminWithPermission(adminUserId, Permissions.VIEW_ADMIN);
        List<User> admins = userRepository.findAllByRole(Role.ADMIN);
            return admins.stream()
                .map(user -> {
                    UserResponse.UserResponseBuilder builder = UserResponse.builder()
                        .id(user.getId())
                        .name(user.getFirstname() + " " + user.getLastname())
                        .email(user.getEmail())
                        .role(user.getRole())
                        .status(user.getStatus());

                    adminRepository.findByUserId(user.getId()).ifPresent(admin ->
                        builder.admin(AdminResponse.builder()
                            .permissions(admin.getPermissions())
                            .build()
                        )
                    );
                    return builder.build();
                })
                .toList();
    }

    @Override
    public void createAdmin(AdminDTO adminDTO) {
        Admin newAdmin = adminMapper.toEntity(adminDTO);
        Admin savedAdmin = adminRepository.save(newAdmin);
        adminMapper.toDTO(savedAdmin);
    }

    @Override
    public void createAdminByAdmin(String adminUserId, AdminDTO adminDTO) {
        checkAdminWithPermission(adminUserId, Permissions.CREATE_ADMIN);
        Admin newAdmin = adminMapper.toEntity(adminDTO);
        Admin savedAdmin = adminRepository.save(newAdmin);
        adminMapper.toDTO(savedAdmin);
    }


    @Override
    public void editAdminRoles(String adminUserId, String targetAdminId, List<Permissions> newPermissions) {
        checkAdminWithPermission(adminUserId, Permissions.EDIT_ADMIN_ROLES);
        Admin targetAdmin = adminRepository.findByUserId(targetAdminId)
            .orElseThrow(() -> new RuntimeException("Target admin not found"));
        targetAdmin.setPermissions(newPermissions);
        adminRepository.save(targetAdmin);
    }

    @Override
    public String suspendAdmin(String adminUserId, String targetAdminId) {
        checkAdminWithPermission(adminUserId, Permissions.SUSPEND_ADMIN);
        User target = getUserOrThrow(targetAdminId, Role.ADMIN);
        target.setStatus(AccountStatus.SUSPENDED);
        userRepository.save(target);
        return targetAdminId;
    }

    @Override
    public String activateAdmin(String adminUserId, String targetAdminId) {
        checkAdminWithPermission(adminUserId, Permissions.SUSPEND_ADMIN);
        User target = getUserOrThrow(targetAdminId, Role.ADMIN);
        target.setStatus(AccountStatus.ACTIVE);
        userRepository.save(target);
        return targetAdminId;
    }

    @Override
    public String deleteAdmin(String adminUserId, String targetAdminId) {
        checkAdminWithPermission(adminUserId, Permissions.DELETE_ADMIN);
        User target = getUserOrThrow(targetAdminId, Role.ADMIN);
        target.setStatus(AccountStatus.DELETED);
        userRepository.save(target);
        return targetAdminId;
    }

    // -------------------------------
    // Helpers
    // -------------------------------
    private void checkAdminWithPermission(String adminUserId, Permissions required) {
        User adminUser = userRepository.findById(adminUserId)
            .orElseThrow(() -> new RuntimeException("Admin user not found"));

        if (adminUser.getRole() != Role.ADMIN) {
            throw new RuntimeException("Not an admin");
        }

        Admin adminProfile = adminRepository.findByUserId(adminUserId)
            .orElseThrow(() -> new RuntimeException("Admin profile not found"));

        checkPermission(adminProfile, required);
    }

    private void checkPermission(Admin adminProfile, Permissions required) {
        if (!adminProfile.getPermissions().contains(required)) {
            throw new RuntimeException("Unauthorized: missing " + required + " permission");
        }
    }

    private User getUserOrThrow(String userId, Role expectedRole) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));
        if (user.getRole() != expectedRole) {
            throw new RuntimeException("User is not a " + expectedRole);
        }
        return user;
    }


    private UserResponse createUserResponse(User user) {
        return UserResponse.builder()
            .id(user.getId())
            .name(user.getFirstname() + " " + user.getLastname())
            .email(user.getEmail())
            .role(user.getRole())
            .status(user.getStatus())
            .build();
    }


}
