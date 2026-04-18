package com.huodaizi.backend.dto.transportdemand;

import java.util.List;

public record TransportDemandListResponse(
    List<TransportDemandItemDTO> items, Integer total, Integer page, Integer pageSize) {}
