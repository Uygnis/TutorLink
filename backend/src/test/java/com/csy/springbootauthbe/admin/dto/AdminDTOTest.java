package com.csy.springbootauthbe.admin.dto;

import com.csy.springbootauthbe.admin.entity.Permissions;
import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class AdminDTOTest {

    @Test
    void testBuilderAndFields() {
        List<Permissions> perms = List.of(Permissions.VIEW_STUDENTS, Permissions.SUSPEND_ADMIN);
        AdminDTO dto = AdminDTO.builder()
            .id("1")
            .userId("u1")
            .email("a@a.com")
            .permissions(perms)
            .build();
        assertEquals("u1", dto.getUserId());
        assertEquals(2, dto.getPermissions().size());
    }
}
