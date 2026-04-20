package com.huodaizi.backend.dto.admn02;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;

public record Admn02BuyerListRequest(
    @Size(max = 64, message = "keyword 最大长度64")
    String keyword,
    @Size(max = 32, message = "accountStatus 最大长度32")
    String accountStatus,
    @Size(max = 16, message = "blacklistStatus 最大长度16")
    String blacklistStatus,
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
