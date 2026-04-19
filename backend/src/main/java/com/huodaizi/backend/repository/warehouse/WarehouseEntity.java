package com.huodaizi.backend.repository.warehouse;

import java.time.LocalDateTime;

public class WarehouseEntity {
  private final String id;
  private String name;
  private String city;
  private String type;
  private String capacity;
  private String throughput;
  private String capability;
  private String categories;
  private String price;
  private String address;
  private String contactName;
  private String contactPhone;
  private String status;
  private boolean pinned;
  private LocalDateTime updatedAt;

  public WarehouseEntity(
      String id,
      String name,
      String city,
      String type,
      String capacity,
      String throughput,
      String capability,
      String categories,
      String price,
      String address,
      String contactName,
      String contactPhone,
      String status,
      boolean pinned,
      LocalDateTime updatedAt) {
    this.id = id;
    this.name = name;
    this.city = city;
    this.type = type;
    this.capacity = capacity;
    this.throughput = throughput;
    this.capability = capability;
    this.categories = categories;
    this.price = price;
    this.address = address;
    this.contactName = contactName;
    this.contactPhone = contactPhone;
    this.status = status;
    this.pinned = pinned;
    this.updatedAt = updatedAt;
  }

  public String getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public String getCity() {
    return city;
  }

  public String getType() {
    return type;
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

  public String getCategories() {
    return categories;
  }

  public String getPrice() {
    return price;
  }

  public String getAddress() {
    return address;
  }

  public String getContactName() {
    return contactName;
  }

  public String getContactPhone() {
    return contactPhone;
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
      String name,
      String city,
      String type,
      String capacity,
      String throughput,
      String capability,
      String categories,
      String price,
      String address,
      String contactName,
      String contactPhone,
      String status,
      Boolean pinned) {
    if (name != null) {
      this.name = name;
    }
    if (city != null) {
      this.city = city;
    }
    if (type != null) {
      this.type = type;
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
    if (categories != null) {
      this.categories = categories;
    }
    if (price != null) {
      this.price = price;
    }
    if (address != null) {
      this.address = address;
    }
    if (contactName != null) {
      this.contactName = contactName;
    }
    if (contactPhone != null) {
      this.contactPhone = contactPhone;
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
