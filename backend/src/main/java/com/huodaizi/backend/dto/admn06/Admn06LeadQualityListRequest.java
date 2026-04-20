package com.huodaizi.backend.dto.admn06;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;

public record Admn06LeadQualityListRequest(
    @Size(max = 16, message = "source 最大长度16")
    String source,
    @Size(max = 16, message = "qualityStatus 最大长度16")
    String qualityStatus,
    @Size(max = 16, message = "riskLevel 最大长度16")
    String riskLevel,
    @Size(max = 64, message = "reviewer 最大长度64")
    String reviewer,
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
