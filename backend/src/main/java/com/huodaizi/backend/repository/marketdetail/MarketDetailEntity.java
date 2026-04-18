package com.huodaizi.backend.repository.marketdetail;

import com.huodaizi.backend.dto.marketdetail.MarketDetailSectionType;
import java.time.LocalDateTime;

public class MarketDetailEntity {
  private final String id;
  private String symbol;
  private String city;
  private final MarketDetailSectionType sectionType;
  private String title;
  private String subtitle;
  private String value;
  private String trend;
  private String status;
  private boolean pinned;
  private LocalDateTime updatedAt;

  public MarketDetailEntity(
      String id,
      String symbol,
      String city,
      MarketDetailSectionType sectionType,
      String title,
      String subtitle,
      String value,
      String trend,
      String status,
      boolean pinned,
      LocalDateTime updatedAt) {
    this.id = id;
    this.symbol = symbol;
    this.city = city;
    this.sectionType = sectionType;
    this.title = title;
    this.subtitle = subtitle;
    this.value = value;
    this.trend = trend;
    this.status = status;
    this.pinned = pinned;
    this.updatedAt = updatedAt;
  }

  public String getId() {
    return id;
  }

  public String getSymbol() {
    return symbol;
  }

  public String getCity() {
    return city;
  }

  public MarketDetailSectionType getSectionType() {
    return sectionType;
  }

  public String getTitle() {
    return title;
  }

  public String getSubtitle() {
    return subtitle;
  }

  public String getValue() {
    return value;
  }

  public String getTrend() {
    return trend;
  }

  public String getStatus() {
    return status;
  }

  public boolean isPinned() {
    return pinned;
  }

  public LocalDateTime getUpdatedAt() {
    return updatedAt;
  }

  public void update(
      String symbol,
      String city,
      String title,
      String subtitle,
      String value,
      String trend,
      String status,
      Boolean pinned) {
    if (symbol != null) {
      this.symbol = symbol;
    }
    if (city != null) {
      this.city = city;
    }
    if (title != null) {
      this.title = title;
    }
    if (subtitle != null) {
      this.subtitle = subtitle;
    }
    if (value != null) {
      this.value = value;
    }
    if (trend != null) {
      this.trend = trend;
    }
    if (status != null) {
      this.status = status;
    }
    if (pinned != null) {
      this.pinned = pinned;
    }
    this.updatedAt = LocalDateTime.now();
  }

  public void setStatus(String status) {
    this.status = status;
    this.updatedAt = LocalDateTime.now();
  }

  public void setPinned(boolean pinned) {
    this.pinned = pinned;
    this.updatedAt = LocalDateTime.now();
  }
}
