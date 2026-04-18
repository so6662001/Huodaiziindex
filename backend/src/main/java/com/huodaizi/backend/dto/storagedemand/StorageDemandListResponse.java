package com.huodaizi.backend.dto.storagedemand;

import java.util.List;

public record StorageDemandListResponse(
    List<StorageDemandItemDTO> items,
    Integer total,
    Integer page,
    Integer pageSize) {}
