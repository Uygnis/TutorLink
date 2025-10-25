package com.csy.springbootauthbe.wallet.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "wallet_transactions")
public class WalletTransaction {
    @Id
    private String id;
    private String studentId;
    private String type; // PURCHASE, BOOKING_DEDUCT, REFUND, BONUS
    private BigDecimal amount;
    private String description;
    private String refId; // bookingId / paymentId
    private LocalDateTime createdAt = LocalDateTime.now();
}

