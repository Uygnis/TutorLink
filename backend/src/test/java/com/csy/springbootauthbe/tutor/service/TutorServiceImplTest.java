package com.csy.springbootauthbe.tutor.service;

import com.csy.springbootauthbe.tutor.dto.TutorDTO;
import com.csy.springbootauthbe.tutor.entity.Tutor;
import com.csy.springbootauthbe.tutor.mapper.TutorMapper;
import com.csy.springbootauthbe.tutor.repository.TutorRepository;
import com.csy.springbootauthbe.user.entity.AccountStatus;
import com.csy.springbootauthbe.user.entity.User;
import com.csy.springbootauthbe.user.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class TutorServiceImplTest {

    @Mock UserRepository userRepository;
    @Mock TutorRepository tutorRepository;
    @Mock TutorMapper tutorMapper;

    @InjectMocks TutorServiceImpl tutorService;

    @Test
    void createTutor_mapsSavesAndReturnsDto() {
        // Arrange
        TutorDTO in = new TutorDTO();
        in.setUserId("U1");
        Tutor mapped = new Tutor();
        Tutor saved = new Tutor();
        TutorDTO out = new TutorDTO();

        when(tutorMapper.toEntity(in)).thenReturn(mapped);
        when(tutorRepository.save(mapped)).thenReturn(saved);
        when(tutorMapper.toDTO(saved)).thenReturn(out);

        // Act
        TutorDTO result = tutorService.createTutor(in);

        // Assert
        assertSame(out, result);
        verify(tutorMapper).toEntity(in);
        verify(tutorRepository).save(mapped);
        verify(tutorMapper).toDTO(saved);
    }

    @Test
    void getTutorByUserId_userMissing_returnsEmpty() {
        when(userRepository.findById("U404")).thenReturn(Optional.empty());
        when(tutorRepository.findByUserId("U404")).thenReturn(Optional.empty());

        var result = tutorService.getTutorByUserId("U404");

        assertTrue(result.isEmpty());
    }

    @Test
    void getTutorByUserId_userInactive_returnsEmpty() {
        User u = new User();
        u.setId("U2");
        u.setStatus(AccountStatus.SUSPENDED); // or INACTIVE

        when(userRepository.findById("U2")).thenReturn(Optional.of(u));
        when(tutorRepository.findByUserId("U2")).thenReturn(Optional.of(new Tutor()));

        var result = tutorService.getTutorByUserId("U2");

        assertTrue(result.isEmpty());
    }

    @Test
    void getTutorByUserId_ok_returnsDto() {
        User u = new User();
        u.setId("U1");
        u.setStatus(AccountStatus.ACTIVE);

        Tutor entity = new Tutor();
        TutorDTO dto = new TutorDTO();
        dto.setUserId("U1");

        when(userRepository.findById("U1")).thenReturn(Optional.of(u));
        when(tutorRepository.findByUserId("U1")).thenReturn(Optional.of(entity));
        when(tutorMapper.toDTO(entity)).thenReturn(dto);

        var result = tutorService.getTutorByUserId("U1");

        assertTrue(result.isPresent());
        assertEquals("U1", result.get().getUserId());
    }
}
