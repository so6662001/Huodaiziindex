package com.huodaizi.backend.dto.admn01;

import java.util.List;

public record Admn01MerchantCertificationDetailResponse(
    String certificationId,
    String userId,
    String accountMasked,
    String companyName,
    String unifiedSocialCreditCode,
    String legalPersonName,
    String legalPersonIdNoMasked,
    String contactName,
    String contactMobileMasked,
    String businessLicenseUrl,
    String legalIdFrontUrl,
    String legalIdBackUrl,
    String bankAccountName,
    String bankAccountNoMasked,
    String bankName,
    String province,
    String city,
    String address,
    String status,
    String statusText,
    String latestRemark,
    String operator,
    String submittedAt,
    String updatedAt,
    List<String> availableActions) {}
