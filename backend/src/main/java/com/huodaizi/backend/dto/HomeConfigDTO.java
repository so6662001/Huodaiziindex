package com.huodaizi.backend.dto;

public record HomeConfigDTO(
    String heroTitle,
    String heroSubtitle,
    String primaryCtaText,
    String secondaryCtaText,
    String logisticsCtaText,
    String searchPlaceholder,
    String searchHotKeywords
) {}
