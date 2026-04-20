package com.huodaizi.backend.dto.admn01;

public record Admn01MerchantCertificationItemDTO(
    String certificationId,
    String userId,
    String companyName,
    String unifiedSocialCreditCode,
    String contactName,
    String contactMobileMasked,
    String province,
    String city,
    String status,
    String statusText,
    String riskTag,
    String operator,
    String submittedAt,
    String updatedAt) {}
