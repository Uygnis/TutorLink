package com.csy.springbootauthbe.student.service;

import com.csy.springbootauthbe.common.sequence.SequenceGeneratorService;
import com.csy.springbootauthbe.student.dto.StudentDTO;
import com.csy.springbootauthbe.student.entity.Student;
import com.csy.springbootauthbe.student.mapper.StudentMapper;
import com.csy.springbootauthbe.student.repository.StudentRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StudentServiceImplTest {

    @Mock StudentRepository repo;
    @Mock StudentMapper mapper;
    @Mock SequenceGeneratorService sequence;

    @InjectMocks StudentServiceImpl service;

    @Test
    void createStudent_happyPath_assignsId_and_maps_and_saves() {
        StudentDTO in = new StudentDTO();
        in.setUserId("U1");

        Student mapped = new Student();
        Student saved = new Student();
        StudentDTO out = new StudentDTO();

        when(sequence.getNextStudentId()).thenReturn("S100");
        when(mapper.toEntity(in)).thenAnswer(inv -> {
            // emulate mapper behavior that uses generated id
            mapped.setId("S100");
            mapped.setUserId("U1");
            return mapped;
        });
        when(repo.save(mapped)).thenReturn(saved);
        when(mapper.toDTO(saved)).thenAnswer(inv -> {
            out.setId("S100");
            out.setUserId("U1");
            return out;
        });

        StudentDTO result = service.createStudent(in);

        assertEquals("S100", result.getId());
        assertEquals("U1", result.getUserId());
        verify(sequence).getNextStudentId();
        verify(mapper).toEntity(in);
        verify(repo).save(mapped);
        verify(mapper).toDTO(saved);
    }

    @Test
    void getStudentByUserId_found_returnsDto() {
        Student entity = new Student();
        entity.setUserId("U1");
        StudentDTO dto = new StudentDTO();
        dto.setUserId("U1");

        when(repo.findByUserId("U1")).thenReturn(Optional.of(entity));
        when(mapper.toDTO(entity)).thenReturn(dto);

        var result = service.getStudentByUserId("U1");

        assertTrue(result.isPresent());
        assertEquals("U1", result.get().getUserId());
    }

    @Test
    void getStudentByUserId_notFound_returnsEmpty() {
        when(repo.findByUserId("MISSING")).thenReturn(Optional.empty());

        var result = service.getStudentByUserId("MISSING");

        assertTrue(result.isEmpty());
        verify(mapper, never()).toDTO(any());
    }
}
