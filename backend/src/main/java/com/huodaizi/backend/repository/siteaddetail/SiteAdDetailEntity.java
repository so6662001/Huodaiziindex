package com.huodaizi.backend.repository.siteaddetail;

import com.huodaizi.backend.dto.siteaddetail.SiteAdDetailSectionType;
import java.time.LocalDateTime;

public class SiteAdDetailEntity {
  private final String id;
  private final String placementId;
  private final SiteAdDetailSectionType sectionType;
  private String title;
  private String desc;
  private String value;
  private String extra;
  private String link;
  private String status;
  private boolean pinned;
  private LocalDateTime updatedAt;

  public SiteAdDetailEntity(
      String id,
      String placementId,
      SiteAdDetailSectionType sectionType,
      String title,
      String desc,
      String value,
      String extra,
      String link,
      String status,
      boolean pinned,
      LocalDateTime updatedAt) {
    this.id = id;
    this.placementId = placementId;
    this.sectionType = sectionType;
    this.title = title;
    this.desc = desc;
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

  public String getPlacementId() {
    return placementId;
  }

  public SiteAdDetailSectionType getSectionType() {
    return sectionType;
  }

  public String getTitle() {
    return title;
  }

  public String getDesc() {
    return desc;
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
      String desc,
      String value,
      String extra,
      String link,
      String status,
      Boolean pinned) {
    if (title != null) {
      this.title = title;
    }
    if (desc != null) {
      this.desc = desc;
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
