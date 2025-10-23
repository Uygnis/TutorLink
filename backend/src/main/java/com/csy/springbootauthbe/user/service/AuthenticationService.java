package com.csy.springbootauthbe.user.service;

import com.csy.springbootauthbe.admin.dto.AdminDTO;
import com.csy.springbootauthbe.admin.service.AdminService;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    private final AdminService adminService;

    private static final Logger logger = LoggerFactory.getLogger(AuthenticationService.class);

    public AuthenticationResponse register(RegisterRequest request) {
        logger.info("Register request received: email={}, role={}", request.getEmail(), request.getRole());

        if (repository.existsByEmail(request.getEmail())) {
            logger.warn("Registration failed: Email already exists - {}", request.getEmail());
            throw new DataIntegrityViolationException("Email already exists");
        }

        Role userRole = getUserRole(request);

        AccountStatus status = userRole == Role.TUTOR ?
            AccountStatus.PENDING_APPROVAL : AccountStatus.ACTIVE;

        var user = User.builder()
                .firstname(request.getFirstname())
                .lastname(request.getLastname())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(userRole)
                .status(status)
                .build();

        repository.save(user);
        logger.info("User saved successfully: id={}, email={}", user.getId(), user.getEmail());

        // Create student entity if role is STUDENT
        if (userRole == Role.STUDENT) {
            var studentDTO = StudentDTO.builder()
                    .userId(user.getId())
                    .studentNumber(request.getStudentNumber())
                    .gradeLevel(request.getGradeLevel())
                    .build();
            studentService.createStudent(studentDTO);
            logger.info("Student entity created for userId={}", user.getId());
        }

        // Create tutor entity if role is TUTOR
        if (userRole == Role.TUTOR) {
            TutorDTO tutorDTO = TutorDTO.builder().userId(user.getId()).subject(request.getSubject()).build();
            tutorService.createTutor(tutorDTO);
            logger.info("Tutor entity created for userId={}", user.getId());
        }

        // Create admin entity if role is ADMIN
        if (userRole == Role.ADMIN) {
            AdminDTO adminDTO = AdminDTO.builder()
                .userId(user.getId())
                .permissions(request.getPermissions())
                .build();
            adminService.createAdmin(adminDTO);
            logger.info("Admin entity created for userId={}", user.getId());
        }



        var jwtToken = jwtService.generateToken(user);
        logger.info("JWT generated for userId={}", user.getId());

        UserResponse userObj = UserResponse.builder()
                .id(user.getId())
                .name(user.getFirstname() + " " + user.getLastname())
                .email(user.getEmail())
                .role(user.getRole())
                .status(user.getStatus())
                .token(jwtToken)
                .build();

        logger.info("Registration successful for userId={}", user.getId());

        return AuthenticationResponse.builder()
                .message("User Registered successfully.")
                .user(userObj)
                .build();
    }

    public AuthenticationResponse login(LoginRequest request) {
        logger.info("Login request received: email={}", request.getEmail());
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
            );
            logger.info("Authentication successful for email={}", request.getEmail());
        } catch (Exception e) {
            logger.error("Authentication failed for email={}. Error: {}", request.getEmail(), e.getMessage());
            throw e;
        }

        var user = repository.findByEmail(request.getEmail())
                .orElseThrow(() -> {
                    logger.error("User not found for email={}", request.getEmail());
                    return new IllegalArgumentException("User not found");
                });

        if (user.getStatus() == AccountStatus.SUSPENDED) {
            logger.warn("Login blocked: User is suspended. userId={}", user.getId());
            throw new RuntimeException("Your account has been suspended. Please contact administrator for support.");
        }

        if (user.getStatus() == AccountStatus.DELETED) {
            logger.warn("Login blocked: User is deleted. userId={}", user.getId());
            throw new RuntimeException("Your account has been deleted. Please contact administrator for support.");
        }

        var jwtToken = jwtService.generateToken(user);
        logger.info("JWT generated for login: userId={}", user.getId());

        UserResponse userObj = UserResponse.builder()
                .id(user.getId())
                .name(user.getFirstname() + " " + user.getLastname())
                .email(user.getEmail())
                .role(user.getRole())
                .token(jwtToken)
                .build();

        logger.info("Login successful for userId={}", user.getId());

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
