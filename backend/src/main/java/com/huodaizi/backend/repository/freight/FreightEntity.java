package com.huodaizi.backend.repository.freight;

import java.time.LocalDateTime;

public class FreightEntity {
  private final String id;
  private String provider;
  private String origin;
  private String destination;
  private String vehicleType;
  private String loadRange;
  private String frequency;
  private Integer timelinessHours;
  private boolean returnTruck;
  private String price;
  private String contactName;
  private String contactPhone;
  private String status;
  private boolean pinned;
  private LocalDateTime updatedAt;

  public FreightEntity(
      String id,
      String provider,
      String origin,
      String destination,
      String vehicleType,
      String loadRange,
      String frequency,
      Integer timelinessHours,
      boolean returnTruck,
      String price,
      String contactName,
      String contactPhone,
      String status,
      boolean pinned,
      LocalDateTime updatedAt) {
    this.id = id;
    this.provider = provider;
    this.origin = origin;
    this.destination = destination;
    this.vehicleType = vehicleType;
    this.loadRange = loadRange;
    this.frequency = frequency;
    this.timelinessHours = timelinessHours;
    this.returnTruck = returnTruck;
    this.price = price;
    this.contactName = contactName;
    this.contactPhone = contactPhone;
    this.status = status;
    this.pinned = pinned;
    this.updatedAt = updatedAt;
  }

  public String getId() {
    return id;
  }

  public String getProvider() {
    return provider;
  }

  public String getOrigin() {
    return origin;
  }

  public String getDestination() {
    return destination;
  }

  public String getRoute() {
    return origin + " → " + destination;
  }

  public String getVehicleType() {
    return vehicleType;
  }

  public String getLoadRange() {
    return loadRange;
  }

  public String getFrequency() {
    return frequency;
  }

  public Integer getTimelinessHours() {
    return timelinessHours;
  }

  public boolean isReturnTruck() {
    return returnTruck;
  }

  public String getPrice() {
    return price;
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
      String provider,
      String origin,
      String destination,
      String vehicleType,
      String loadRange,
      String frequency,
      Integer timelinessHours,
      Boolean returnTruck,
      String price,
      String contactName,
      String contactPhone,
      String status,
      Boolean pinned) {
    if (provider != null) {
      this.provider = provider;
    }
    if (origin != null) {
      this.origin = origin;
    }
    if (destination != null) {
      this.destination = destination;
    }
    if (vehicleType != null) {
      this.vehicleType = vehicleType;
    }
    if (loadRange != null) {
      this.loadRange = loadRange;
    }
    if (frequency != null) {
      this.frequency = frequency;
    }
    if (timelinessHours != null) {
      this.timelinessHours = timelinessHours;
    }
    if (returnTruck != null) {
      this.returnTruck = returnTruck;
    }
    if (price != null) {
      this.price = price;
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
