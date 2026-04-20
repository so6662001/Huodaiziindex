package com.huodaizi.backend.dto.inquiry;

import java.util.List;

public record InquiryBillingOrderListResponse(
    String merchantId,
    List<InquiryBillingOrderItemDTO> items,
    int total,
    int page,
    int pageSize,
    int unpaidCount,
    int partialPaidCount,
    int paidCount,
    String totalReceivableAmount,
    String totalPaidAmount,
    String totalOutstandingAmount) {}
