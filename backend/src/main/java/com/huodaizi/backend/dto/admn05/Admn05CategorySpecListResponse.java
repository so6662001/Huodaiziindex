package com.huodaizi.backend.dto.admn05;

import java.util.List;

public record Admn05CategorySpecListResponse(
    int total,
    int page,
    int pageSize,
    String keyword,
    String status,
    String sceneCode,
    int activeCount,
    int disabledCount,
    List<Admn05CategorySpecListItemDTO> records) {}
