package com.huodaizi.backend.dto.inquiry;

import java.util.List;

public record InquiryH5InquiryStep3Response(
    String draftId,
    String inquiryId,
    String inquiryNo,
    String inquiryStatus,
    String specText,
    String deliveryCity,
    String demandQtyTon,
    String contactMobileMasked,
    String createdAt,
    int quoteCount,
    List<String> nextSteps,
    String compareUrl,
    String tipText) {}
