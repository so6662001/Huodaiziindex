package com.huodaizi.backend.dto.storagedemand;

import java.util.List;

public record StorageDemandListResponse(
    List<StorageDemandItemDTO> items, int total, int page, int pageSize) {}
