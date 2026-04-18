package com.huodaizi.backend.dto.transportdemand;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;

public record TransportDemandFilterRequest(
    @Size(max = 32, message = "originCity 最大长度32") String originCity,
    @Size(max = 32, message = "destinationCity 最大长度32") String destinationCity,
    @Size(max = 32, message = "goodsCategory 最大长度32") String goodsCategory,
    @Size(max = 32, message = "vehicleType 最大长度32") String vehicleType,
    @Size(max = 32, message = "timeliness 最大长度32") String timeliness,
    @Size(max = 32, message = "invoiceNeed 最大长度32") String invoiceNeed,
    @Size(max = 64, message = "keyword 最大长度64") String keyword,
    @Min(value = 1, message = "page 最小为1") Integer page,
    @Min(value = 1, message = "pageSize 最小为1")
    @Max(value = 50, message = "pageSize 最大为50")
    Integer pageSize) {

  public int safePage() {
    return page == null ? 1 : page;
  }

  public int safePageSize() {
    return pageSize == null ? 10 : pageSize;
  }
}
