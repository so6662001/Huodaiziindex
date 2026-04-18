package com.huodaizi.backend.repository.spot;

import java.time.LocalDateTime;

public class SpotEntity {
  private final String id;
  private String title;
  private String seller;
  private String category;
  private String spec;
  private String city;
  private String price;
  private String delivery;
  private String tonnage;
  private String contactName;
  private String contactPhone;
  private String status;
  private boolean pinned;
  private LocalDateTime updatedAt;

  public SpotEntity(
      String id,
      String title,
      String seller,
      String category,
      String spec,
      String city,
      String price,
      String delivery,
      String tonnage,
      String contactName,
      String contactPhone,
      String status,
      boolean pinned,
      LocalDateTime updatedAt) {
    this.id = id;
    this.title = title;
    this.seller = seller;
    this.category = category;
    this.spec = spec;
    this.city = city;
    this.price = price;
    this.delivery = delivery;
    this.tonnage = tonnage;
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

  public String getSeller() {
    return seller;
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

  public String getPrice() {
    return price;
  }

  public String getDelivery() {
    return delivery;
  }

  public String getTonnage() {
    return tonnage;
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
      String seller,
      String category,
      String spec,
      String city,
      String price,
      String delivery,
      String tonnage,
      String contactName,
      String contactPhone,
      String status,
      Boolean pinned) {
    if (title != null) {
      this.title = title;
    }
    if (seller != null) {
      this.seller = seller;
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
    if (price != null) {
      this.price = price;
    }
    if (delivery != null) {
      this.delivery = delivery;
    }
    if (tonnage != null) {
      this.tonnage = tonnage;
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
