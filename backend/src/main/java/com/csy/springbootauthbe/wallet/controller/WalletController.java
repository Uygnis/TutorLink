package com.csy.springbootauthbe.wallet.controller;

import com.csy.springbootauthbe.wallet.entity.Wallet;
import com.csy.springbootauthbe.wallet.entity.WalletTransaction;
import com.csy.springbootauthbe.wallet.service.WalletService;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/wallet")
@RequiredArgsConstructor
public class WalletController {

    private final WalletService walletService;

    //  Get current wallet balance
    @GetMapping("/{studentId}")
    public ResponseEntity<Wallet> getWallet(@PathVariable String studentId) {
        return ResponseEntity.ok(walletService.getWallet(studentId));
    }

    // Manually top-up credits (used by success redirect)
    @PostMapping("/topup")
    public ResponseEntity<Wallet> topUp(@RequestBody Map<String, Object> req) {
        String studentId = (String) req.get("studentId");
        BigDecimal amount = new BigDecimal(req.get("amount").toString());
        String refId = UUID.randomUUID().toString();
        return ResponseEntity.ok(walletService.addCredits(studentId, amount, refId));
    }

    // Deduct credits for bookings
    @PostMapping("/deduct")
    public ResponseEntity<Wallet> deduct(@RequestBody Map<String, Object> req) {
        String studentId = (String) req.get("studentId");
        BigDecimal amount = new BigDecimal(req.get("amount").toString());
        String bookingId = (String) req.get("bookingId");
        return ResponseEntity.ok(walletService.deductCredits(studentId, amount, bookingId));
    }

    // View all wallet transactions
    @GetMapping("/transactions/{studentId}")
    public ResponseEntity<List<WalletTransaction>> getTxns(@PathVariable String studentId) {
        return ResponseEntity.ok(walletService.getTransactions(studentId));
    }

    //  Create Stripe Checkout session (NO webhook needed)
    @PostMapping("/create-checkout-session")
    public ResponseEntity<Map<String, Object>> createCheckoutSession(@RequestBody Map<String, Object> req) throws Exception {
        String studentId = (String) req.get("studentId");
        BigDecimal amount = new BigDecimal(req.get("amount").toString());
        long amountInCents = amount.multiply(BigDecimal.valueOf(100)).longValue();

        SessionCreateParams params = SessionCreateParams.builder()
                .setMode(SessionCreateParams.Mode.PAYMENT)
                .setSuccessUrl("http://localhost:5173/wallet/success?studentId=" + studentId + "&amount=" + amount)
                .setCancelUrl("http://localhost:5173/wallet/cancel")
                .addLineItem(
                        SessionCreateParams.LineItem.builder()
                                .setQuantity(1L)
                                .setPriceData(
                                        SessionCreateParams.LineItem.PriceData.builder()
                                                .setCurrency("sgd")
                                                .setUnitAmount(amountInCents)
                                                .setProductData(
                                                        SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                                                .setName("TutorLink Credits (" + amount + " credits)")
                                                                .build()
                                                )
                                                .build()
                                )
                                .build()
                )
                .build();

        Session session = Session.create(params);

        return ResponseEntity.ok(Map.of("url", session.getUrl()));
    }
}
