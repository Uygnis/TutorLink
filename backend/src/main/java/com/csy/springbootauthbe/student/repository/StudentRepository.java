package com.csy.springbootauthbe.student.repository;

import com.csy.springbootauthbe.student.entity.Student;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface StudentRepository extends MongoRepository<Student, String> {
    Optional<Student> findByUserId(String userId);

}

