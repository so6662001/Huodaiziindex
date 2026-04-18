package com.huodaizi.backend.dto.spot;

import java.util.List;

public record SpotListResponse(
    List<SpotItemDTO> items,
    Integer total,
    Integer page,
    Integer pageSize
) {}
