package com.huodaizi.backend.repository.sitead;

import com.huodaizi.backend.dto.sitead.SiteAdSectionType;
import java.time.LocalDateTime;

public class SiteAdEntity {
  private final String id;
  private final SiteAdSectionType sectionType;
  private String city;
  private String placement;
  private String duration;
  private String companyName;
  private String contactName;
  private String phone;
  private String budget;
  private String remark;
  private String link;
  private String status;
  private boolean pinned;
  private LocalDateTime updatedAt;

  public SiteAdEntity(
      String id,
      SiteAdSectionType sectionType,
      String city,
      String placement,
      String duration,
      String companyName,
      String contactName,
      String phone,
      String budget,
      String remark,
      String link,
      String status,
      boolean pinned,
      LocalDateTime updatedAt) {
    this.id = id;
    this.sectionType = sectionType;
    this.city = city;
    this.placement = placement;
    this.duration = duration;
    this.companyName = companyName;
    this.contactName = contactName;
    this.phone = phone;
    this.budget = budget;
    this.remark = remark;
    this.link = link;
    this.status = status;
    this.pinned = pinned;
    this.updatedAt = updatedAt;
  }

  public String getId() {
    return id;
  }

  public SiteAdSectionType getSectionType() {
    return sectionType;
  }

  public String getCity() {
    return city;
  }

  public String getPlacement() {
    return placement;
  }

  public String getDuration() {
    return duration;
  }

  public String getCompanyName() {
    return companyName;
  }

  public String getContactName() {
    return contactName;
  }

  public String getPhone() {
    return phone;
  }

  public String getBudget() {
    return budget;
  }

  public String getRemark() {
    return remark;
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
      String city,
      String placement,
      String duration,
      String companyName,
      String contactName,
      String phone,
      String budget,
      String remark,
      String link,
      String status,
      Boolean pinned) {
    if (city != null) {
      this.city = city;
    }
    if (placement != null) {
      this.placement = placement;
    }
    if (duration != null) {
      this.duration = duration;
    }
    if (companyName != null) {
      this.companyName = companyName;
    }
    if (contactName != null) {
      this.contactName = contactName;
    }
    if (phone != null) {
      this.phone = phone;
    }
    if (budget != null) {
      this.budget = budget;
    }
    if (remark != null) {
      this.remark = remark;
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
