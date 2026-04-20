package com.huodaizi.backend.dto.auth;

import java.util.List;

public record N09AfterSaleProgressListResponse(
    int pageNo, int pageSize, long total, List<N09AfterSaleProgressListItemDTO> records) {}
