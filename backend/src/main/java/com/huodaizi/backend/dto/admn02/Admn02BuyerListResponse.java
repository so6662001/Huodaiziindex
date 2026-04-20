package com.huodaizi.backend.dto.admn02;

import java.util.List;

public record Admn02BuyerListResponse(
    int total,
    int page,
    int pageSize,
    String keyword,
    String accountStatus,
    String blacklistStatus,
    int blacklistedCount,
    int normalCount,
    List<Admn02BuyerListItemDTO> records) {}
