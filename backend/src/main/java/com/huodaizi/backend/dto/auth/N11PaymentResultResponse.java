package com.huodaizi.backend.dto.auth;

import java.util.List;

public record N11PaymentResultResponse(
    String cashierOrderId,
    String orderId,
    String orderNo,
    String inquiryNo,
    String buyerCompany,
    String supplierName,
    String goodsName,
    String payStatus,
    String payStatusText,
    String resultStatus,
    String resultStatusText,
    String payChannel,
    String payChannelText,
    String payableAmount,
    String paidAmount,
    String outstandingAmount,
    String paidAt,
    String latestRemark,
    String linkedOrderStatus,
    String linkedOrderStatusText,
    String orderSyncRemark,
    String nextActionHint,
    String createdAt,
    String updatedAt,
    List<N11PaymentResultNodeDTO> nodes) {}
