package com.huodaizi.backend.dto.freightdetail;

public record FreightDetailRelatedLineDTO(
    String id,
    String provider,
    String route,
    String timeliness,
    String quote,
    String link) {}
