package com.huodaizi.backend.repository.sitecity;

import com.huodaizi.backend.dto.sitecity.SiteCitySectionType;
import java.time.LocalDateTime;

public class SiteCityEntity {
  private final String id;
  private final String city;
  private final SiteCitySectionType sectionType;
  private String title;
  private String subtitle;
  private String value;
  private String extra;
  private String link;
  private String status;
  private boolean pinned;
  private LocalDateTime updatedAt;

  public SiteCityEntity(
      String id,
      String city,
      SiteCitySectionType sectionType,
      String title,
      String subtitle,
      String value,
      String extra,
      String link,
      String status,
      boolean pinned,
      LocalDateTime updatedAt) {
    this.id = id;
    this.city = city;
    this.sectionType = sectionType;
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

  public String getCity() {
    return city;
  }

  public SiteCitySectionType getSectionType() {
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
      String title,
      String subtitle,
      String value,
      String extra,
      String link,
      String status,
      Boolean pinned) {
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
