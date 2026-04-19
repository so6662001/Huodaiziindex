package com.huodaizi.backend.dto.warehouse;

import java.util.List;

public record WarehouseListResponse(
    List<WarehouseItemDTO> items,
    Integer total,
    Integer page,
    Integer pageSize) {}
