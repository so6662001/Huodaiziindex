package com.huodaizi.backend.dto.buy;

import java.util.List;

public record BuyListResponse(
    List<BuyItemDTO> items,
    Integer total,
    Integer page,
    Integer pageSize) {}
