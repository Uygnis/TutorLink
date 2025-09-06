package com.csy.springbootauthbe.student.controller;

import com.csy.springbootauthbe.student.dto.StudentDTO;
import com.csy.springbootauthbe.student.service.StudentService;
import com.csy.springbootauthbe.student.utils.TutorSearchRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.bson.Document;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/students")
@RequiredArgsConstructor
public class StudentController {

    private final StudentService studentService;

    @GetMapping("/by-user/{userId}")
    public ResponseEntity<StudentDTO> getStudentByUserId(@PathVariable String userId) {
        Optional<StudentDTO> studentOpt = studentService.getStudentByUserId(userId);
        return studentOpt.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("/search")
    public List<Document> searchTutors(@RequestBody TutorSearchRequest request) {
        return studentService.searchTutors(request);
    }


}
