package com.huodaizi.backend.dto.auth;

import java.util.List;

public record H5N09LitePayDetailResponse(
    String cashierOrderId,
    String orderId,
    String orderNo,
    String inquiryNo,
    String buyerCompany,
    String supplierName,
    String goodsName,
    String payableAmount,
    String paidAmount,
    String outstandingAmount,
    String payStatus,
    String payStatusText,
    String payChannel,
    String payChannelText,
    String dueAt,
    String paidAt,
    String latestRemark,
    String channel,
    String quickResultStatus,
    String quickResultText,
    List<String> availablePayMethods,
    String tipText,
    String createdAt,
    String updatedAt,
    List<N10CashierTimelineNodeDTO> timeline) {}
