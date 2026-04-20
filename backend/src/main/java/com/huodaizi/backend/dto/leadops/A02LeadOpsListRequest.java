package com.huodaizi.backend.dto.leadops;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;

public record A02LeadOpsListRequest(
    @Size(max = 32, message = "source 最大长度32")
    String source,
    @Size(max = 32, message = "status 最大长度32")
    String status,
    @Size(max = 64, message = "keyword 最大长度64")
    String keyword,
    @Size(max = 32, message = "owner 最大长度32")
    String owner,
    @Size(max = 32, message = "city 最大长度32")
    String city,
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

  public String safeSource() {
    if (source == null || source.isBlank()) {
      return "ALL";
    }
    return source.trim().toUpperCase();
  }
}
