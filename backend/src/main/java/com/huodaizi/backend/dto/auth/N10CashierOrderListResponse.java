package com.huodaizi.backend.dto.auth;

import java.util.List;

public record N10CashierOrderListResponse(
    int pageNo, int pageSize, long total, List<N10CashierOrderListItemDTO> records) {}
