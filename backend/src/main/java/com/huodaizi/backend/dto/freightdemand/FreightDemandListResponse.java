package com.huodaizi.backend.dto.freightdemand;

import java.util.List;

public record FreightDemandListResponse(
    List<FreightDemandItemDTO> items, Integer total, Integer page, Integer pageSize) {}
