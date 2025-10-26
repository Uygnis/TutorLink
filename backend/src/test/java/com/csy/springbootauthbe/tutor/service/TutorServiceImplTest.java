package com.csy.springbootauthbe.tutor.service;

import com.csy.springbootauthbe.tutor.dto.TutorDTO;
import com.csy.springbootauthbe.tutor.entity.Tutor;
import com.csy.springbootauthbe.tutor.mapper.TutorMapper;
import com.csy.springbootauthbe.tutor.repository.TutorRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TutorServiceImplTest {

    @Mock TutorRepository tutorRepository;
    @Mock TutorMapper tutorMapper;

    @InjectMocks TutorServiceImpl tutorService;

    @Test
    void createTutor_mapsSavesAndReturnsDto() {
        TutorDTO inputDto = new TutorDTO();
        Tutor mappedEntity = new Tutor();
        Tutor savedEntity = new Tutor();
        TutorDTO mappedOut = new TutorDTO();

        when(tutorMapper.toEntity(inputDto)).thenReturn(mappedEntity);
        when(tutorRepository.save(mappedEntity)).thenReturn(savedEntity);
        when(tutorMapper.toDTO(savedEntity)).thenReturn(mappedOut);

        TutorDTO result = tutorService.createTutor(inputDto);

        assertSame(mappedOut, result);
        verify(tutorMapper).toEntity(inputDto);
        verify(tutorRepository).save(mappedEntity);
        verify(tutorMapper).toDTO(savedEntity);
    }

    @Test
    void getTutorByUserId_found_returnsDto() {
        Tutor entity = new Tutor();
        TutorDTO dto = new TutorDTO();
        when(tutorRepository.findByUserId("U1")).thenReturn(Optional.of(entity));
        when(tutorMapper.toDTO(entity)).thenReturn(dto);

        Optional<TutorDTO> result = tutorService.getTutorByUserId("U1");

        assertTrue(result.isPresent());
        assertSame(dto, result.get());
    }

    @Test
    void getTutorByUserId_notFound_returnsEmpty() {
        when(tutorRepository.findByUserId("NOPE")).thenReturn(Optional.empty());

        Optional<TutorDTO> result = tutorService.getTutorByUserId("NOPE");

        assertTrue(result.isEmpty());
        verify(tutorMapper, never()).toDTO(any());
    }
}
