package com.csy.springbootauthbe.admin.controller;

import com.csy.springbootauthbe.admin.dto.AdminDTO;
import com.csy.springbootauthbe.admin.service.AdminService;
import com.csy.springbootauthbe.user.entity.Role;
import com.csy.springbootauthbe.user.utils.UserResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
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

    @GetMapping("/tutors/{adminId}")
    public ResponseEntity<List<UserResponse>> getAllTutors(@PathVariable String adminId) { return ResponseEntity.ok(adminService.viewTutors(adminId));}

    @GetMapping("/students/{adminId}")
    public ResponseEntity<List<UserResponse>> getAllStudents(@PathVariable String adminId) { return ResponseEntity.ok(adminService.viewStudents(adminId));}

    @GetMapping("/admins/{adminId}")
    public ResponseEntity<List<UserResponse>> getAllAdmins(@PathVariable String adminId) { return ResponseEntity.ok(adminService.viewAdmins(adminId));}

    @PutMapping("/suspendAdmin/{adminId}/{userId}")
    public ResponseEntity<UserResponse> suspendAdmin(@PathVariable String adminId, @PathVariable String userId) {
        String updatedUserOpt = adminService.suspendAdmin(adminId, userId);
        return ResponseEntity.ok(UserResponse.builder().id(updatedUserOpt).role(Role.ADMIN).build());
    }

    @PutMapping("/activateAdmin/{adminId}/{userId}")
    public ResponseEntity<UserResponse> activateAdmin(@PathVariable String adminId, @PathVariable String userId) {
        String updatedUserOpt = adminService.activateAdmin(adminId, userId);
        return ResponseEntity.ok(UserResponse.builder().id(updatedUserOpt).role(Role.ADMIN).build());
    }

    @PutMapping("/suspendTutor/{adminId}/{userId}")
    public ResponseEntity<UserResponse> suspendTutor(@PathVariable String adminId, @PathVariable String userId) {
        String updatedUserOpt = adminService.suspendTutor(adminId, userId);
        return ResponseEntity.ok(UserResponse.builder().id(updatedUserOpt).role(Role.TUTOR).build());
    }

    @PutMapping("/activateTutor/{adminId}/{userId}")
    public ResponseEntity<UserResponse> activateTutor(@PathVariable String adminId, @PathVariable String userId) {
        String updatedUserOpt = adminService.activateTutor(adminId, userId);
        return ResponseEntity.ok(UserResponse.builder().id(updatedUserOpt).role(Role.TUTOR).build());
    }

    @PutMapping("/suspendStudent/{adminId}/{userId}")
    public ResponseEntity<UserResponse> suspendStudent(@PathVariable String adminId, @PathVariable String userId) {
        String updatedUserOpt = adminService.suspendStudent(adminId, userId);
        return ResponseEntity.ok(UserResponse.builder().id(updatedUserOpt).role(Role.STUDENT).build());
    }

    @PutMapping("/activateStudent/{adminId}/{userId}")
    public ResponseEntity<UserResponse> activateStudent(@PathVariable String adminId, @PathVariable String userId) {
        String updatedUserOpt = adminService.activateStudent(adminId, userId);
        return ResponseEntity.ok(UserResponse.builder().id(updatedUserOpt).role(Role.STUDENT).build());
    }

    @DeleteMapping("admin/{adminId}/{userId}")
    public ResponseEntity<UserResponse> deleteAdmin(@PathVariable String adminId, @PathVariable String userId) {
        String deletedUserOpt = adminService.deleteAdmin(adminId, userId);
        return ResponseEntity.ok(UserResponse.builder().id(deletedUserOpt).role(Role.ADMIN).build());
    }

    @DeleteMapping("tutor/{adminId}/{userId}")
    public ResponseEntity<UserResponse> deleteTutor(@PathVariable String adminId, @PathVariable String userId) {
        String deletedUserOpt = adminService.deleteTutor(adminId, userId);
        return ResponseEntity.ok(UserResponse.builder().id(deletedUserOpt).role(Role.ADMIN).build());
    }

    @DeleteMapping("student/{adminId}/{userId}")
    public ResponseEntity<UserResponse> deleteStudent(@PathVariable String adminId, @PathVariable String userId) {
        String deletedUserOpt = adminService.deleteStudent(adminId, userId);
        return ResponseEntity.ok(UserResponse.builder().id(deletedUserOpt).role(Role.ADMIN).build());
    }
}
