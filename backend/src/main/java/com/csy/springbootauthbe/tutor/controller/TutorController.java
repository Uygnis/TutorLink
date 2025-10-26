package com.csy.springbootauthbe.tutor.controller;

import com.csy.springbootauthbe.student.dto.StudentDTO;
import com.csy.springbootauthbe.tutor.dto.TutorDTO;
import com.csy.springbootauthbe.tutor.entity.Availability;
import com.csy.springbootauthbe.tutor.entity.QualificationFile;
import com.csy.springbootauthbe.tutor.service.TutorService;
import com.csy.springbootauthbe.tutor.utils.TutorRequest;
import com.csy.springbootauthbe.tutor.utils.TutorResponse;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
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
                                                     @RequestParam("description") String description,
                                                     @RequestParam("lessonType") String lessonTypeJson,
                                                     @RequestParam("subject") String subject,
                                                     @RequestParam("availability") String availabilityJson,
                                                     @RequestParam(value  = "qualifications", required = false) String qualificationsJson,
                                                     @RequestParam(value  = "fileUploads", required = false) List<MultipartFile> fileUploads) throws IOException, NoSuchAlgorithmException {

        ObjectMapper mapper = new ObjectMapper();
        List<String> lessonType = mapper.readValue(lessonTypeJson, new TypeReference<List<String>>() {});
        Map<String, Availability> availability =
                mapper.readValue(availabilityJson, new TypeReference<>() {});
        List<QualificationFile> qualifications = mapper.readValue(
                qualificationsJson,
                new TypeReference<>() {}
        );
        TutorRequest request = new TutorRequest();
        request.setQualifications(qualifications);
        request.setFileUploads(fileUploads);
        request.setAvailability(availability);
        request.setHourlyRate(hourlyRate.doubleValue());
        request.setSubject(subject);
        request.setDescription(description);
        request.setLessonType(lessonType);
        request.setUserId(userId);
        TutorResponse response = tutorService.updateTutor(userId, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<String> deleteTutor(@PathVariable String userId){
        tutorService.deleteTutor(userId);
        return ResponseEntity.ok("Tutor info deleted successfully.");
    }

    @PostMapping("/{id}/profile-picture")
    public ResponseEntity<TutorDTO> uploadProfilePicture(@PathVariable String id,
                                                           @RequestParam("file") MultipartFile file) {
        return ResponseEntity.ok(tutorService.updateProfilePicture(id, file));
    }

    @PostMapping("/{tutorId}/review")
    public ResponseEntity<TutorDTO> addReview(
            @PathVariable String tutorId,
            @RequestBody Map<String, Object> body) {

        String bookingId = (String) body.get("bookingId");
        String studentName = (String) body.get("studentName");
        int rating = (int) body.get("rating");
        String comment = (String) body.get("comment");

        TutorDTO updatedTutor = tutorService.addReview(tutorId, bookingId, studentName, rating, comment);
        return ResponseEntity.ok(updatedTutor);
    }





}
