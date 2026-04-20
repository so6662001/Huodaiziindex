package com.huodaizi.backend.dto.auth;

import java.util.List;

public record H5N07ReconcileDetailResponse(
    String reconcileId,
    String reconcileNo,
    String pickupOrderId,
    String pickupOrderNo,
    String inquiryId,
    String inquiryNo,
    String quoteId,
    String supplierName,
    String buyerCompany,
    String goodsSummary,
    String statementMonth,
    String dueDate,
    String status,
    String statusText,
    String invoiceStatus,
    String invoiceStatusText,
    String receivableAmount,
    String paidAmount,
    String outstandingAmount,
    String contactMobileMasked,
    String channel,
    String latestRemark,
    List<String> availableActions,
    String createdAt,
    String updatedAt) {}
