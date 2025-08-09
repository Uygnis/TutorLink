package com.csy.springbootauthbe.doctor.repository;

import com.csy.springbootauthbe.doctor.entity.Doctor;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface DoctorRepository extends MongoRepository<Doctor, String> {
}
