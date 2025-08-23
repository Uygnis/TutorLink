package com.csy.springbootauthbe.tutor.service;

import com.csy.springbootauthbe.tutor.dto.TutorDTO;
import com.csy.springbootauthbe.tutor.entity.Tutor;
import com.csy.springbootauthbe.tutor.mapper.TutorMapper;
import com.csy.springbootauthbe.tutor.repository.TutorRepository;
import com.csy.springbootauthbe.tutor.utils.TutorRequest;
import com.csy.springbootauthbe.tutor.utils.TutorResponse;
import com.csy.springbootauthbe.user.entity.AccountStatus;
import com.csy.springbootauthbe.user.entity.Role;
import com.csy.springbootauthbe.user.entity.User;
import com.csy.springbootauthbe.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TutorServiceTest {
    @Mock
    private TutorRepository tutorRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private TutorMapper mapper;

    @InjectMocks
    private TutorService tutorService;

    private Tutor tutorEntity;
    private TutorDTO tutorDTO;
    private TutorRequest tutorRequest;
    private TutorResponse tutorResponse;

    @BeforeEach
    void setUp() {
        tutorEntity = new Tutor();
        tutorEntity.setId("t1");
        tutorEntity.setUserId("user123");

        tutorDTO = new TutorDTO();
        tutorDTO.setId("t1");
        tutorDTO.setUserId("user123");

        tutorRequest = new TutorRequest();
        tutorRequest.setUserId(tutorDTO.getUserId());
        tutorRequest.setHourlyRate(10d);

        tutorResponse = new TutorResponse();
        tutorResponse.setId("t1");
        tutorResponse.setHourlyRate(10d);
    }

    @Test
    void createTutor_shouldReturnTutorDTO() {
        //prepare
        when(tutorRepository.save(any(Tutor.class))).thenReturn(tutorEntity);
        when(mapper.toDTO(tutorEntity)).thenReturn(tutorDTO);

        //act
        TutorDTO saved = tutorService.createTutor(tutorDTO);

        //assert
        assertNotNull(saved);
        assertEquals(tutorDTO.getUserId(), saved.getUserId());

        // verify
        verify(tutorRepository).save(any(Tutor.class));
        verify(mapper).toDTO(tutorEntity);
    }

    @Test
    void getTutor_shouldReturnTutorDTO() {
        // prepare
        when(tutorRepository.findByUserId("user123")).thenReturn(Optional.of(tutorEntity));
        when(mapper.toDTO(tutorEntity)).thenReturn(tutorDTO);

        // act
        Optional<TutorDTO> result = tutorService.getTutorByUserId("user123");

        // assert
        assertTrue(result.isPresent());
        assertEquals("t1", result.get().getId());

        // verify
        verify(tutorRepository).findByUserId("user123");
        verify(mapper).toDTO(tutorEntity);
    }

    @Test
    void getTutor_shouldReturnNull(){
        // prepare
        when(tutorRepository.findByUserId("userNotExist123")).thenReturn(Optional.empty());

        // act
        Optional<TutorDTO> result = tutorService.getTutorByUserId("userNotExist123");

        // assert
        assertFalse(result.isPresent());

        // verify
        verify(tutorRepository).findByUserId("userNotExist123");
    }

    @Test
    void updateTutor_shouldReturnTutorResponse() {
        // prepare
        when(tutorRepository.save(any(Tutor.class))).thenReturn(tutorEntity);
        when(mapper.toDTO(tutorEntity)).thenReturn(tutorDTO);

        // act
        TutorResponse updated = tutorService.updateTutor(tutorRequest.getUserId(), tutorRequest);

        // assert
        assertNotNull(updated);

        // verify
        verify(tutorRepository).save(any(Tutor.class));
        verify(mapper).toDTO(tutorEntity);
    }

}
