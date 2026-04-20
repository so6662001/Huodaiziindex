package com.huodaizi.backend.dto.inquiry;

public record InquiryH5InquiryStep2SubmitResponse(
    String draftId,
    String inquiryId,
    String inquiryNo,
    String nextStepUrl,
    String successMessage,
    String submittedAt) {}
