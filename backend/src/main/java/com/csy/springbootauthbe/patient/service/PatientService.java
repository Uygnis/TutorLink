package com.csy.springbootauthbe.patient.service;

import com.csy.springbootauthbe.patient.dto.PatientDTO;

import java.util.List;

public interface PatientService {
    List<PatientDTO> getAllPatients();
    PatientDTO getPatientById(String id);
    PatientDTO createPatient(PatientDTO patientDTO);
    PatientDTO updatePatient(String id, PatientDTO patientDTO);
    void deletePatient(String id);
    String generateNextPatientReferenceId();

}
