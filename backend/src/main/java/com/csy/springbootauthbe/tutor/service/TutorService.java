package com.csy.springbootauthbe.tutor.service;

import com.csy.springbootauthbe.tutor.dto.TutorDTO;
import com.csy.springbootauthbe.tutor.utils.TutorRequest;
import com.csy.springbootauthbe.tutor.utils.TutorResponse;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.Optional;

public interface TutorService {
    TutorDTO createTutor(TutorDTO tutorDTO);
    Optional<TutorDTO> getTutorByUserId(String userId);
    void deleteTutor(String userId);
    TutorResponse updateTutor(String userId, TutorRequest updateRequest) throws NoSuchAlgorithmException, IOException;
}
