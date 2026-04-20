package com.huodaizi.backend.dto.auth;

import java.util.List;

public record N13CreditScoreListResponse(
    int pageNo, int pageSize, long total, List<N13CreditScoreListItemDTO> records) {}
