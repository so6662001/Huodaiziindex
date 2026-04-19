package com.huodaizi.backend.dto.siteaddetail;

public record SiteAdDetailPlacementDTO(
    String id,
    String title,
    String subtitle,
    String price,
    String desc,
    String tags,
    String exposure,
    String clickRate,
    String recommendedCities,
    String adSpec,
    String link) {}
