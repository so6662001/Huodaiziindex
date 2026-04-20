package com.huodaizi.backend.dto.siteadlead;

import java.util.List;

public record SiteAdLeadItemDTO(
    String id,
    String leadNo,
    String placementId,
    String placementName,
    String city,
    String duration,
    String budget,
    String companyName,
    String contactNameMasked,
    String contactPhoneMasked,
    String remark,
    String owner,
    String status,
    String nextActionAt,
    List<SiteAdLeadFollowLogDTO> followLogs,
    String updatedAt) {}
