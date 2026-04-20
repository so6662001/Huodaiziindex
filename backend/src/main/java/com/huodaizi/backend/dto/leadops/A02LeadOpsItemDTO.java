package com.huodaizi.backend.dto.leadops;

public record A02LeadOpsItemDTO(
    String source,
    String leadId,
    String leadNo,
    String status,
    String statusText,
    String owner,
    String companyName,
    String contactNameMasked,
    String contactMobileMasked,
    String city,
    String specOrPlacementName,
    String demandOrBudget,
    String latestFollow,
    String nextActionAt,
    String createdAt,
    String updatedAt) {}
