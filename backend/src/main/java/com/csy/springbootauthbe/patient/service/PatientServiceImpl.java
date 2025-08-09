package com.csy.springbootauthbe.patient.service;

import com.csy.springbootauthbe.exception.ResourceNotFoundException;
import com.csy.springbootauthbe.patient.dto.PatientDTO;
import com.csy.springbootauthbe.patient.entity.Patient;
import com.csy.springbootauthbe.patient.mapper.PatientMapper;
import com.csy.springbootauthbe.patient.repository.PatientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PatientServiceImpl implements PatientService {

    private final PatientRepository patientRepository;
    private final PatientMapper patientMapper;

    @Override
    public List<PatientDTO> getAllPatients() {
        return patientRepository.findAll().stream()
                .filter(patient -> !"DELETED".equalsIgnoreCase(patient.getStatus()))
                .map(patientMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public PatientDTO getPatientById(String id) {
        Patient patient = patientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found with id: " + id));
        return patientMapper.toDto(patient);
    }

    @Override
    public PatientDTO createPatient(PatientDTO patientDTO) {
        Patient patient = patientMapper.toEntity(patientDTO);
        patient.setStatus("ACTIVE"); // Default status
        Patient saved = patientRepository.save(patient);
        return patientMapper.toDto(saved);
    }

    @Override
    public PatientDTO updatePatient(String id, PatientDTO patientDTO) {
        Patient patient = patientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found with id: " + id));

        patientMapper.updateEntityFromDto(patientDTO, patient);
        Patient updated = patientRepository.save(patient);
        return patientMapper.toDto(updated);
    }

    @Override
    public void deletePatient(String id) {
        Patient patient = patientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found with id: " + id));
        patient.setStatus("DELETED");
        patientRepository.save(patient);
    }

    @Override
    public String generateNextPatientReferenceId() {
        String lastId = patientRepository.findLastPatientReferenceId();
        int nextNumber = 1;

        if (lastId != null && lastId.startsWith("PTT_")) {
            String numericPart = lastId.substring(4); // Remove "PTT_"
            try {
                nextNumber = Integer.parseInt(numericPart) + 1;
            } catch (NumberFormatException e) {
                // fallback to 1
            }
        }

        return String.format("PTT_%03d", nextNumber); // PTT_001 format
    }

}
