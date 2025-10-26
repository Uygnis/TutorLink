package com.csy.springbootauthbe.wallet.service;

import com.csy.springbootauthbe.wallet.entity.Wallet;
import com.csy.springbootauthbe.wallet.entity.WalletTransaction;
import com.csy.springbootauthbe.wallet.repository.WalletRepository;
import com.csy.springbootauthbe.wallet.repository.WalletTransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class WalletService {

    private final WalletRepository walletRepo;
    private final WalletTransactionRepository txnRepo;

    public Wallet getWallet(String userId) {
        return walletRepo.findByStudentId(userId)
                .orElseGet(() -> walletRepo.save(new Wallet(null, userId, BigDecimal.ZERO, "SGD", LocalDateTime.now())));
    }

    @Transactional
    public Wallet addCredits(String userId, BigDecimal amount, String refId) {
        Wallet wallet = getWallet(userId);
        wallet.setBalance(wallet.getBalance().add(amount));
        wallet.setUpdatedAt(LocalDateTime.now());
        walletRepo.save(wallet);

        txnRepo.save(new WalletTransaction(null, userId, "PURCHASE", amount, "Top-up credits", refId, LocalDateTime.now()));
        return wallet;
    }

    @Transactional
    public Wallet deductCredits(String userId, BigDecimal amount, String bookingId) {
        Wallet wallet = getWallet(userId);
        if (wallet.getBalance().compareTo(amount) < 0) {
            throw new RuntimeException("Insufficient credits");
        }

        wallet.setBalance(wallet.getBalance().subtract(amount));
        wallet.setUpdatedAt(LocalDateTime.now());
        walletRepo.save(wallet);

        txnRepo.save(new WalletTransaction(null, userId, "BOOKING_DEDUCT", amount.negate(), "Booking charge", bookingId, LocalDateTime.now()));
        return wallet;
    }

    // ✅ NEW: Temporarily hold credits when booking created
    @Transactional
    public Wallet holdCredits(String studentId, BigDecimal amount, String bookingId) {
        Wallet wallet = getWallet(studentId);
        if (wallet.getBalance().compareTo(amount) < 0) {
            throw new RuntimeException("Insufficient credits to hold for booking");
        }

        wallet.setBalance(wallet.getBalance().subtract(amount));
        wallet.setUpdatedAt(LocalDateTime.now());
        walletRepo.save(wallet);

        txnRepo.save(new WalletTransaction(null, studentId, "BOOKING_HOLD", amount.negate(),
                "Hold for booking ID: " + bookingId, bookingId, LocalDateTime.now()));
        return wallet;
    }

    // ✅ NEW: Release funds to tutor on acceptance
    @Transactional
    public void releaseToTutor(String studentId, String tutorId, BigDecimal amount, String bookingId) {
        // 1️⃣ Credit tutor
        Wallet tutorWallet = getWallet(tutorId);
        tutorWallet.setBalance(tutorWallet.getBalance().add(amount));
        tutorWallet.setUpdatedAt(LocalDateTime.now());
        walletRepo.save(tutorWallet);

        txnRepo.save(new WalletTransaction(null, tutorId, "BOOKING_PAYMENT", amount,
                "Payment received for booking ID: " + bookingId, bookingId, LocalDateTime.now()));

        // 2️⃣ Record final debit for student (for trace)
        txnRepo.save(new WalletTransaction(null, studentId, "BOOKING_CONFIRMED", amount.negate(),
                "Booking confirmed - funds transferred to tutor", bookingId, LocalDateTime.now()));
    }

    // ✅ NEW: Refund if cancelled or rejected
    @Transactional
    public void refundStudent(String studentId, BigDecimal amount, String bookingId) {
        Wallet wallet = getWallet(studentId);
        wallet.setBalance(wallet.getBalance().add(amount));
        wallet.setUpdatedAt(LocalDateTime.now());
        walletRepo.save(wallet);

        txnRepo.save(new WalletTransaction(null, studentId, "BOOKING_REFUND", amount,
                "Refund for cancelled booking ID: " + bookingId, bookingId, LocalDateTime.now()));
    }

    public List<WalletTransaction> getTransactions(String userId) {
        return txnRepo.findByStudentIdOrderByCreatedAtDesc(userId);
    }
}
