package com.huodaizi.backend.repository.newsdetail;

import com.huodaizi.backend.dto.newsdetail.NewsDetailSectionType;
import java.time.LocalDateTime;

public class NewsDetailEntity {
  private final String id;
  private final String newsId;
  private final NewsDetailSectionType sectionType;
  private String title;
  private String category;
  private String city;
  private String publishAt;
  private String source;
  private String summary;
  private String content;
  private String tags;
  private String relatedNewsId;
  private String link;
  private String status;
  private boolean pinned;
  private LocalDateTime updatedAt;

  public NewsDetailEntity(
      String id,
      String newsId,
      NewsDetailSectionType sectionType,
      String title,
      String category,
      String city,
      String publishAt,
      String source,
      String summary,
      String content,
      String tags,
      String relatedNewsId,
      String link,
      String status,
      boolean pinned,
      LocalDateTime updatedAt) {
    this.id = id;
    this.newsId = newsId;
    this.sectionType = sectionType;
    this.title = title;
    this.category = category;
    this.city = city;
    this.publishAt = publishAt;
    this.source = source;
    this.summary = summary;
    this.content = content;
    this.tags = tags;
    this.relatedNewsId = relatedNewsId;
    this.link = link;
    this.status = status;
    this.pinned = pinned;
    this.updatedAt = updatedAt;
  }

  public String getId() {
    return id;
  }

  public String getNewsId() {
    return newsId;
  }

  public NewsDetailSectionType getSectionType() {
    return sectionType;
  }

  public String getTitle() {
    return title;
  }

  public String getCategory() {
    return category;
  }

  public String getCity() {
    return city;
  }

  public String getPublishAt() {
    return publishAt;
  }

  public String getSource() {
    return source;
  }

  public String getSummary() {
    return summary;
  }

  public String getContent() {
    return content;
  }

  public String getTags() {
    return tags;
  }

  public String getRelatedNewsId() {
    return relatedNewsId;
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
      String category,
      String city,
      String publishAt,
      String source,
      String summary,
      String content,
      String tags,
      String relatedNewsId,
      String link,
      String status,
      Boolean pinned) {
    if (title != null) {
      this.title = title;
    }
    if (category != null) {
      this.category = category;
    }
    if (city != null) {
      this.city = city;
    }
    if (publishAt != null) {
      this.publishAt = publishAt;
    }
    if (source != null) {
      this.source = source;
    }
    if (summary != null) {
      this.summary = summary;
    }
    if (content != null) {
      this.content = content;
    }
    if (tags != null) {
      this.tags = tags;
    }
    if (relatedNewsId != null) {
      this.relatedNewsId = relatedNewsId;
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
