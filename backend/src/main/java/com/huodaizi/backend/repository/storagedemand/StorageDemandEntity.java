package com.huodaizi.backend.repository.storagedemand;

import java.time.LocalDateTime;

public class StorageDemandEntity {
  private final String id;
  private String title;
  private String city;
  private String goodsCategory;
  private String tonnage;
  private String storageDays;
  private String inboundDate;
  private boolean needLoading;
  private boolean needSorting;
  private String companyName;
  private String contactName;
  private String contactPhone;
  private String remark;
  private String status;
  private boolean pinned;
  private LocalDateTime updatedAt;

  public StorageDemandEntity(
      String id,
      String title,
      String city,
      String goodsCategory,
      String tonnage,
      String storageDays,
      String inboundDate,
      boolean needLoading,
      boolean needSorting,
      String companyName,
      String contactName,
      String contactPhone,
      String remark,
      String status,
      boolean pinned,
      LocalDateTime updatedAt) {
    this.id = id;
    this.title = title;
    this.city = city;
    this.goodsCategory = goodsCategory;
    this.tonnage = tonnage;
    this.storageDays = storageDays;
    this.inboundDate = inboundDate;
    this.needLoading = needLoading;
    this.needSorting = needSorting;
    this.companyName = companyName;
    this.contactName = contactName;
    this.contactPhone = contactPhone;
    this.remark = remark;
    this.status = status;
    this.pinned = pinned;
    this.updatedAt = updatedAt;
  }

  public String getId() {
    return id;
  }

  public String getTitle() {
    return title;
  }

  public String getCity() {
    return city;
  }

  public String getGoodsCategory() {
    return goodsCategory;
  }

  public String getTonnage() {
    return tonnage;
  }

  public String getStorageDays() {
    return storageDays;
  }

  public String getInboundDate() {
    return inboundDate;
  }

  public boolean isNeedLoading() {
    return needLoading;
  }

  public boolean isNeedSorting() {
    return needSorting;
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
      String city,
      String goodsCategory,
      String tonnage,
      String storageDays,
      String inboundDate,
      Boolean needLoading,
      Boolean needSorting,
      String companyName,
      String contactName,
      String contactPhone,
      String remark,
      String status,
      Boolean pinned) {
    if (title != null) {
      this.title = title;
    }
    if (city != null) {
      this.city = city;
    }
    if (goodsCategory != null) {
      this.goodsCategory = goodsCategory;
    }
    if (tonnage != null) {
      this.tonnage = tonnage;
    }
    if (storageDays != null) {
      this.storageDays = storageDays;
    }
    if (inboundDate != null) {
      this.inboundDate = inboundDate;
    }
    if (needLoading != null) {
      this.needLoading = needLoading;
    }
    if (needSorting != null) {
      this.needSorting = needSorting;
    }
    if (companyName != null) {
      this.companyName = companyName;
    }
    if (contactName != null) {
      this.contactName = contactName;
    }
    if (contactPhone != null) {
      this.contactPhone = contactPhone;
    }
    if (remark != null) {
      this.remark = remark;
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
