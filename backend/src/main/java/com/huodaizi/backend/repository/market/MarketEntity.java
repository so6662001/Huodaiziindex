package com.huodaizi.backend.repository.market;

import com.huodaizi.backend.dto.market.MarketSectionType;
import java.time.LocalDateTime;

public class MarketEntity {
  private final String id;
  private final MarketSectionType sectionType;
  private String category;
  private String city;
  private String rangeTag;
  private String title;
  private String subtitle;
  private String value;
  private String highPrice;
  private String lowPrice;
  private String extra;
  private String tag;
  private String link;
  private String status;
  private boolean pinned;
  private LocalDateTime updatedAt;

  public MarketEntity(
      String id,
      MarketSectionType sectionType,
      String category,
      String city,
      String rangeTag,
      String title,
      String subtitle,
      String value,
      String highPrice,
      String lowPrice,
      String extra,
      String tag,
      String link,
      String status,
      boolean pinned,
      LocalDateTime updatedAt) {
    this.id = id;
    this.sectionType = sectionType;
    this.category = category;
    this.city = city;
    this.rangeTag = rangeTag;
    this.title = title;
    this.subtitle = subtitle;
    this.value = value;
    this.highPrice = highPrice;
    this.lowPrice = lowPrice;
    this.extra = extra;
    this.tag = tag;
    this.link = link;
    this.status = status;
    this.pinned = pinned;
    this.updatedAt = updatedAt;
  }

  public String getId() {
    return id;
  }

  public MarketSectionType getSectionType() {
    return sectionType;
  }

  public MarketSectionType getSection() {
    return sectionType;
  }

  public String getCategory() {
    return category;
  }

  public String getCity() {
    return city;
  }

  public String getRangeTag() {
    return rangeTag;
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

  public String getPrice() {
    return value;
  }

  public String getHighPrice() {
    return highPrice;
  }

  public String getLowPrice() {
    return lowPrice;
  }

  public String getExtra() {
    return extra;
  }

  public String getChangeValue() {
    return extra;
  }

  public String getTag() {
    return tag;
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
      String rangeTag,
      String title,
      String subtitle,
      String value,
      String highPrice,
      String lowPrice,
      String extra,
      String tag,
      String link,
      String status,
      Boolean pinned) {
    if (category != null) {
      this.category = category;
    }
    if (city != null) {
      this.city = city;
    }
    if (rangeTag != null) {
      this.rangeTag = rangeTag;
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
    if (highPrice != null) {
      this.highPrice = highPrice;
    }
    if (lowPrice != null) {
      this.lowPrice = lowPrice;
    }
    if (extra != null) {
      this.extra = extra;
    }
    if (tag != null) {
      this.tag = tag;
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
