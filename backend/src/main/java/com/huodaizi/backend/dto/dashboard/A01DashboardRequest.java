package com.huodaizi.backend.dto.dashboard;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;

public record A01DashboardRequest(
    @Min(value = 1, message = "days 最小为1")
    @Max(value = 365, message = "days 最大为365")
    Integer days,
    @Size(max = 32, message = "city 最大长度32")
    String city) {

  public int safeDays() {
    return days == null ? 30 : days;
  }

  public String safeCity() {
    if (city == null || city.isBlank()) {
      return "全国";
    }
    return city.trim();
  }
}
