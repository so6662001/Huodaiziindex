package com.huodaizi.backend.repository.warehousedetail;

import com.huodaizi.backend.dto.warehousedetail.WarehouseDetailSectionType;
import java.time.LocalDateTime;

public class WarehouseDetailEntity {
  private final String id;
  private final String warehouseId;
  private final WarehouseDetailSectionType sectionType;
  private String title;
  private String city;
  private String warehouseType;
  private String capacity;
  private String throughput;
  private String capability;
  private String quote;
  private String address;
  private String workTime;
  private String serviceTags;
  private String description;
  private String relatedId;
  private String contactName;
  private String contactPhone;
  private String serviceStatus;
  private String link;
  private String status;
  private boolean pinned;
  private LocalDateTime updatedAt;

  public WarehouseDetailEntity(
      String id,
      String warehouseId,
      WarehouseDetailSectionType sectionType,
      String title,
      String city,
      String warehouseType,
      String capacity,
      String throughput,
      String capability,
      String quote,
      String address,
      String workTime,
      String serviceTags,
      String description,
      String relatedId,
      String contactName,
      String contactPhone,
      String serviceStatus,
      String link,
      String status,
      boolean pinned,
      LocalDateTime updatedAt) {
    this.id = id;
    this.warehouseId = warehouseId;
    this.sectionType = sectionType;
    this.title = title;
    this.city = city;
    this.warehouseType = warehouseType;
    this.capacity = capacity;
    this.throughput = throughput;
    this.capability = capability;
    this.quote = quote;
    this.address = address;
    this.workTime = workTime;
    this.serviceTags = serviceTags;
    this.description = description;
    this.relatedId = relatedId;
    this.contactName = contactName;
    this.contactPhone = contactPhone;
    this.serviceStatus = serviceStatus;
    this.link = link;
    this.status = status;
    this.pinned = pinned;
    this.updatedAt = updatedAt;
  }

  public String getId() {
    return id;
  }

  public String getWarehouseId() {
    return warehouseId;
  }

  public WarehouseDetailSectionType getSectionType() {
    return sectionType;
  }

  public String getTitle() {
    return title;
  }

  public String getCity() {
    return city;
  }

  public String getWarehouseType() {
    return warehouseType;
  }

  public String getCapacity() {
    return capacity;
  }

  public String getThroughput() {
    return throughput;
  }

  public String getCapability() {
    return capability;
  }

  public String getQuote() {
    return quote;
  }

  public String getAddress() {
    return address;
  }

  public String getWorkTime() {
    return workTime;
  }

  public String getServiceTags() {
    return serviceTags;
  }

  public String getDescription() {
    return description;
  }

  public String getRelatedId() {
    return relatedId;
  }

  public String getContactName() {
    return contactName;
  }

  public String getContactPhone() {
    return contactPhone;
  }

  public String getServiceStatus() {
    return serviceStatus;
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
      String city,
      String warehouseType,
      String capacity,
      String throughput,
      String capability,
      String quote,
      String address,
      String workTime,
      String serviceTags,
      String description,
      String relatedId,
      String contactName,
      String contactPhone,
      String serviceStatus,
      String link,
      String status,
      Boolean pinned) {
    if (title != null) {
      this.title = title;
    }
    if (city != null) {
      this.city = city;
    }
    if (warehouseType != null) {
      this.warehouseType = warehouseType;
    }
    if (capacity != null) {
      this.capacity = capacity;
    }
    if (throughput != null) {
      this.throughput = throughput;
    }
    if (capability != null) {
      this.capability = capability;
    }
    if (quote != null) {
      this.quote = quote;
    }
    if (address != null) {
      this.address = address;
    }
    if (workTime != null) {
      this.workTime = workTime;
    }
    if (serviceTags != null) {
      this.serviceTags = serviceTags;
    }
    if (description != null) {
      this.description = description;
    }
    if (relatedId != null) {
      this.relatedId = relatedId;
    }
    if (contactName != null) {
      this.contactName = contactName;
    }
    if (contactPhone != null) {
      this.contactPhone = contactPhone;
    }
    if (serviceStatus != null) {
      this.serviceStatus = serviceStatus;
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
