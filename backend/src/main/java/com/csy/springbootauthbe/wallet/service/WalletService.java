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

    // Temporarily hold credits when booking created
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

    // Release funds to tutor on acceptance
    /**
     * Release funds to tutor on acceptance.
     * - Tutor receives 95% of the booking amount.
     * - 5% commission is credited to the COMPANY wallet.
     */
    @Transactional
    public void releaseToTutor(String studentId, String tutorId, BigDecimal amount, String bookingId) {
        BigDecimal commissionRate = new BigDecimal("0.05");
        BigDecimal commission = amount.multiply(commissionRate);
        BigDecimal tutorAmount = amount.subtract(commission);

        // Get wallets
        Wallet tutorWallet = getWallet(tutorId);
        Wallet companyWallet = getWallet("COMPANY_WALLET");

        // 1 Credit tutor 95%
        tutorWallet.setBalance(tutorWallet.getBalance().add(tutorAmount));
        tutorWallet.setUpdatedAt(LocalDateTime.now());
        walletRepo.save(tutorWallet);

        txnRepo.save(new WalletTransaction(
                null,
                tutorId,
                "BOOKING_PAYMENT_TUTOR",
                tutorAmount,
                "Payment (95%) for booking ID: " + bookingId,
                bookingId,
                LocalDateTime.now()
        ));

        // 2 Credit 5% to company wallet
        companyWallet.setBalance(companyWallet.getBalance().add(commission));
        companyWallet.setUpdatedAt(LocalDateTime.now());
        walletRepo.save(companyWallet);

        txnRepo.save(new WalletTransaction(
                null,
                "COMPANY_WALLET",
                "BOOKING_COMMISSION",
                commission,
                "5% commission from booking ID: " + bookingId,
                bookingId,
                LocalDateTime.now()
        ));

        // 3️⃣ Record final debit for student (for trace)
        txnRepo.save(new WalletTransaction(
                null,
                studentId,
                "BOOKING_CONFIRMED",
                amount.negate(),
                "Booking confirmed - funds split to tutor and company",
                bookingId,
                LocalDateTime.now()
        ));
    }

    // Refund if cancelled or rejected
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
