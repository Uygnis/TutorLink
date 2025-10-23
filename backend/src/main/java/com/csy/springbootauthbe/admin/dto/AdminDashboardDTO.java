package com.csy.springbootauthbe.admin.dto;

import com.csy.springbootauthbe.admin.entity.Permissions;
import com.csy.springbootauthbe.tutor.dto.TutorDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminDashboardDTO {
    private int totalUsers;
    private int activeUsers;
    private int suspendedUsers;

    private int totalTutors;
    private int activeTutors;
    private int suspendedTutors;
    private int rejectedTutors;

    private int totalStudents;
    private int activeStudents;
    private int suspendedStudents;
    private List<TutorDTO> pendingTutors;
}