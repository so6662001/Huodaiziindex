package com.huodaizi.backend.dto.freight;

import java.util.List;

public record FreightListResponse(
    List<FreightItemDTO> items,
    Integer total,
    Integer page,
    Integer pageSize) {}
