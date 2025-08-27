package com.csy.springbootauthbe.user.utils;

import java.util.List;

import com.csy.springbootauthbe.admin.entity.Permissions;
import com.csy.springbootauthbe.user.entity.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RegisterRequest {
    private String firstname;
    private String lastname;
    private String email;
    private String password;
    private String role;

    // Student-specific fields
    private String studentNumber;
    private String gradeLevel;

    // Admin-specific fields
    private List<Permissions> permissions;
}
