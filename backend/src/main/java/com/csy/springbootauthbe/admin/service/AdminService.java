package com.csy.springbootauthbe.admin.service;

import com.csy.springbootauthbe.admin.dto.AdminDTO;

import java.util.Optional;

public interface AdminService {
    AdminDTO createAdmin(AdminDTO adminDTO);
    Optional<AdminDTO> getAdminByUserId(String userId);

}
