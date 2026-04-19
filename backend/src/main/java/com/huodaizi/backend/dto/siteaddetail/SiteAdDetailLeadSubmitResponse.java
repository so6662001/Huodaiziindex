package com.huodaizi.backend.dto.siteaddetail;

public record SiteAdDetailLeadSubmitResponse(
    String leadNo,
    String placementId,
    String placementName,
    String companyName,
    String contactNameMasked,
    String phoneMasked,
    String message) {}
