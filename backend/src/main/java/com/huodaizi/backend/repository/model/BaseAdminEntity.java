package com.huodaizi.backend.repository.model;

import java.time.LocalDateTime;

public class BaseAdminEntity {
  private final String id;
  private final AdminEntityType type;
  private String name;
  private String title;
  private String city;
  private String category;
  private String meta;
  private String price;
  private String trend;
  private LocalDateTime updatedAt;

  public BaseAdminEntity(
      String id,
      AdminEntityType type,
      String name,
      String title,
      String city,
      String category,
      String meta,
      String price,
      String trend,
      LocalDateTime updatedAt) {
    this.id = id;
    this.type = type;
    this.name = name;
    this.title = title;
    this.city = city;
    this.category = category;
    this.meta = meta;
    this.price = price;
    this.trend = trend;
    this.updatedAt = updatedAt;
  }

  public String getId() {
    return id;
  }

  public AdminEntityType getType() {
    return type;
  }

  public String getName() {
    return name;
  }

  public String getTitle() {
    return title;
  }

  public String getCity() {
    return city;
  }

  public String getCategory() {
    return category;
  }

  public String getMeta() {
    return meta;
  }

  public String getPrice() {
    return price;
  }

  public String getTrend() {
    return trend;
  }

  public LocalDateTime getUpdatedAt() {
    return updatedAt;
  }

  public void update(
      String name,
      String title,
      String city,
      String category,
      String meta,
      String price,
      String trend) {
    this.name = name;
    this.title = title;
    this.city = city;
    this.category = category;
    this.meta = meta;
    this.price = price;
    this.trend = trend;
    this.updatedAt = LocalDateTime.now();
  }
}
