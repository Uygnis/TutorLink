package com.csy.springbootauthbe.student.service;

import com.csy.springbootauthbe.student.dto.StudentDTO;

import java.util.List;
import java.util.Optional;

import com.csy.springbootauthbe.student.dto.TutorProfileDTO;
import com.csy.springbootauthbe.student.utils.TutorSearchRequest;

public interface StudentService {

    StudentDTO createStudent(StudentDTO studentDTO);

    Optional<StudentDTO> getStudentByUserId(String userId);

    List<TutorProfileDTO> searchTutors(TutorSearchRequest req);
    Optional<TutorProfileDTO> getTutorById(String tutorId);


}
