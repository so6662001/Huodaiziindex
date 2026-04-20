package com.huodaizi.backend.dto.auth;

import java.util.List;

public record H5N07ReconcileListResponse(
    int pageNo,
    int pageSize,
    long total,
    String channel,
    String contactMobileMasked,
    String activeReconcileId,
    List<H5N07ReconcileListItemDTO> records) {}
