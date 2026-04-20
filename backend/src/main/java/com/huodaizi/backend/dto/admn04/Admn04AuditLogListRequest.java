package com.huodaizi.backend.dto.admn04;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;

public record Admn04AuditLogListRequest(
    @Size(max = 32, message = "moduleCode 最大长度32")
    String moduleCode,
    @Size(max = 32, message = "actionCode 最大长度32")
    String actionCode,
    @Size(max = 16, message = "resultStatus 最大长度16")
    String resultStatus,
    @Size(max = 64, message = "operator 最大长度64")
    String operator,
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
