package com.csy.springbootauthbe.student.service;

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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StudentServiceImplTest {

    @Mock StudentRepository studentRepository;
    @Mock StudentMapper studentMapper;

    @InjectMocks StudentServiceImpl studentService;

    @Test
    void createStudent_mapsSavesAndReturnsDto() {
        StudentDTO inputDto = new StudentDTO();
        Student mapped = new Student();
        Student saved = new Student();
        StudentDTO out = new StudentDTO();

        when(studentMapper.toEntity(inputDto)).thenReturn(mapped);
        when(studentRepository.save(mapped)).thenReturn(saved);
        when(studentMapper.toDTO(saved)).thenReturn(out);

        StudentDTO result = studentService.createStudent(inputDto);

        assertSame(out, result);
        verify(studentMapper).toEntity(inputDto);
        verify(studentRepository).save(mapped);
        verify(studentMapper).toDTO(saved);
    }

    @Test
    void getStudentByUserId_found_returnsDto() {
        Student entity = new Student();
        StudentDTO dto = new StudentDTO();
        when(studentRepository.findByUserId("U1")).thenReturn(Optional.of(entity));
        when(studentMapper.toDTO(entity)).thenReturn(dto);

        Optional<StudentDTO> result = studentService.getStudentByUserId("U1");

        assertTrue(result.isPresent());
        assertSame(dto, result.get());
    }

    @Test
    void getStudentByUserId_notFound_returnsEmpty() {
        when(studentRepository.findByUserId("NONE")).thenReturn(Optional.empty());

        Optional<StudentDTO> result = studentService.getStudentByUserId("NONE");

        assertTrue(result.isEmpty());
        verify(studentMapper, never()).toDTO(any());
    }
}
