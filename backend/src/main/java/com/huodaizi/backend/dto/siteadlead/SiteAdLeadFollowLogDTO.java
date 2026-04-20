package com.huodaizi.backend.dto.siteadlead;

public record SiteAdLeadFollowLogDTO(
    String id,
    String leadId,
    String action,
    String content,
    String nextActionAt,
    String operator,
    String createdAt) {}
