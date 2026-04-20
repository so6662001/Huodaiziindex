package com.huodaizi.backend.dto.inquiry;

public record InquiryMerchantLeadItemDTO(
    String leadId,
    String inquiryId,
    String inquiryNo,
    String supplierId,
    String supplierName,
    String specText,
    String demandQtyTon,
    String deliveryCity,
    String invoiceNeed,
    String buyerContactMasked,
    String buyerMobileMasked,
    String quoteDeadlineAt,
    String status,
    String owner,
    String latestFollow,
    String updatedAt) {}
