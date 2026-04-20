package com.huodaizi.backend.dto.inquiry;

public record InquiryH5ReconcileOrderCreateResponse(
    String reconcileId,
    String reconcileNo,
    String pickupOrderId,
    String status,
    String statusText,
    String nextStepUrl,
    String message) {}
