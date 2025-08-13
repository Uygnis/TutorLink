package com.csy.springbootauthbe.tutor.service;

import com.csy.springbootauthbe.student.entity.Student;
import com.csy.springbootauthbe.tutor.dto.TutorDTO;
import com.csy.springbootauthbe.tutor.entity.Tutor;
import com.csy.springbootauthbe.tutor.mapper.TutorMapper;
import com.csy.springbootauthbe.tutor.repository.TutorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TutorServiceImpl implements TutorService {

    private final TutorRepository tutorRepository;
    private final TutorMapper tutorMapper;

    @Override
    public TutorDTO createTutor(TutorDTO tutorDTO) {
        Tutor tutor = tutorMapper.toEntity(tutorDTO);
        Tutor saved = tutorRepository.save(tutor);
        return tutorMapper.toDTO(saved);
    }

    @Override
    public Optional<TutorDTO> getTutorByUserId(String userId) {
        return tutorRepository.findByUserId(userId).map(tutorMapper::toDTO);
    }





}
