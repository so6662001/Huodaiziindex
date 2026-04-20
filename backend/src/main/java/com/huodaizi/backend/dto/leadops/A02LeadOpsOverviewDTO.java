package com.huodaizi.backend.dto.leadops;

public record A02LeadOpsOverviewDTO(
    int total,
    int inquiryLeadTotal,
    int adLeadTotal,
    int newCount,
    int followingCount,
    int quotedOrProposalCount,
    int wonOrConvertedCount,
    int closedOrLostCount) {}
