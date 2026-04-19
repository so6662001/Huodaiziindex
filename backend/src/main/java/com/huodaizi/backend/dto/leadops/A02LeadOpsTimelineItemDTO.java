package com.huodaizi.backend.dto.leadops;

public record A02LeadOpsTimelineItemDTO(
    String eventType, String operator, String content, String nextActionAt, String createdAt) {}
