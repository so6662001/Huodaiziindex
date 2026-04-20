package com.huodaizi.backend.dto.admn12;

import java.util.List;

public record Admn12AdSlotScheduleListResponse(
    int total,
    int page,
    int pageSize,
    String scheduleStatus,
    String slotType,
    String city,
    String keyword,
    int activeCount,
    int draftCount,
    int pausedCount,
    int soldOutCount,
    List<Admn12AdSlotScheduleListItemDTO> records) {}
