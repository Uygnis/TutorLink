package com.csy.springbootauthbe.user.utils;

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
}
