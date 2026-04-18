package com.huodaizi.backend.repository.freightdetail;

import com.huodaizi.backend.dto.freightdetail.FreightDetailSectionType;
import java.time.LocalDateTime;

public class FreightDetailEntity {
  private final String id;
  private final String freightId;
  private final FreightDetailSectionType sectionType;
  private String title;
  private String provider;
  private String route;
  private String vehicle;
  private String loadRange;
  private String frequency;
  private String timeliness;
  private String quote;
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

  public FreightDetailEntity(
      String id,
      String freightId,
      FreightDetailSectionType sectionType,
      String title,
      String provider,
      String route,
      String vehicle,
      String loadRange,
      String frequency,
      String timeliness,
      String quote,
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
    this.freightId = freightId;
    this.sectionType = sectionType;
    this.title = title;
    this.provider = provider;
    this.route = route;
    this.vehicle = vehicle;
    this.loadRange = loadRange;
    this.frequency = frequency;
    this.timeliness = timeliness;
    this.quote = quote;
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

  public String getFreightId() {
    return freightId;
  }

  public FreightDetailSectionType getSectionType() {
    return sectionType;
  }

  public String getTitle() {
    return title;
  }

  public String getProvider() {
    return provider;
  }

  public String getRoute() {
    return route;
  }

  public String getVehicle() {
    return vehicle;
  }

  public String getLoadRange() {
    return loadRange;
  }

  public String getFrequency() {
    return frequency;
  }

  public String getTimeliness() {
    return timeliness;
  }

  public String getQuote() {
    return quote;
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
      String provider,
      String route,
      String vehicle,
      String loadRange,
      String frequency,
      String timeliness,
      String quote,
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
    if (provider != null) {
      this.provider = provider;
    }
    if (route != null) {
      this.route = route;
    }
    if (vehicle != null) {
      this.vehicle = vehicle;
    }
    if (loadRange != null) {
      this.loadRange = loadRange;
    }
    if (frequency != null) {
      this.frequency = frequency;
    }
    if (timeliness != null) {
      this.timeliness = timeliness;
    }
    if (quote != null) {
      this.quote = quote;
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
