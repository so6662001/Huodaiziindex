package com.huodaizi.backend.dto.siteadlead;

public record SiteAdLeadSubmitResponse(
    String leadId,
    String leadNo,
    String placementId,
    String placementName,
    String status,
    String message) {}
