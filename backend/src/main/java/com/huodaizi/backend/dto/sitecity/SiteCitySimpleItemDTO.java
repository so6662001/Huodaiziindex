package com.huodaizi.backend.dto.sitecity;

public record SiteCitySimpleItemDTO(
    String id,
    String title,
    String subtitle,
    String status,
    boolean pinned,
    String updatedAt) {}
