package com.csy.springbootauthbe.wallet.repository;

import com.csy.springbootauthbe.wallet.entity.Wallet;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface WalletRepository extends MongoRepository<Wallet, String> {
    Optional<Wallet> findByStudentId(String studentId);
}
