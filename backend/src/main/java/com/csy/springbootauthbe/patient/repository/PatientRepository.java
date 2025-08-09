package com.csy.springbootauthbe.patient.repository;

import com.csy.springbootauthbe.patient.entity.Patient;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

public interface PatientRepository extends MongoRepository<Patient, String> {

    @Query(value = "{}", sort = "{ '_id': -1 }")
    Patient findFirstByOrderByIdDesc();

    default String findLastPatientReferenceId() {
        Patient lastPatient = findFirstByOrderByIdDesc();
        return lastPatient != null ? lastPatient.getPatientReferenceId() : null;
    }
}
