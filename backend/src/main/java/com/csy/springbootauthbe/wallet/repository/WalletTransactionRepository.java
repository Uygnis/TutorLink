package com.csy.springbootauthbe.wallet.repository;

import com.csy.springbootauthbe.wallet.entity.WalletTransaction;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface WalletTransactionRepository extends MongoRepository<WalletTransaction, String> {
    List<WalletTransaction> findByStudentIdOrderByCreatedAtDesc(String studentId);
}