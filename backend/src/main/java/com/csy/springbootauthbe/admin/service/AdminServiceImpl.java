package com.csy.springbootauthbe.admin.service;

import com.csy.springbootauthbe.admin.dto.AdminDTO;
import com.csy.springbootauthbe.admin.entity.Admin;
import com.csy.springbootauthbe.admin.entity.Permissions;
import com.csy.springbootauthbe.admin.mapper.AdminMapper;
import com.csy.springbootauthbe.admin.repository.AdminRepository;
import com.csy.springbootauthbe.admin.util.AdminResponse;
import com.csy.springbootauthbe.student.repository.StudentRepository;
import com.csy.springbootauthbe.student.util.StudentResponse;
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

    @Override
    public AdminDTO createAdmin(AdminDTO adminDTO) {
        Admin admin = adminMapper.toEntity(adminDTO);
        Admin savedAdmin = adminRepository.save(admin);
        return adminMapper.toDTO(savedAdmin);
    }

    @Override
    public Optional<AdminDTO> getAdminByUserId(String userId) {
        return adminRepository.findByUserId(userId)
                .map(adminMapper::toDTO);
    }

    public List<UserResponse> getUsersByType(Role role) {
        List<User> users = userRepository.findAllByRole(role);

        return users.stream()
            .map(user -> {
                UserResponse.UserResponseBuilder builder = UserResponse.builder()
                    .id(user.getId())
                    .name(user.getFirstname() + " " + user.getLastname())
                    .email(user.getEmail())
                    .role(user.getRole())
                    .status(user.getStatus());

                if (role == Role.STUDENT) {
                    studentRepository.findByUserId(user.getId()).ifPresent(student ->
                        builder.student(StudentResponse.builder()
                            .studentNumber(student.getStudentNumber())
                            .gradeLevel(student.getGradeLevel())
                            .build()
                        )
                    );
                }

                if (role == Role.ADMIN) {
                    adminRepository.findByUserId(user.getId()).ifPresent(admin ->
                        builder.admin(AdminResponse.builder()
                            .permissions(admin.getPermissions())
                            .build()
                        )
                    );
                }

                return builder.build();
            })
            .toList();
    }

    @Override
    public void updateUserRole(String adminUserId, String targetUserId, Role newRole) {
        // Load admin making the change
        User adminUser = userRepository.findById(adminUserId)
                .orElseThrow(() -> new RuntimeException("Admin user not found"));

        if (adminUser.getRole() != Role.ADMIN) {
            throw new RuntimeException("Not an admin");
        }

        Admin adminProfile = adminRepository.findByUserId(adminUserId)
                .orElseThrow(() -> new RuntimeException("Admin profile not found"));

        if (!adminProfile.getPermissions().contains(Permissions.EDIT_ROLES)) {
            throw new RuntimeException("Unauthorized: missing EDIT_ROLES permission");
        }

        // Load the target user
        User targetUser = userRepository.findById(targetUserId)
                .orElseThrow(() -> new RuntimeException("Target user not found"));

        // If new role is SUPER_ADMIN, require MANAGE_ADMINS
        if (newRole == Role.ADMIN &&
                !adminProfile.getPermissions().contains(Permissions.MANAGE_ADMINS)) {
            throw new RuntimeException("Unauthorized: cannot assign MANAGE_ADMIN permission");
        }

        // If target user is SUPER_ADMIN, block downgrading unless MANAGE_ADMINS
        if (targetUser.getRole() == Role.ADMIN &&
                !adminProfile.getPermissions().contains(Permissions.MANAGE_ADMINS)) {
            throw new RuntimeException("Unauthorized: cannot modify this user.");
        }

        // Update and save
        targetUser.setRole(newRole);
        userRepository.save(targetUser);
    }

    @Override
    public void deleteUser(String adminUserId, String targetUserId) {
        User adminUser = userRepository.findById(adminUserId)
                .orElseThrow(() -> new RuntimeException("Admin user not found"));

        if (adminUser.getRole() != Role.ADMIN) {
            throw new RuntimeException("Not an admin");
        }

        Admin adminProfile = adminRepository.findByUserId(adminUserId)
                .orElseThrow(() -> new RuntimeException("Admin profile not found"));

        if (!adminProfile.getPermissions().contains(Permissions.DELETE_USER)) {
            throw new RuntimeException("Unauthorized: missing DELETE_USER permission");
        }

        User targetUser = userRepository.findById(targetUserId)
                .orElseThrow(() -> new RuntimeException("Target user not found"));

        // Optional: prevent deleting another admin without MANAGE_ADMINS
        if (targetUser.getRole() == Role.ADMIN &&
                !adminProfile.getPermissions().contains(Permissions.MANAGE_ADMINS)) {
            throw new RuntimeException("Cannot delete another admin");
        }

        targetUser.setStatus(AccountStatus.DELETED);
        userRepository.save(targetUser);
    }

}
