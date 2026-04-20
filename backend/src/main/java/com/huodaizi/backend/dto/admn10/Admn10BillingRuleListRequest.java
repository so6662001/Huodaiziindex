package com.huodaizi.backend.dto.admn10;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;

public record Admn10BillingRuleListRequest(
    @Size(max = 24, message = "ruleStatus 最大长度24")
    String ruleStatus,
    @Size(max = 24, message = "sceneCode 最大长度24")
    String sceneCode,
    @Size(max = 16, message = "billingMode 最大长度16")
    String billingMode,
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
