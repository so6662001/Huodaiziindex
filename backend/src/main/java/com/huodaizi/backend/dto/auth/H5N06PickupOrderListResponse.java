package com.huodaizi.backend.dto.auth;

import java.util.List;

public record H5N06PickupOrderListResponse(
    int pageNo,
    int pageSize,
    long total,
    String channel,
    String contactMobileMasked,
    String activePickupId,
    List<H5N06PickupOrderListItemDTO> records) {}
