package com.huodaizi.backend.dto.market;

import jakarta.validation.constraints.Size;

public record MarketOverviewRequest(
    @Size(max = 32, message = "category 最大长度32") String category,
    @Size(max = 32, message = "city 最大长度32") String city,
    @Size(max = 8, message = "range 最大长度8") String range) {}
