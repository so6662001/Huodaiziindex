package com.huodaizi.backend.dto.riskalert;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.util.Locale;

public record A08RiskAlertListRequest(
    @Pattern(regexp = "^$|^1\\d{10}$", message = "contactMobile 必须为11位手机号")
    String contactMobile,
    @Size(max = 32, message = "source 最大长度32")
    String source,
    @Size(max = 16, message = "level 最大长度16")
    String level,
    @Size(max = 32, message = "status 最大长度32")
    String status,
    @Size(max = 64, message = "owner 最大长度64")
    String owner,
    @Size(max = 64, message = "keyword 最大长度64")
    String keyword,
    @Size(max = 16, message = "highOnly 最大长度16")
    String highOnly,
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

  public boolean safeHighOnly() {
    return parseBoolean(highOnly);
  }

  private boolean parseBoolean(String value) {
    if (value == null || value.isBlank()) {
      return false;
    }
    String normalized = value.trim().toUpperCase(Locale.ROOT);
    return "TRUE".equals(normalized)
        || "1".equals(normalized)
        || "Y".equals(normalized)
        || "YES".equals(normalized);
  }
}
