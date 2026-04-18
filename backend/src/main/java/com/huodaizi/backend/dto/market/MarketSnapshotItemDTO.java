package com.huodaizi.backend.dto.market;

public record MarketSnapshotItemDTO(
    String id,
    String productName,
    String city,
    String latestPrice,
    String highPrice,
    String lowPrice,
    String changeText) {}
