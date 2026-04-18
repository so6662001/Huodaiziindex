package com.huodaizi.backend.dto;

public record MarketQuoteDTO(
    String symbol,
    String city,
    String price,
    String trend
) {}
