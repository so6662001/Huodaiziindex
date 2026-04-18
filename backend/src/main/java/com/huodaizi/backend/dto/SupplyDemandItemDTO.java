package com.huodaizi.backend.dto;

public record SupplyDemandItemDTO(
    String id,
    String title,
    String city,
    String category,
    String quantity,
    String updatedAt) {}
