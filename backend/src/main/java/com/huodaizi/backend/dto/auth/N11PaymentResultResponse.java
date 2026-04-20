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
    String payChannel,
    String payChannelText,
    String payableAmount,
    String paidAmount,
    String outstandingAmount,
    String paidAt,
    String latestRemark,
    String orderStatus,
    String orderStatusText,
    List<N06OrderActionDTO> nextActions,
    String createdAt,
    String updatedAt,
    List<N11PaymentResultNodeDTO> nodes) {}
