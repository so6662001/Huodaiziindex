package com.huodaizi.backend.dto.admn12;

import java.util.List;

public record Admn12AdSlotScheduleDetailResponse(
    String scheduleId,
    String scheduleNo,
    String adSlotCode,
    String slotName,
    String slotType,
    String slotTypeText,
    String cityCode,
    String cityName,
    String scheduleStatus,
    String scheduleStatusText,
    String fillStatus,
    String fillStatusText,
    String startDate,
    String endDate,
    String totalSlots,
    String soldSlots,
    String pricePerDay,
    String creativeUrl,
    String advertiserName,
    String campaignName,
    String owner,
    String remark,
    String createdAt,
    String updatedAt,
    List<Admn12AdSlotScheduleWindowDTO> scheduleWindows,
    List<String> availableActions) {}
