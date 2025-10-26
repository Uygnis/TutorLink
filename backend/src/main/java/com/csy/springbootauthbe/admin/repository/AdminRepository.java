package com.csy.springbootauthbe.admin.repository;

import com.csy.springbootauthbe.admin.entity.Admin;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface AdminRepository extends MongoRepository<Admin, String> {
    Optional<Admin> findByUserId(String userId);

}

