package com.huodaizi.backend.dto.inquiry;

import java.util.List;

public record InquiryListResponse(
    List<InquiryItemDTO> items,
    int total,
    int page,
    int pageSize,
    int openCount,
    int quotingCount,
    int dealDoneCount,
    int closedCount) {}
