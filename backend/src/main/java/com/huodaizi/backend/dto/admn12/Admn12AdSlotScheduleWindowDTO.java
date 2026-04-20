package com.huodaizi.backend.dto.admn12;

public record Admn12AdSlotScheduleWindowDTO(
    int index,
    String startDate,
    String endDate,
    String windowStatus,
    String windowStatusText,
    String bookedBy) {}
