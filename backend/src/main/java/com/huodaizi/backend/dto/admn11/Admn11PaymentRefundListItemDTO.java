package com.huodaizi.backend.dto.admn11;

public record Admn11PaymentRefundListItemDTO(
    String refundId,
    String refundNo,
    String refundType,
    String cashierOrderId,
    String orderNo,
    String buyerCompany,
    String supplierName,
    String payChannel,
    String payChannelText,
    String refundReasonCode,
    String refundReasonText,
    String refundStatus,
    String refundStatusText,
    String payableAmountYuan,
    String paidAmountYuan,
    String refundAmountYuan,
    String latestRemark,
    String createdAt,
    String updatedAt) {}
