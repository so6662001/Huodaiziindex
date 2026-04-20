package com.huodaizi.backend.dto.inquiry;

public record InquiryH5ReconcileOrderQuickStatusResponse(
    String reconcileId,
    String status,
    String statusText,
    String message) {}
