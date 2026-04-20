package com.huodaizi.backend.dto.auth;

import java.util.List;

public record H5N08AfterSaleListResponse(
    int pageNo,
    int pageSize,
    long total,
    String channel,
    String activeDisputeId,
    List<H5N08AfterSaleListItemDTO> records) {}
