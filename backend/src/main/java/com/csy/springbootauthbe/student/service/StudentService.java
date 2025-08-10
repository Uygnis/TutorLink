package com.csy.springbootauthbe.student.service;

import com.csy.springbootauthbe.student.dto.StudentDTO;

import java.util.List;
import java.util.Optional;

public interface StudentService {

    StudentDTO createStudent(StudentDTO studentDTO);

    Optional<StudentDTO> getStudentByUserId(String userId);

}
