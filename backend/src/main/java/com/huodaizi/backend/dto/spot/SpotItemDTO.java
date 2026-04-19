package com.huodaizi.backend.dto.spot;

public record SpotItemDTO(
    String id,
    String title,
    String seller,
    String category,
    String spec,
    String tonnage,
    String city,
    String price,
    String delivery,
    String contactPhone,
    String updatedAt,
    String status,
    boolean pinned) {}
