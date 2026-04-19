package com.huodaizi.backend.dto.market;

public record MarketCategoryQuoteDTO(
    String id, String name, String subtitle, String price, String trend, String updatedAt) {}
