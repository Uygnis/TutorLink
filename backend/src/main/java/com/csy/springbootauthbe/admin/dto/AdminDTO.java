package com.csy.springbootauthbe.admin.dto;

import java.util.List;

import com.csy.springbootauthbe.admin.entity.Permissions;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminDTO {
    private String id;
    private String userId;
    private String firstName;
    private String lastName;
    private String email;
    private String role;
    private String status;
    private List<Permissions> permissions;
}
