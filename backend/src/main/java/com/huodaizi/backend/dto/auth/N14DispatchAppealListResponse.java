package com.huodaizi.backend.dto.auth;

import java.util.List;

public record N14DispatchAppealListResponse(
    int pageNo, int pageSize, long total, List<N14DispatchAppealListItemDTO> records) {}
