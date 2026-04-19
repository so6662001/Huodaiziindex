package com.huodaizi.backend.dto.siteaddetail;

public record SiteAdDetailAdminItemDTO(
    String id,
    String placementId,
    String section,
    String city,
    String title,
    String subtitle,
    String value,
    String extra,
    String link,
    String duration,
    String companyName,
    String contactNameMasked,
    String phoneMasked,
    String budget,
    String remark,
    String status,
    boolean pinned,
    String updatedAt) {}
