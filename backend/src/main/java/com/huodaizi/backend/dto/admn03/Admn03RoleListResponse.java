package com.huodaizi.backend.dto.admn03;

import java.util.List;

public record Admn03RoleListResponse(
    int total,
    int page,
    int pageSize,
    String keyword,
    String status,
    int activeCount,
    int disabledCount,
    List<Admn03RoleListItemDTO> records) {}
