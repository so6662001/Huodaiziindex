package com.huodaizi.backend.dto.admn16;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;

public record Admn16DataApiSubscriptionListRequest(
    @Size(max = 24, message = "subscriptionStatus 最大长度24")
    String subscriptionStatus,
    @Size(max = 32, message = "apiProductCode 最大长度32")
    String apiProductCode,
    @Size(max = 24, message = "billingCycle 最大长度24")
    String billingCycle,
    @Size(max = 64, message = "owner 最大长度64")
    String owner,
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
