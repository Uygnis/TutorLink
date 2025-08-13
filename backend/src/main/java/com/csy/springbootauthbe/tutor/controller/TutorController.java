package com.csy.springbootauthbe.tutor.controller;

import com.csy.springbootauthbe.tutor.dto.TutorDTO;
import com.csy.springbootauthbe.tutor.service.TutorService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping("/api/v1/tutors")
@RequiredArgsConstructor
public class TutorController {

    private final TutorService tutorService;

    @GetMapping("/by-user/{userId}")
    public ResponseEntity<TutorDTO> getTutorByUserId(@PathVariable String userId) {
        Optional<TutorDTO> tutorOpt = tutorService.getTutorByUserId(userId);
        return tutorOpt.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }


}
