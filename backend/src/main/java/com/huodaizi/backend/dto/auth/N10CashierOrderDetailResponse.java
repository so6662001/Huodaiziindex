package com.huodaizi.backend.dto.auth;

import java.util.List;

public record N10CashierOrderDetailResponse(
    String cashierOrderId,
    String orderId,
    String orderNo,
    String inquiryNo,
    String buyerCompany,
    String supplierName,
    String goodsName,
    String quantityTon,
    String payType,
    String payTypeText,
    String payableAmount,
    String paidAmount,
    String discountAmount,
    String serviceFeeAmount,
    String finalPayAmount,
    String status,
    String statusText,
    String latestRemark,
    String dueAt,
    String paidAt,
    String createdAt,
    String updatedAt,
    List<N10CashierTimelineNodeDTO> timeline) {}
