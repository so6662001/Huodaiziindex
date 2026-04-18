package com.huodaizi.backend.dto.sitecenter;

public record SiteCenterAdminItemDTO(
    String id,
    String section,
    String region,
    String cityName,
    String citySlug,
    String title,
    String subtitle,
    String value,
    String extra,
    String link,
    String status,
    boolean pinned,
    String updatedAt) {}
