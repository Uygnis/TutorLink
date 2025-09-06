package com.csy.springbootauthbe.common.sequence;

import com.csy.springbootauthbe.student.service.StudentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/v1/seq")
@RequiredArgsConstructor
public class SequenceGeneratorController {

    private final SequenceGeneratorService sequenceGeneratorService;

    @GetMapping("/next-id")
    public ResponseEntity<String> getNextStudentId() {
        String nextId = sequenceGeneratorService.peekNextStudentId();
        return ResponseEntity.ok(nextId);
    }
}
