package com.csy.springbootauthbe.admin.controller;

import com.csy.springbootauthbe.admin.dto.AdminDTO;
import com.csy.springbootauthbe.admin.service.AdminService;
import com.csy.springbootauthbe.user.entity.Role;
import com.csy.springbootauthbe.user.utils.UserResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/admins")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    @GetMapping("/by-user/{userId}")
    public ResponseEntity<AdminDTO> getAdminByUserId(@PathVariable String userId) {
        Optional<AdminDTO> adminOpt = adminService.getAdminByUserId(userId);
        return adminOpt.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/tutors")
    public ResponseEntity<List<UserResponse>> getAllTutors() { return ResponseEntity.ok(adminService.getUsersByType(Role.TUTOR));}

    @GetMapping("/students")
    public ResponseEntity<List<UserResponse>> getAllStudents() { return ResponseEntity.ok(adminService.getUsersByType(Role.STUDENT));}

    @GetMapping("/admins")
    public ResponseEntity<List<UserResponse>> getAllAdmins() { return ResponseEntity.ok(adminService.getUsersByType(Role.ADMIN));}


}
