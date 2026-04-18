package com.huodaizi.backend.dto.freight;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;

public record FreightFilterRequest(
    @Size(max = 32, message = "origin 最大长度32") String origin,
    @Size(max = 32, message = "destination 最大长度32") String destination,
    @Size(max = 32, message = "vehicleType 最大长度32") String vehicleType,
    @Size(max = 32, message = "timeliness 最大长度32") String timeliness,
    @Size(max = 32, message = "returnTruck 最大长度32") String returnTruck,
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
