package com.huodaizi.backend.dto.auth;

import java.util.List;

public record H5N09LitePayListResponse(
    int pageNo,
    int pageSize,
    long total,
    String channel,
    String contactMobileMasked,
    String activeCashierOrderId,
    List<H5N09LitePayListItemDTO> records) {}
