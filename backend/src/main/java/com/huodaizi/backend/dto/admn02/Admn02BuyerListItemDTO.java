package com.huodaizi.backend.dto.admn02;

public record Admn02BuyerListItemDTO(
    String userId,
    String accountMasked,
    String companyName,
    String contactName,
    String contactMobileMasked,
    String roleCode,
    String accountStatus,
    String accountStatusText,
    boolean blacklisted,
    String blacklistStatusText,
    String riskLevel,
    String lastOrderAt,
    String createdAt,
    String updatedAt) {}
