package com.huodaizi.backend.repository.siteadlead;

import com.huodaizi.backend.dto.siteadlead.SiteAdLeadStatus;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class SiteAdLeadEntity {
  private final String id;
  private final String leadNo;
  private String placementId;
  private String placementName;
  private String city;
  private String duration;
  private String budget;
  private String companyName;
  private String contactName;
  private String contactPhone;
  private String remark;
  private SiteAdLeadStatus status;
  private String owner;
  private String closeReason;
  private String nextFollowAt;
  private final LocalDateTime createdAt;
  private final List<SiteAdLeadFollowEntity> followLogs = new CopyOnWriteArrayList<>();
  private LocalDateTime updatedAt;

  public SiteAdLeadEntity(
      String id,
      String leadNo,
      String placementId,
      String placementName,
      String city,
      String duration,
      String budget,
      String companyName,
      String contactName,
      String contactPhone,
      String remark,
      SiteAdLeadStatus status,
      String owner,
      String closeReason,
      String nextFollowAt,
      LocalDateTime createdAt,
      LocalDateTime updatedAt) {
    this.id = id;
    this.leadNo = leadNo;
    this.placementId = placementId;
    this.placementName = placementName;
    this.city = city;
    this.duration = duration;
    this.budget = budget;
    this.companyName = companyName;
    this.contactName = contactName;
    this.contactPhone = contactPhone;
    this.remark = remark;
    this.status = status;
    this.owner = owner;
    this.closeReason = closeReason;
    this.nextFollowAt = nextFollowAt;
    this.createdAt = createdAt;
    this.updatedAt = updatedAt;
  }

  public String getId() {
    return id;
  }

  public String getLeadNo() {
    return leadNo;
  }

  public String getPlacementId() {
    return placementId;
  }

  public String getPlacementName() {
    return placementName;
  }

  public String getCity() {
    return city;
  }

  public String getDuration() {
    return duration;
  }

  public String getBudget() {
    return budget;
  }

  public String getCompanyName() {
    return companyName;
  }

  public String getContactName() {
    return contactName;
  }

  public String getContactPhone() {
    return contactPhone;
  }

  public String getRemark() {
    return remark;
  }

  public SiteAdLeadStatus getStatus() {
    return status;
  }

  public String getOwner() {
    return owner;
  }

  public String getCloseReason() {
    return closeReason;
  }

  public String getNextFollowAt() {
    return nextFollowAt;
  }

  public LocalDateTime getCreatedAt() {
    return createdAt;
  }

  public List<SiteAdLeadFollowEntity> getFollowLogs() {
    return List.copyOf(followLogs);
  }

  public LocalDateTime getUpdatedAt() {
    return updatedAt;
  }

  public synchronized void setStatus(SiteAdLeadStatus status) {
    this.status = status;
    this.updatedAt = LocalDateTime.now();
  }

  public synchronized void setOwner(String owner) {
    this.owner = owner;
    this.updatedAt = LocalDateTime.now();
  }

  public synchronized void setCloseReason(String closeReason) {
    this.closeReason = closeReason;
    this.updatedAt = LocalDateTime.now();
  }

  public synchronized void setNextFollowAt(String nextFollowAt) {
    this.nextFollowAt = nextFollowAt;
    this.updatedAt = LocalDateTime.now();
  }

  public synchronized void addFollow(SiteAdLeadFollowEntity followEntity) {
    this.followLogs.add(followEntity);
    if (followEntity.getNextAction() != null && !followEntity.getNextAction().isBlank()) {
      this.nextFollowAt = followEntity.getNextAction();
    }
    this.updatedAt = LocalDateTime.now();
  }

  public synchronized void setCity(String city) {
    this.city = city;
    this.updatedAt = LocalDateTime.now();
  }

  public synchronized void setPlacementName(String placementName) {
    this.placementName = placementName;
    this.updatedAt = LocalDateTime.now();
  }

  public synchronized void setDuration(String duration) {
    this.duration = duration;
    this.updatedAt = LocalDateTime.now();
  }

  public synchronized void setBudget(String budget) {
    this.budget = budget;
    this.updatedAt = LocalDateTime.now();
  }

  public synchronized void setCompanyName(String companyName) {
    this.companyName = companyName;
    this.updatedAt = LocalDateTime.now();
  }

  public synchronized void setContactName(String contactName) {
    this.contactName = contactName;
    this.updatedAt = LocalDateTime.now();
  }

  public synchronized void setContactPhone(String contactPhone) {
    this.contactPhone = contactPhone;
    this.updatedAt = LocalDateTime.now();
  }

  public synchronized void setRemark(String remark) {
    this.remark = remark;
    this.updatedAt = LocalDateTime.now();
  }
}
