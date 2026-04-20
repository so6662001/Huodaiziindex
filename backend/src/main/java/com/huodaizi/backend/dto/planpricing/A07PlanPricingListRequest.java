package com.huodaizi.backend.dto.planpricing;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import java.util.Locale;

public record A07PlanPricingListRequest(
    @Size(max = 64, message = "keyword 最大长度64")
    String keyword,
    @Size(max = 32, message = "planType 最大长度32")
    String planType,
    @Size(max = 16, message = "enabled 最大长度16")
    String enabled,
    @Size(max = 16, message = "recommended 最大长度16")
    String recommended,
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

  public boolean hasEnabledFilter() {
    return parseBoolean(enabled) != null;
  }

  public boolean safeEnabledOnly() {
    Boolean parsed = parseBoolean(enabled);
    return Boolean.TRUE.equals(parsed);
  }

  public boolean hasRecommendedFilter() {
    return parseBoolean(recommended) != null;
  }

  public boolean safeRecommendedOnly() {
    Boolean parsed = parseBoolean(recommended);
    return Boolean.TRUE.equals(parsed);
  }

  private Boolean parseBoolean(String text) {
    if (text == null || text.isBlank()) {
      return null;
    }
    String normalized = text.trim().toUpperCase(Locale.ROOT);
    return switch (normalized) {
      case "TRUE", "1", "Y", "YES" -> true;
      case "FALSE", "0", "N", "NO" -> false;
      default -> null;
    };
  }
}
