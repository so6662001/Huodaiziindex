package com.huodaizi.backend.dto.freight;

public record FreightItemDTO(
    String id,
    String provider,
    String route,
    String origin,
    String destination,
    String vehicle,
    String loadRange,
    String frequency,
    String timeliness,
    String returnTruck,
    String price,
    String contactPhone,
    String status,
    boolean pinned,
    String updatedAt) {}
