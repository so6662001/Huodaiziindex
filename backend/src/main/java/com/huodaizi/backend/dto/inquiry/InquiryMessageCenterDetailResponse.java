package com.huodaizi.backend.dto.inquiry;

public record InquiryMessageCenterDetailResponse(
    InquiryMessageCenterItemDTO message,
    String content,
    String extraInfo,
    String relatedBizNo,
    String relatedBizType) {}
