package com.huodaizi.backend.repository.news;

import com.huodaizi.backend.dto.news.NewsSectionType;
import java.time.LocalDateTime;

public class NewsEntity {
  private final String id;
  private final NewsSectionType sectionType;
  private String category;
  private String city;
  private String title;
  private String summary;
  private String publishAt;
  private String link;
  private String status;
  private boolean pinned;
  private LocalDateTime updatedAt;

  public NewsEntity(
      String id,
      NewsSectionType sectionType,
      String category,
      String city,
      String title,
      String summary,
      String publishAt,
      String link,
      String status,
      boolean pinned,
      LocalDateTime updatedAt) {
    this.id = id;
    this.sectionType = sectionType;
    this.category = category;
    this.city = city;
    this.title = title;
    this.summary = summary;
    this.publishAt = publishAt;
    this.link = link;
    this.status = status;
    this.pinned = pinned;
    this.updatedAt = updatedAt;
  }

  public String getId() {
    return id;
  }

  public NewsSectionType getSectionType() {
    return sectionType;
  }

  public String getCategory() {
    return category;
  }

  public String getCity() {
    return city;
  }

  public String getTitle() {
    return title;
  }

  public String getSummary() {
    return summary;
  }

  public String getPublishAt() {
    return publishAt;
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
      String category,
      String city,
      String title,
      String summary,
      String publishAt,
      String link,
      String status,
      Boolean pinned) {
    if (category != null) {
      this.category = category;
    }
    if (city != null) {
      this.city = city;
    }
    if (title != null) {
      this.title = title;
    }
    if (summary != null) {
      this.summary = summary;
    }
    if (publishAt != null) {
      this.publishAt = publishAt;
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
