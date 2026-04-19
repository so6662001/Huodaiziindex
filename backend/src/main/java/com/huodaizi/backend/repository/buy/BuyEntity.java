package com.huodaizi.backend.repository.buy;

import java.time.LocalDateTime;

public class BuyEntity {
  private final String id;
  private String title;
  private String buyer;
  private String category;
  private String spec;
  private String city;
  private String budget;
  private String arrival;
  private String demand;
  private String contactName;
  private String contactPhone;
  private String status;
  private boolean pinned;
  private LocalDateTime updatedAt;

  public BuyEntity(
      String id,
      String title,
      String buyer,
      String category,
      String spec,
      String city,
      String budget,
      String arrival,
      String demand,
      String contactName,
      String contactPhone,
      String status,
      boolean pinned,
      LocalDateTime updatedAt) {
    this.id = id;
    this.title = title;
    this.buyer = buyer;
    this.category = category;
    this.spec = spec;
    this.city = city;
    this.budget = budget;
    this.arrival = arrival;
    this.demand = demand;
    this.contactName = contactName;
    this.contactPhone = contactPhone;
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

  public String getBuyer() {
    return buyer;
  }

  public String getCategory() {
    return category;
  }

  public String getSpec() {
    return spec;
  }

  public String getCity() {
    return city;
  }

  public String getBudget() {
    return budget;
  }

  public String getArrival() {
    return arrival;
  }

  public String getDemand() {
    return demand;
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
      String title,
      String buyer,
      String category,
      String spec,
      String city,
      String budget,
      String arrival,
      String demand,
      String contactName,
      String contactPhone,
      String status,
      Boolean pinned) {
    if (title != null) {
      this.title = title;
    }
    if (buyer != null) {
      this.buyer = buyer;
    }
    if (category != null) {
      this.category = category;
    }
    if (spec != null) {
      this.spec = spec;
    }
    if (city != null) {
      this.city = city;
    }
    if (budget != null) {
      this.budget = budget;
    }
    if (arrival != null) {
      this.arrival = arrival;
    }
    if (demand != null) {
      this.demand = demand;
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
