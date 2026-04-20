package com.huodaizi.backend.repository.auth;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Admn12AdSlotScheduleEntity {
  private final String scheduleId;
  private String slotCode;
  private String slotName;
  private String slotType;
  private String cityCode;
  private String cityName;
  private String scheduleStatus;
  private String scheduleFillStatus;
  private String startDate;
  private String endDate;
  private String totalSlots;
  private String soldSlots;
  private String pricePerDay;
  private String creativeUrl;
  private String advertiserName;
  private String campaignName;
  private String operator;
  private String remark;
  private final LocalDateTime createdAt;
  private LocalDateTime updatedAt;
  private final List<ScheduleWindow> windows;

  public Admn12AdSlotScheduleEntity(
      String scheduleId,
      String slotCode,
      String slotName,
      String slotType,
      String cityCode,
      String cityName,
      String scheduleStatus,
      String scheduleFillStatus,
      String startDate,
      String endDate,
      String totalSlots,
      String soldSlots,
      String pricePerDay,
      String creativeUrl,
      String advertiserName,
      String campaignName,
      String operator,
      String remark,
      LocalDateTime createdAt,
      LocalDateTime updatedAt) {
    this.scheduleId = scheduleId;
    this.slotCode = slotCode;
    this.slotName = slotName;
    this.slotType = slotType;
    this.cityCode = cityCode;
    this.cityName = cityName;
    this.scheduleStatus = scheduleStatus;
    this.scheduleFillStatus = scheduleFillStatus;
    this.startDate = startDate;
    this.endDate = endDate;
    this.totalSlots = totalSlots;
    this.soldSlots = soldSlots;
    this.pricePerDay = pricePerDay;
    this.creativeUrl = creativeUrl;
    this.advertiserName = advertiserName;
    this.campaignName = campaignName;
    this.operator = operator;
    this.remark = remark;
    this.createdAt = createdAt;
    this.updatedAt = updatedAt;
    this.windows = new ArrayList<>();
  }

  public String getScheduleId() {
    return scheduleId;
  }

  public String getScheduleCode() {
    return scheduleId;
  }

  public String getScheduleNo() {
    return scheduleId;
  }

  public String getSlotId() {
    return slotCode;
  }

  public String getSlotCode() {
    return slotCode;
  }

  public String getSlotName() {
    return slotName;
  }

  public String getSlotType() {
    return slotType;
  }

  public String getCityCode() {
    return cityCode;
  }

  public String getCityName() {
    return cityName;
  }

  public String getScheduleStatus() {
    return scheduleStatus;
  }

  public String getScheduleFillStatus() {
    return scheduleFillStatus;
  }

  public String getStartDate() {
    return startDate;
  }

  public String getEndDate() {
    return endDate;
  }

  public String getTotalSlots() {
    return totalSlots;
  }

  public String getSoldSlots() {
    return soldSlots;
  }

  public String getPricePerDay() {
    return pricePerDay;
  }

  public String getCreativeUrl() {
    return creativeUrl;
  }

  public String getAdvertiserName() {
    return advertiserName;
  }

  public String getCampaignName() {
    return campaignName;
  }

  public String getRemark() {
    return remark;
  }

  public String getOperator() {
    return operator;
  }

  public LocalDateTime getCreatedAt() {
    return createdAt;
  }

  public LocalDateTime getUpdatedAt() {
    return updatedAt;
  }

  public List<ScheduleWindow> getWindows() {
    return List.copyOf(windows);
  }

  public void update(
      String slotName,
      String slotType,
      String cityCode,
      String cityName,
      String scheduleStatus,
      String scheduleFillStatus,
      String startDate,
      String endDate,
      String totalSlots,
      String soldSlots,
      String pricePerDay,
      String creativeUrl,
      String advertiserName,
      String campaignName,
      String operator,
      String remark,
      List<ScheduleWindow> windows,
      LocalDateTime now) {
    this.slotName = slotName;
    this.slotType = slotType;
    this.cityCode = cityCode;
    this.cityName = cityName;
    this.scheduleStatus = scheduleStatus;
    this.scheduleFillStatus = scheduleFillStatus;
    this.startDate = startDate;
    this.endDate = endDate;
    this.totalSlots = totalSlots;
    this.soldSlots = soldSlots;
    this.pricePerDay = pricePerDay;
    this.creativeUrl = creativeUrl;
    this.advertiserName = advertiserName;
    this.campaignName = campaignName;
    this.operator = operator;
    this.remark = remark;
    this.updatedAt = now;
    this.windows.clear();
    if (windows != null) {
      this.windows.addAll(windows);
    }
  }

  public void appendWindow(
      String startDate,
      String endDate,
      String windowStatus,
      String windowStatusText,
      String bookedBy) {
    this.windows.add(
        new ScheduleWindow(startDate, endDate, windowStatus, windowStatusText, bookedBy));
  }

  public int getRemainingSlots() {
    return parseIntSafe(totalSlots) - parseIntSafe(soldSlots);
  }

  private int parseIntSafe(String value) {
    if (value == null || value.isBlank()) {
      return 0;
    }
    try {
      return Integer.parseInt(value.trim());
    } catch (NumberFormatException ex) {
      return 0;
    }
  }

  public record ScheduleWindow(
      String startDate,
      String endDate,
      String windowStatus,
      String windowStatusText,
      String bookedBy) {}
}
