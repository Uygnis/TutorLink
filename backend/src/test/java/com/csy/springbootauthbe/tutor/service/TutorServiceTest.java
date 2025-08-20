package com.csy.springbootauthbe.tutor.service;

import com.csy.springbootauthbe.tutor.dto.TutorDTO;
import com.csy.springbootauthbe.tutor.mapper.TutorMapper;
import com.csy.springbootauthbe.tutor.repository.TutorRepository;
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

    private User tutorUser;

    @BeforeEach
    void setUp() {
        tutorUser = new User();
        tutorUser.setId("Tutor1");
        tutorUser.setFirstname("Tutor");
        tutorUser.setLastname("User");
        tutorUser.setEmail("tutor@example.com");
        tutorUser.setPassword("password");
        tutorUser.setRole(Role.TUTOR);
        tutorUser.setStatus(AccountStatus.ACTIVE);
    }

    @Test
    void createTutor_shouldReturnTutorDTO() {
        //given
        TutorDTO tutorDTO = TutorDTO.builder().userId(tutorUser.getId()).build();
        //when
        TutorDTO saved = tutorService.createTutor(tutorDTO);
        //then
        assertNotNull(saved);
        assertEquals("Tutor1", saved.getUserId());
    }


}
