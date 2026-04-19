package com.huodaizi.backend.dto.inquiry;

public record InquiryH5InquiryStep1SaveResponse(
    String draftId,
    String draftStatus,
    String nextStepUrl,
    String summaryText,
    String updatedAt) {}
