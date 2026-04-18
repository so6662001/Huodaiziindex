package com.huodaizi.backend.dto.sitead;

public record SiteAdAdminItemDTO(
    String id,
    String section,
    String title,
    String subtitle,
    String value,
    String extra,
    String link,
    String city,
    String placement,
    String duration,
    String companyName,
    String contactName,
    String phoneMasked,
    String budget,
    String remark,
    String status,
    boolean pinned,
    String updatedAt) {}
