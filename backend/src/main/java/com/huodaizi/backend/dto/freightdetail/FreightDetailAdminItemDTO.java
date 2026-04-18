package com.huodaizi.backend.dto.freightdetail;

public record FreightDetailAdminItemDTO(
    String id,
    String freightId,
    String section,
    String title,
    String subtitle,
    String value,
    String extra,
    String link,
    String status,
    boolean pinned,
    String updatedAt) {}
