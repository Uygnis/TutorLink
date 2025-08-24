package com.csy.springbootauthbe.tutor.service;

import com.csy.springbootauthbe.tutor.dto.TutorDTO;
import com.csy.springbootauthbe.tutor.entity.Tutor;
import com.csy.springbootauthbe.tutor.mapper.TutorMapper;
import com.csy.springbootauthbe.tutor.repository.TutorRepository;
import com.csy.springbootauthbe.tutor.utils.TutorRequest;
import com.csy.springbootauthbe.tutor.utils.TutorResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
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

    @Override
    public TutorResponse updateTutor(String userId, TutorRequest updatedData) {
        Tutor tutor = tutorRepository.findByUserId(userId)
                .orElseThrow(() -> new UsernameNotFoundException("Tutor not found"));
        tutor.setHourlyRate(updatedData.getHourlyRate());
        tutor.setQualifications(updatedData.getQualifications());
        tutor.setAvailability(updatedData.getAvailability());
        tutorRepository.save(tutor);
        return createTutorResponse(tutor);
    }

    @Override
    public void deleteTutor(String userId) {
            Tutor tutor = tutorRepository.findByUserId(userId)
                    .orElseThrow(() -> new UsernameNotFoundException("Tutor not found"));
        tutorRepository.delete(tutor);
    }

    private TutorResponse createTutorResponse(Tutor tutor) {
        return TutorResponse.builder()
                .id(tutor.getId())
                .hourlyRate(tutor.getHourlyRate())
                .qualifications(tutor.getQualifications())
                .availability(tutor.getAvailability())
                .build();
    }


}
