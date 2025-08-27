package com.csy.springbootauthbe.admin.service;

import com.csy.springbootauthbe.admin.dto.AdminDTO;
import com.csy.springbootauthbe.admin.entity.Permissions;
import com.csy.springbootauthbe.user.utils.UserResponse;

import java.util.List;
import java.util.Optional;

public interface AdminService {
    // -------------------------------
    //  Student Management
    // -------------------------------
    List<UserResponse> viewStudents(String adminUserId);

    void suspendStudent(String adminUserId, String studentId);

    void deleteStudent(String adminUserId, String studentId);

    // -------------------------------
    //  Tutor Management
    // -------------------------------
    List<UserResponse> viewTutors(String adminUserId);

    void approveTutor(String adminUserId, String tutorId);

    void rejectTutor(String adminUserId, String tutorId);

    void suspendTutor(String adminUserId, String tutorId);

    void deleteTutor(String adminUserId, String tutorId);

    // -------------------------------
    //  Admin Management
    // -------------------------------
    Optional<AdminDTO> getAdminByUserId(String userId);

    // -------------------------------
    //  Admin Management
    // -------------------------------
    List<UserResponse> viewAdmins(String adminUserId);

    void createAdmin(AdminDTO adminDTO);

    void createAdminByAdmin(String adminUserId, AdminDTO adminDTO);

    void editAdminRoles(String adminUserId, String targetAdminId, List<Permissions> newPermissions);

    void suspendAdmin(String adminUserId, String targetAdminId);

    void deleteAdmin(String adminUserId, String targetAdminId);
}
