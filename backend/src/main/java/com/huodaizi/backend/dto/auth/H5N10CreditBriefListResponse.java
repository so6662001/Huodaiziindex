package com.huodaizi.backend.dto.auth;

import java.util.List;

public record H5N10CreditBriefListResponse(
    int pageNo,
    int pageSize,
    long total,
    String channel,
    String contactMobileMasked,
    String activeScoreId,
    List<H5N10CreditBriefListItemDTO> records) {}
