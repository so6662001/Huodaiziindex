package com.huodaizi.backend.dto.inquiry;

public record InquiryReconcileOrderCreateResponse(
    String reconcileId,
    String reconcileNo,
    String pickupId,
    String status,
    String message) {}
