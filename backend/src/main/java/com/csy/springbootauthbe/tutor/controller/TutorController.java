package com.csy.springbootauthbe.tutor.controller;

import com.csy.springbootauthbe.tutor.dto.TutorDTO;
import com.csy.springbootauthbe.tutor.service.TutorService;
import com.csy.springbootauthbe.tutor.utils.TutorRequest;
import com.csy.springbootauthbe.tutor.utils.TutorResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @PutMapping("/{userId}")
    public ResponseEntity<TutorResponse> updateTutor(@PathVariable String userId, @RequestBody TutorRequest request){
        TutorResponse response = tutorService.updateTutor(userId, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<String> deleteTutor(@PathVariable String userId){
        tutorService.deleteTutor(userId);
        return ResponseEntity.ok("Tutor info deleted successfully.");
    }


}
