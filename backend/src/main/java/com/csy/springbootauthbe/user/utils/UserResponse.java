package com.csy.springbootauthbe.user.utils;

import com.csy.springbootauthbe.admin.util.AdminResponse;
import com.csy.springbootauthbe.student.utils.StudentResponse;
import com.csy.springbootauthbe.tutor.utils.TutorResponse;
import com.csy.springbootauthbe.user.entity.AccountStatus;
import com.csy.springbootauthbe.user.entity.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserResponse {
    private String id;
    private String name;
    private String email;
    private Role role;
    private AccountStatus status;
    private String token;

    private StudentResponse student; // null if not student
    private TutorResponse tutor; // null if not tutor
    private AdminResponse admin;     // null if not admin
}
