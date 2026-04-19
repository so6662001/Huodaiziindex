package com.huodaizi.backend.dto.logistics;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;

public record LogisticsSearchRequest(
    @Size(max = 32, message = "city 最大长度32") String city,
    @Size(max = 32, message = "type 最大长度32") String type,
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
