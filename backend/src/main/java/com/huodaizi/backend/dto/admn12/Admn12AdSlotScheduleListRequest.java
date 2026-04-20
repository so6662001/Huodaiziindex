package com.huodaizi.backend.dto.admn12;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;

public record Admn12AdSlotScheduleListRequest(
    @Size(max = 24, message = "scheduleStatus 最大长度24")
    String scheduleStatus,
    @Size(max = 24, message = "slotType 最大长度24")
    String slotType,
    @Size(max = 24, message = "cityCode 最大长度24")
    String cityCode,
    @Size(max = 80, message = "keyword 最大长度80")
    String keyword,
    @Min(value = 1, message = "page 最小为1")
    Integer page,
    @Min(value = 1, message = "pageSize 最小为1")
    @Max(value = 100, message = "pageSize 最大为100")
    Integer pageSize) {

  public int safePage() {
    return page == null ? 1 : page;
  }

  public int safePageSize() {
    return pageSize == null ? 10 : pageSize;
  }
}
