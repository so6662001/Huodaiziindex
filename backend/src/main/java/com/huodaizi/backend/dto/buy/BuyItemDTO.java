package com.huodaizi.backend.dto.buy;

public record BuyItemDTO(
    String id,
    String title,
    String buyer,
    String category,
    String spec,
    String demand,
    String city,
    String budget,
    String arrival,
    String contactPhone,
    String updatedAt,
    String status,
    boolean pinned) {}
