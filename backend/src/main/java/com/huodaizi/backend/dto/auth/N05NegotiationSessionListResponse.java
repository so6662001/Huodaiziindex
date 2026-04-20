package com.huodaizi.backend.dto.auth;

import java.util.List;

public record N05NegotiationSessionListResponse(
    int pageNo, int pageSize, long total, List<N05NegotiationSessionItemDTO> records) {}
