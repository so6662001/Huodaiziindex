package com.huodaizi.backend.dto.dispatchstrategy;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;

public record A06DispatchStrategyListRequest(
    @Size(max = 64, message = "keyword 最大长度64")
    String keyword,
    @Size(max = 32, message = "sceneCode 最大长度32")
    String sceneCode,
    @Size(max = 32, message = "enabled 最大长度32")
    String enabled,
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

  public boolean safeEnabledOnly() {
    if (enabled == null || enabled.isBlank()) {
      return false;
    }
    String normalized = enabled.trim().toUpperCase();
    return "TRUE".equals(normalized)
        || "1".equals(normalized)
        || "Y".equals(normalized)
        || "YES".equals(normalized);
  }
}
