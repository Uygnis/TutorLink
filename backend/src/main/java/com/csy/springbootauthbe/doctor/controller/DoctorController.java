package com.csy.springbootauthbe.doctor.controller;

import com.csy.springbootauthbe.doctor.dto.DoctorDTO;
import com.csy.springbootauthbe.doctor.service.DoctorService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/doctors")
@RequiredArgsConstructor
public class DoctorController {

    private final DoctorService doctorService;

    /** Get all doctors */
    @GetMapping
    public ResponseEntity<List<DoctorDTO>> getAllDoctors() {
        List<DoctorDTO> doctors = doctorService.getAllDoctors();
        return ResponseEntity.ok(doctors);
    }

    /** Get Doctor By Id */
    @GetMapping("/{id}")
    public ResponseEntity<DoctorDTO> getDoctorById(@PathVariable String id) throws Exception {
        DoctorDTO doctor = doctorService.getDoctorById(id);
        return ResponseEntity.ok(doctor);
    }

    /** Create New Doctor */
    @PostMapping
    public ResponseEntity<DoctorDTO> createDoctor(@RequestBody DoctorDTO doctorDto) {
        DoctorDTO created = doctorService.createDoctor(doctorDto);
        return ResponseEntity.ok(created);
    }

    /** Update Doctor By Id */
    @PutMapping("/{id}")
    public ResponseEntity<DoctorDTO> updateDoctor(@PathVariable String id, @RequestBody DoctorDTO doctorDto) throws Exception {
        DoctorDTO updated = doctorService.updateDoctor(id, doctorDto);
        return ResponseEntity.ok(updated);
    }

    /** Delete Doctor By Id */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDoctor(@PathVariable String id) throws Exception {
        doctorService.deleteDoctor(id);
        return ResponseEntity.noContent().build();
    }
}
