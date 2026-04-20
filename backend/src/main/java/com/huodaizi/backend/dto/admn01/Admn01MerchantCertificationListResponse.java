package com.huodaizi.backend.dto.admn01;

import java.util.List;

public record Admn01MerchantCertificationListResponse(
    long total,
    int page,
    int pageSize,
    String status,
    String keyword,
    int pendingCount,
    int approvedCount,
    int rejectedCount,
    List<Admn01MerchantCertificationItemDTO> records) {}
