package com.huodaizi.backend.dto.auth;

import java.util.List;

public record N07TradeTermsDetailResponse(
    String orderId,
    String orderNo,
    String inquiryNo,
    String buyerCompany,
    String supplierName,
    String goodsName,
    String specText,
    String quantityTon,
    String unitPrice,
    String totalAmount,
    String deliveryTerm,
    String paymentTerm,
    String invoiceTerm,
    String settlementMethod,
    String qualityStandard,
    String toleranceRange,
    String breachLiability,
    String disputeResolution,
    String otherClause,
    String effectiveDate,
    String expireDate,
    boolean confirmed,
    String confirmedBy,
    String confirmedAt,
    String confirmRemark,
    String createdAt,
    String updatedAt,
    List<N07TradeTermClauseDTO> clauses,
    List<N07TradeTermAttachmentDTO> attachments) {}
