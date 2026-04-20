package com.huodaizi.backend.dto.auth;

import java.util.List;

public record N08AfterSaleDisputeListResponse(
    int pageNo, int pageSize, long total, List<N08AfterSaleDisputeListItemDTO> records) {}
