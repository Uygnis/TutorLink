package com.csy.springbootauthbe.user.service;

import com.csy.springbootauthbe.student.dto.StudentDTO;
import com.csy.springbootauthbe.student.service.StudentService;
import com.csy.springbootauthbe.tutor.dto.TutorDTO;
import com.csy.springbootauthbe.tutor.service.TutorService;
import com.csy.springbootauthbe.user.entity.AccountStatus;
import com.csy.springbootauthbe.user.utils.AuthenticationResponse;
import com.csy.springbootauthbe.user.utils.LoginRequest;
import com.csy.springbootauthbe.user.utils.RegisterRequest;
import com.csy.springbootauthbe.user.utils.UserResponse;
import com.csy.springbootauthbe.config.JWTService;
import com.csy.springbootauthbe.user.entity.Role;
import com.csy.springbootauthbe.user.entity.User;
import com.csy.springbootauthbe.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final JWTService jwtService;
    private final AuthenticationManager authenticationManager;
    private final StudentService studentService;
    private final TutorService tutorService;

    public AuthenticationResponse register(RegisterRequest request) {

        AccountStatus status = AccountStatus.ACTIVE;

        if (repository.existsByEmail(request.getEmail())) {
            throw new DataIntegrityViolationException("Email already exists");
        }

        Role userRole = getUserRole(request);

        var user = User.builder()
                .firstname(request.getFirstname())
                .lastname(request.getLastname())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(userRole)
                .status(status)
                .build();

        repository.save(user);

        // If the user is a student, create Student entity
        if (userRole == Role.STUDENT) {
            var studentDTO = StudentDTO.builder()
                    .userId(user.getId())
                    .studentNumber(request.getStudentNumber())
                    .gradeLevel(request.getGradeLevel())
                    .build();

            studentService.createStudent(studentDTO);
        }

        // If the user is a tutor, create tutor entity
        if (userRole == Role.TUTOR){
            TutorDTO tutorDTO = TutorDTO.builder()
                    .userId(user.getId()).build();

            tutorService.createTutor(tutorDTO);
        }

        var jwtToken = jwtService.generateToken(user);

        UserResponse userObj = UserResponse.builder()
                .id(user.getId())
                .name(user.getFirstname() + " " + user.getLastname())
                .email(user.getEmail())
                .role(user.getRole())
                .status(user.getStatus())
                .token(jwtToken)
                .build();

        return AuthenticationResponse.builder()
                .message("User Registered successfully.")
                .user(userObj)
                .build();
    }


    public AuthenticationResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        var user = repository.findByEmail(request.getEmail())
                .orElseThrow();

        var jwtToken = jwtService.generateToken(user);

        UserResponse userObj = UserResponse.builder()
                .id(user.getId()) // String ID
                .name(user.getFirstname() + " " + user.getLastname())
                .email(user.getEmail())
                .role(user.getRole())
                .token(jwtToken)
                .build();

        return AuthenticationResponse.builder()
                .message("User Login successfully.")
                .user(userObj)
                .build();
    }

    private static Role getUserRole(RegisterRequest request) {
        Role userRole;
        if ("Admin".equalsIgnoreCase(request.getRole())) {
            userRole = Role.ADMIN;
        } else if ("Student".equalsIgnoreCase(request.getRole())) {
            userRole = Role.STUDENT;
        } else if ("Tutor".equalsIgnoreCase(request.getRole())) {
            userRole = Role.TUTOR;
        } else if ("User".equalsIgnoreCase(request.getRole())) {
            userRole = Role.USER;
        } else {
            throw new IllegalArgumentException("Invalid role: " + request.getRole());
        }
        return userRole;
    }
}
