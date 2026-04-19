package com.huodaizi.backend.repository.freightdemand;

import java.time.LocalDateTime;

public class FreightDemandEntity {
  private final String id;
  private String title;
  private String originCity;
  private String destinationCity;
  private String goodsCategory;
  private String tonnage;
  private String vehicleType;
  private String timeliness;
  private String loadDate;
  private boolean needInvoice;
  private boolean needLoading;
  private String contactName;
  private String contactPhone;
  private String companyName;
  private String remark;
  private String status;
  private boolean pinned;
  private LocalDateTime updatedAt;

  public FreightDemandEntity(
      String id,
      String title,
      String originCity,
      String destinationCity,
      String goodsCategory,
      String tonnage,
      String vehicleType,
      String timeliness,
      String loadDate,
      boolean needInvoice,
      boolean needLoading,
      String contactName,
      String contactPhone,
      String companyName,
      String remark,
      String status,
      boolean pinned,
      LocalDateTime updatedAt) {
    this.id = id;
    this.title = title;
    this.originCity = originCity;
    this.destinationCity = destinationCity;
    this.goodsCategory = goodsCategory;
    this.tonnage = tonnage;
    this.vehicleType = vehicleType;
    this.timeliness = timeliness;
    this.loadDate = loadDate;
    this.needInvoice = needInvoice;
    this.needLoading = needLoading;
    this.contactName = contactName;
    this.contactPhone = contactPhone;
    this.companyName = companyName;
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

  public String getOriginCity() {
    return originCity;
  }

  public String getDestinationCity() {
    return destinationCity;
  }

  public String getGoodsCategory() {
    return goodsCategory;
  }

  public String getTonnage() {
    return tonnage;
  }

  public String getVehicleType() {
    return vehicleType;
  }

  public String getTimeliness() {
    return timeliness;
  }

  public String getLoadDate() {
    return loadDate;
  }

  public boolean isNeedInvoice() {
    return needInvoice;
  }

  public boolean isNeedLoading() {
    return needLoading;
  }

  public String getContactName() {
    return contactName;
  }

  public String getContactPhone() {
    return contactPhone;
  }

  public String getCompanyName() {
    return companyName;
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
      String originCity,
      String destinationCity,
      String goodsCategory,
      String tonnage,
      String vehicleType,
      String timeliness,
      String loadDate,
      Boolean needInvoice,
      Boolean needLoading,
      String contactName,
      String contactPhone,
      String companyName,
      String remark,
      String status,
      Boolean pinned) {
    if (title != null) {
      this.title = title;
    }
    if (originCity != null) {
      this.originCity = originCity;
    }
    if (destinationCity != null) {
      this.destinationCity = destinationCity;
    }
    if (goodsCategory != null) {
      this.goodsCategory = goodsCategory;
    }
    if (tonnage != null) {
      this.tonnage = tonnage;
    }
    if (vehicleType != null) {
      this.vehicleType = vehicleType;
    }
    if (timeliness != null) {
      this.timeliness = timeliness;
    }
    if (loadDate != null) {
      this.loadDate = loadDate;
    }
    if (needInvoice != null) {
      this.needInvoice = needInvoice;
    }
    if (needLoading != null) {
      this.needLoading = needLoading;
    }
    if (contactName != null) {
      this.contactName = contactName;
    }
    if (contactPhone != null) {
      this.contactPhone = contactPhone;
    }
    if (companyName != null) {
      this.companyName = companyName;
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
