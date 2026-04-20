package com.huodaizi.backend.dto.auth;

import java.util.List;

public record N12InvoiceApplicationListResponse(
    int pageNo, int pageSize, long total, List<N12InvoiceApplicationListItemDTO> records) {}
