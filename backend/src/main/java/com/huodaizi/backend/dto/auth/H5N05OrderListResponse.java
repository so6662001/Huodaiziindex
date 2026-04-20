package com.huodaizi.backend.dto.auth;

import java.util.List;

public record H5N05OrderListResponse(
    int pageNo,
    int pageSize,
    long total,
    String channel,
    String activeOrderId,
    List<H5N05OrderListItemDTO> records) {}
