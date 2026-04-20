package com.huodaizi.backend.repository.auth;

public record EnterpriseCertificationDraft(
    String companyName,
    String unifiedSocialCreditCode,
    String legalPersonName,
    String legalPersonIdNo,
    String contactName,
    String contactMobile,
    String businessLicenseUrl,
    String legalIdFrontUrl,
    String legalIdBackUrl,
    String bankAccountName,
    String bankAccountNo,
    String bankName,
    String province,
    String city,
    String address,
    String remark,
    String operator) {}
