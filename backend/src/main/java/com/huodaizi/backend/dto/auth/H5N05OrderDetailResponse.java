package com.huodaizi.backend.dto.auth;

import java.util.List;

public record H5N05OrderDetailResponse(
    String orderId,
    String orderNo,
    String inquiryNo,
    String goodsName,
    String specText,
    String quantityTon,
    String supplierName,
    String buyerCompany,
    String orderStatus,
    String orderStatusText,
    String paymentStatus,
    String paymentStatusText,
    String contractStatus,
    String contractStatusText,
    String expectedDeliveryAt,
    String latestRemark,
    String receivableAmount,
    String paidAmount,
    String outstandingAmount,
    String channel,
    List<String> availableActions,
    String createdAt,
    String updatedAt,
    List<H5N05OrderTimelineNodeDTO> timeline,
    List<H5N05OrderActionDTO> actions) {}
