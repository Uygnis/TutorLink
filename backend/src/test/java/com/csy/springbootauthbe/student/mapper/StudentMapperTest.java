package com.csy.springbootauthbe.student.mapper;

import com.csy.springbootauthbe.student.dto.StudentDTO;
import com.csy.springbootauthbe.student.entity.Student;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static org.junit.jupiter.api.Assertions.*;

class StudentMapperTest {

    private final StudentMapper mapper = Mappers.getMapper(StudentMapper.class);

    @Test
    void toDTO_shouldMapBasicFields() {
        Student entity = new Student();
        entity.setId("S001");
        entity.setUserId("U100");
        entity.setStudentNumber("STU-001");
        entity.setGradeLevel("G10");
        entity.setProfileImageUrl("url");

        StudentDTO dto = mapper.toDTO(entity);

        assertNotNull(dto);
        assertEquals("S001", dto.getId());
        assertEquals("U100", dto.getUserId());
        assertEquals("STU-001", dto.getStudentNumber());
        assertEquals("G10", dto.getGradeLevel());
        assertEquals("url", dto.getProfileImageUrl());
    }

    @Test
    void toEntity_shouldMapBasicFields() {
        StudentDTO dto = new StudentDTO();
        dto.setId("S002");
        dto.setUserId("U200");
        dto.setStudentNumber("STU-002");
        dto.setGradeLevel("G11");
        dto.setProfileImageUrl("pic-url");

        Student entity = mapper.toEntity(dto);

        assertNotNull(entity);
        assertEquals("S002", entity.getId());
        assertEquals("U200", entity.getUserId());
        assertEquals("STU-002", entity.getStudentNumber());
        assertEquals("G11", entity.getGradeLevel());
        assertEquals("pic-url", entity.getProfileImageUrl());
    }
}
