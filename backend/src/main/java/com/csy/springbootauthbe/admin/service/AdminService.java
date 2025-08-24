package com.csy.springbootauthbe.admin.service;

import com.csy.springbootauthbe.admin.dto.AdminDTO;
import com.csy.springbootauthbe.user.entity.Role;
import com.csy.springbootauthbe.user.utils.UserResponse;

import java.util.List;
import java.util.Optional;

public interface AdminService {
    AdminDTO createAdmin(AdminDTO adminDTO);
    Optional<AdminDTO> getAdminByUserId(String userId);

    List<UserResponse> getUsersByType(Role role);

    void updateUserRole(String adminUserId, String targetUserId, Role newRole);

    void deleteUser(String adminUserId, String targetUserId);
}
