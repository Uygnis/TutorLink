package com.csy.springbootauthbe.tutor.controller;

import com.csy.springbootauthbe.tutor.dto.TutorDTO;
import com.csy.springbootauthbe.tutor.entity.Availability;
import com.csy.springbootauthbe.tutor.entity.QualificationFile;
import com.csy.springbootauthbe.tutor.service.TutorService;
import com.csy.springbootauthbe.tutor.utils.TutorRequest;
import com.csy.springbootauthbe.tutor.utils.TutorResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

@RestController
@RequestMapping("/api/v1/tutors")
@RequiredArgsConstructor
public class TutorController {

    private final TutorService tutorService;

    @GetMapping("/{userId}")
    public ResponseEntity<TutorDTO> getTutorByUserId(@PathVariable String userId) {
        Optional<TutorDTO> tutorOpt = tutorService.getTutorByUserId(userId);
        return tutorOpt.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/{userId}")
    public ResponseEntity<TutorResponse> updateTutor(@PathVariable String userId,
                                                     @RequestParam("hourlyRate") Integer hourlyRate,
                                                     @RequestParam("subject") String subject,
                                                     @RequestParam("availability") String availabilityJson,
                                                     @RequestParam("qualifications") List<MultipartFile> qualifications) throws JsonProcessingException {

        ObjectMapper mapper = new ObjectMapper();
        Map<String, Availability> availability =
                mapper.readValue(availabilityJson, new TypeReference<>() {});
        TutorRequest request = new TutorRequest();
        request.setQualifications(qualifications);
        request.setAvailability(availability);
        request.setHourlyRate(hourlyRate.doubleValue());
        request.setSubject(subject);
        request.setUserId(userId);
        TutorResponse response = tutorService.updateTutor(userId, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<String> deleteTutor(@PathVariable String userId){
        tutorService.deleteTutor(userId);
        return ResponseEntity.ok("Tutor info deleted successfully.");
    }


}
