package com.csy.springbootauthbe.admin.service;

import com.csy.springbootauthbe.admin.dto.AdminDTO;
import com.csy.springbootauthbe.admin.entity.Admin;
import com.csy.springbootauthbe.admin.entity.Permissions;
import com.csy.springbootauthbe.admin.mapper.AdminMapper;
import com.csy.springbootauthbe.admin.repository.AdminRepository;
import com.csy.springbootauthbe.admin.util.AdminResponse;
import com.csy.springbootauthbe.student.repository.StudentRepository;
import com.csy.springbootauthbe.student.utils.StudentResponse;
import com.csy.springbootauthbe.tutor.repository.TutorRepository;
import com.csy.springbootauthbe.tutor.utils.TutorResponse;
import com.csy.springbootauthbe.user.entity.AccountStatus;
import com.csy.springbootauthbe.user.entity.Role;
import com.csy.springbootauthbe.user.entity.User;
import com.csy.springbootauthbe.user.repository.UserRepository;
import com.csy.springbootauthbe.user.utils.UserResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
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
    public void suspendStudent(String adminUserId, String studentId) {
        checkAdminWithPermission(adminUserId, Permissions.SUSPEND_STUDENT);
        User student = getUserOrThrow(studentId, Role.STUDENT);
        student.setStatus(AccountStatus.SUSPENDED);
        userRepository.save(student);
    }

    @Override
    public void deleteStudent(String adminUserId, String studentId) {
        checkAdminWithPermission(adminUserId, Permissions.DELETE_STUDENT);
        User student = getUserOrThrow(studentId, Role.STUDENT);
        student.setStatus(AccountStatus.DELETED);
        userRepository.save(student);
    }

    // -------------------------------
    // Tutor Management
    // -------------------------------
    @Override
    public List<UserResponse> viewTutors(String adminUserId) {
        checkAdminWithPermission(adminUserId, Permissions.VIEW_TUTORS);
        List<User> tutors = userRepository.findAllByRole(Role.TUTOR);
        return tutors.stream()
            .map(user -> {
                UserResponse.UserResponseBuilder builder = UserResponse.builder()
                    .id(user.getId())
                    .name(user.getFirstname() + " " + user.getLastname())
                    .email(user.getEmail())
                    .role(user.getRole())
                    .status(user.getStatus());

                tutorRepository.findByUserId(user.getId()).ifPresent(tutor ->
                    builder.tutor(TutorResponse.builder()
                            .availability(tutor.getAvailability())
                            .hourlyRate(tutor.getHourlyRate())
                            .subject(tutor.getSubject())
                            .qualifications(tutor.getQualifications())
                            .build()
                    )
                );
                return builder.build();
            })
            .toList();

    }

    @Override
    public void approveTutor(String adminUserId, String tutorId) {
        checkAdminWithPermission(adminUserId, Permissions.APPROVE_TUTOR);
        User tutor = getUserOrThrow(tutorId, Role.TUTOR);
        tutor.setStatus(AccountStatus.ACTIVE);
        userRepository.save(tutor);
    }

    @Override
    public void rejectTutor(String adminUserId, String tutorId) {
        checkAdminWithPermission(adminUserId, Permissions.REJECT_TUTOR);
        User tutor = getUserOrThrow(tutorId, Role.TUTOR);
        tutor.setStatus(AccountStatus.REJECTED);
        userRepository.save(tutor);
    }

    @Override
    public void suspendTutor(String adminUserId, String tutorId) {
        checkAdminWithPermission(adminUserId, Permissions.SUSPEND_TUTOR);
        User tutor = getUserOrThrow(tutorId, Role.TUTOR);
        tutor.setStatus(AccountStatus.SUSPENDED);
        userRepository.save(tutor);
    }

    @Override
    public void deleteTutor(String adminUserId, String tutorId) {
        checkAdminWithPermission(adminUserId, Permissions.DELETE_TUTOR);
        User tutor = getUserOrThrow(tutorId, Role.TUTOR);
        tutor.setStatus(AccountStatus.DELETED);
        userRepository.save(tutor);
    }

    // -------------------------------
    //  Admin Management
    // -------------------------------
    @Override
    public Optional<AdminDTO> getAdminByUserId(String userId) {
        return adminRepository.findByUserId(userId)
            .map(adminMapper::toDTO);
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
    public void suspendAdmin(String adminUserId, String targetAdminId) {
        checkAdminWithPermission(adminUserId, Permissions.SUSPEND_ADMIN);
        User target = getUserOrThrow(targetAdminId, Role.ADMIN);
        target.setStatus(AccountStatus.SUSPENDED);
        userRepository.save(target);
    }

    @Override
    public void deleteAdmin(String adminUserId, String targetAdminId) {
        checkAdminWithPermission(adminUserId, Permissions.DELETE_ADMIN);
        User target = getUserOrThrow(targetAdminId, Role.ADMIN);
        target.setStatus(AccountStatus.DELETED);
        userRepository.save(target);
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
