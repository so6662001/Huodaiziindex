package com.huodaizi.backend.repository.sitecenter;

import com.huodaizi.backend.dto.sitecenter.SiteCenterSectionType;
import java.time.LocalDateTime;

public class SiteCenterEntity {
  private final String id;
  private final SiteCenterSectionType sectionType;
  private String region;
  private String citySlug;
  private String title;
  private String subtitle;
  private String value;
  private String extra;
  private String link;
  private String status;
  private boolean pinned;
  private LocalDateTime updatedAt;

  public SiteCenterEntity(
      String id,
      SiteCenterSectionType sectionType,
      String region,
      String citySlug,
      String title,
      String subtitle,
      String value,
      String extra,
      String link,
      String status,
      boolean pinned,
      LocalDateTime updatedAt) {
    this.id = id;
    this.sectionType = sectionType;
    this.region = region;
    this.citySlug = citySlug;
    this.title = title;
    this.subtitle = subtitle;
    this.value = value;
    this.extra = extra;
    this.link = link;
    this.status = status;
    this.pinned = pinned;
    this.updatedAt = updatedAt;
  }

  public String getId() {
    return id;
  }

  public SiteCenterSectionType getSectionType() {
    return sectionType;
  }

  public String getRegion() {
    return region;
  }

  public String getCitySlug() {
    return citySlug;
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

  public String getExtra() {
    return extra;
  }

  public String getLink() {
    return link;
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
      String region,
      String citySlug,
      String title,
      String subtitle,
      String value,
      String extra,
      String link,
      String status,
      Boolean pinned) {
    if (region != null) {
      this.region = region;
    }
    if (citySlug != null) {
      this.citySlug = citySlug;
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
    if (extra != null) {
      this.extra = extra;
    }
    if (link != null) {
      this.link = link;
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
