package com.huodaizi.backend.dto.admn02;

import java.util.List;

public record Admn02BuyerDetailResponse(
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
    String latestBlacklistReason,
    String latestBlacklistRemark,
    String latestBlacklistOperator,
    String latestBlacklistAt,
    String lastOrderAt,
    String createdAt,
    String updatedAt,
    List<String> availableActions) {}
