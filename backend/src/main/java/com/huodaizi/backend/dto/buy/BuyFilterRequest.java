package com.huodaizi.backend.dto.buy;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;

public record BuyFilterRequest(
    @Size(max = 32, message = "category 最大长度32") String category,
    @Size(max = 64, message = "spec 最大长度64") String spec,
    @Size(max = 32, message = "city 最大长度32") String city,
    @Size(max = 32, message = "arrival 最大长度32") String arrival,
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
