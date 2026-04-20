package com.huodaizi.backend.dto.auth;

import java.util.List;

public record N06OrderListResponse(
    int pageNo, int pageSize, long total, List<N06OrderListItemDTO> records) {}
