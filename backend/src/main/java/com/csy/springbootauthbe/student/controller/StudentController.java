package com.csy.springbootauthbe.student.controller;

import com.csy.springbootauthbe.student.dto.StudentDTO;
import com.csy.springbootauthbe.student.dto.TutorProfileDTO;
import com.csy.springbootauthbe.student.service.StudentService;
import com.csy.springbootauthbe.student.utils.TutorSearchRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.bson.Document;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/students")
@RequiredArgsConstructor
@Slf4j
public class StudentController {

    private final StudentService studentService;

    @GetMapping("/by-user/{userId}")
    public ResponseEntity<StudentDTO> getStudentByUserId(@PathVariable String userId) {
        log.info("Controller: Fetching student for userId={}", userId);

        Optional<StudentDTO> studentOpt = studentService.getStudentByUserId(userId);

        if (studentOpt.isPresent()) {
            log.info("Controller: Found student DTO: {}", studentOpt.get());
            return ResponseEntity.ok(studentOpt.get());
        } else {
            log.warn("Controller: No student found for userId={}", userId);
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/search")
    public List<TutorProfileDTO> searchTutors(@RequestBody TutorSearchRequest request) {
        return studentService.searchTutors(request);
    }

    @GetMapping("/tutors/{id}")
    public ResponseEntity<TutorProfileDTO> getTutorById(@PathVariable String id) {
        return studentService.getTutorById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/{id}/profile-picture")
    public ResponseEntity<StudentDTO> uploadProfilePicture(@PathVariable String id,
                                                           @RequestParam("file") MultipartFile file) {
        return ResponseEntity.ok(studentService.updateProfilePicture(id, file));
    }



}
