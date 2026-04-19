package com.huodaizi.backend.repository.logistics;

import java.time.LocalDateTime;

public class LogisticsSectionEntity {
  private final String id;
  private final LogisticsSectionType type;
  private String title;
  private String subtitle;
  private String city;
  private String value;
  private String extra;
  private String link;
  private String content;
  private String status;
  private boolean pinned;
  private LocalDateTime updatedAt;

  public LogisticsSectionEntity(
      String id,
      LogisticsSectionType type,
      String title,
      String subtitle,
      String city,
      String value,
      String extra,
      String link,
      String content,
      String status,
      boolean pinned,
      LocalDateTime updatedAt) {
    this.id = id;
    this.type = type;
    this.title = title;
    this.subtitle = subtitle;
    this.city = city;
    this.value = value;
    this.extra = extra;
    this.link = link;
    this.content = content;
    this.status = status;
    this.pinned = pinned;
    this.updatedAt = updatedAt;
  }

  public String getId() {
    return id;
  }

  public LogisticsSectionType getType() {
    return type;
  }

  public String getTitle() {
    return title;
  }

  public String getSubtitle() {
    return subtitle;
  }

  public String getCity() {
    return city;
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

  public String getContent() {
    return content;
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
      String city,
      String value,
      String extra,
      String link,
      String content,
      String status,
      Boolean pinned) {
    if (title != null) {
      this.title = title;
    }
    if (subtitle != null) {
      this.subtitle = subtitle;
    }
    if (city != null) {
      this.city = city;
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
    if (content != null) {
      this.content = content;
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
