package com.huodaizi.backend.repository.inquiry;

import java.time.LocalDateTime;

public class InquiryH5InquiryStep1DraftEntity {
  private final String draftId;
  private final String categoryCode;
  private final String specText;
  private final String deliveryCity;
  private final String demandQtyTon;
  private final String invoiceNeed;
  private final String contactMobile;
  private final String remark;
  private final String expectedDeliveryAt;
  private final String deliveryTimeRange;
  private final String unloadSupport;
  private final String needInvoice;
  private final String step2Remark;
  private final String inquiryId;
  private final String inquiryNo;
  private final String status;
  private final LocalDateTime createdAt;
  private final LocalDateTime updatedAt;

  public InquiryH5InquiryStep1DraftEntity(
      String draftId,
      String categoryCode,
      String specText,
      String deliveryCity,
      String demandQtyTon,
      String invoiceNeed,
      String contactMobile,
      String remark,
      String expectedDeliveryAt,
      String deliveryTimeRange,
      String unloadSupport,
      String needInvoice,
      String step2Remark,
      String inquiryId,
      String inquiryNo,
      String status,
      LocalDateTime createdAt,
      LocalDateTime updatedAt) {
    this.draftId = draftId;
    this.categoryCode = categoryCode;
    this.specText = specText;
    this.deliveryCity = deliveryCity;
    this.demandQtyTon = demandQtyTon;
    this.invoiceNeed = invoiceNeed;
    this.contactMobile = contactMobile;
    this.remark = remark;
    this.expectedDeliveryAt = expectedDeliveryAt;
    this.deliveryTimeRange = deliveryTimeRange;
    this.unloadSupport = unloadSupport;
    this.needInvoice = needInvoice;
    this.step2Remark = step2Remark;
    this.inquiryId = inquiryId;
    this.inquiryNo = inquiryNo;
    this.status = status;
    this.createdAt = createdAt;
    this.updatedAt = updatedAt;
  }

  public String getDraftId() {
    return draftId;
  }

  public String getCategoryCode() {
    return categoryCode;
  }

  public String getSpecText() {
    return specText;
  }

  public String getDeliveryCity() {
    return deliveryCity;
  }

  public String getDemandQtyTon() {
    return demandQtyTon;
  }

  public String getInvoiceNeed() {
    return invoiceNeed;
  }

  public String getContactMobile() {
    return contactMobile;
  }

  public String getRemark() {
    return remark;
  }

  public String getExpectedDeliveryAt() {
    return expectedDeliveryAt;
  }

  public String getDeliveryTimeRange() {
    return deliveryTimeRange;
  }

  public String getUnloadSupport() {
    return unloadSupport;
  }

  public String getNeedInvoice() {
    return needInvoice;
  }

  public String getStep2Remark() {
    return step2Remark;
  }

  public String getInquiryId() {
    return inquiryId;
  }

  public String getInquiryNo() {
    return inquiryNo;
  }

  public String getStatus() {
    return status;
  }

  public LocalDateTime getCreatedAt() {
    return createdAt;
  }

  public LocalDateTime getUpdatedAt() {
    return updatedAt;
  }
}
