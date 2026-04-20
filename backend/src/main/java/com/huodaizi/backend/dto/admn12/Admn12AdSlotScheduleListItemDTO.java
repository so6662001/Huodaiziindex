package com.huodaizi.backend.dto.admn12;

public record Admn12AdSlotScheduleListItemDTO(
    String scheduleId,
    String scheduleCode,
    String slotId,
    String slotName,
    String slotType,
    String slotTypeText,
    String cityCode,
    String cityName,
    String scheduleStatus,
    String scheduleStatusText,
    String unitPriceYuan,
    String totalInventory,
    String lockedInventory,
    String remainingInventory,
    String saleStartAt,
    String saleEndAt,
    String operator,
    String updatedAt) {}
