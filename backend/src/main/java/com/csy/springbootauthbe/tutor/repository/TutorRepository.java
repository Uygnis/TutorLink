package com.csy.springbootauthbe.tutor.repository;

import com.csy.springbootauthbe.tutor.entity.Tutor;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface TutorRepository extends MongoRepository<Tutor, String> {
    Optional<Tutor> findByUserId(String userId);

}

