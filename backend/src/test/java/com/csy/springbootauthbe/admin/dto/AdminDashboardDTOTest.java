package com.csy.springbootauthbe.admin.dto;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class AdminDashboardDTOTest {

    @Test
    void testBuilderAndNestedClasses() {
        AdminDashboardDTO.TransactionSummary summary =
            AdminDashboardDTO.TransactionSummary.builder()
                .description("Lesson Payment")
                .amount(120.0).build();

        AdminDashboardDTO.MonthlyEarnings earnings =
            AdminDashboardDTO.MonthlyEarnings.builder()
                .month("Jan").total(300.0).build();

        AdminDashboardDTO.TransactionMetrics metrics =
            AdminDashboardDTO.TransactionMetrics.builder()
                .totalEarnings(1000.0)
                .commissionCollected(100.0)
                .highestTransaction(summary)
                .monthlyEarnings(java.util.List.of(earnings))
                .build();

        AdminDashboardDTO dto = AdminDashboardDTO.builder()
            .totalAdmins(2).activeTutors(10).transactionMetrics(metrics).build();

        assertEquals(2, dto.getTotalAdmins());
        assertEquals(1000.0, dto.getTransactionMetrics().getTotalEarnings());
        assertEquals("Lesson Payment", summary.getDescription());
        assertEquals("Jan", earnings.getMonth());
    }
}
