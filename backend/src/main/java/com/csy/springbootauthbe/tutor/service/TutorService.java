package com.csy.springbootauthbe.tutor.service;

import com.csy.springbootauthbe.student.dto.StudentDTO;
import com.csy.springbootauthbe.tutor.dto.TutorDTO;

import java.util.Optional;

public interface TutorService {
    TutorDTO createTutor(TutorDTO tutorDTO);
    Optional<TutorDTO> getTutorByUserId(String userId);

}
