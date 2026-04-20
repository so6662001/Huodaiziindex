package com.huodaizi.backend.dto.reconcilemonitor;

public record A05ReconcileMonitorOverviewDTO(
    int totalOrders,
    int createdCount,
    int invoicePendingCount,
    int invoicedCount,
    int confirmedCount,
    int partialPaidCount,
    int paidCount,
    int closedCount,
    int disputedCount,
    int riskCount,
    String paidRate,
    String disputeRate,
    String overdueRate,
    String totalReceivableAmount,
    String totalPaidAmount,
    String totalOutstandingAmount,
    String avgAgingDays) {}
