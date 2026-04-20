package com.huodaizi.backend.repository.inquiry;

import com.huodaizi.backend.dto.inquiry.InquiryStatus;
import java.time.LocalDateTime;

public class InquiryEntity {
  private final String id;
  private final String inquiryNo;
  private final String contactMobile;
  private final String categoryCode;
  private final String specText;
  private final String deliveryCity;
  private final String demandQtyTon;
  private final String expectedDeliveryAt;
  private final String invoiceNeed;
  private final String remark;
  private final LocalDateTime createdAt;
  private LocalDateTime updatedAt;
  private InquiryStatus status;
  private int quoteSupplierCount;

  public InquiryEntity(
      String id,
      String inquiryNo,
      String contactMobile,
      String categoryCode,
      String specText,
      String deliveryCity,
      String demandQtyTon,
      String expectedDeliveryAt,
      String invoiceNeed,
      String remark,
      InquiryStatus status,
      int quoteSupplierCount,
      LocalDateTime createdAt,
      LocalDateTime updatedAt) {
    this.id = id;
    this.inquiryNo = inquiryNo;
    this.contactMobile = contactMobile;
    this.categoryCode = categoryCode;
    this.specText = specText;
    this.deliveryCity = deliveryCity;
    this.demandQtyTon = demandQtyTon;
    this.expectedDeliveryAt = expectedDeliveryAt;
    this.invoiceNeed = invoiceNeed;
    this.remark = remark;
    this.status = status;
    this.quoteSupplierCount = quoteSupplierCount;
    this.createdAt = createdAt;
    this.updatedAt = updatedAt;
  }

  public String getId() {
    return id;
  }

  public String getInquiryNo() {
    return inquiryNo;
  }

  public String getContactMobile() {
    return contactMobile;
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

  public String getExpectedDeliveryAt() {
    return expectedDeliveryAt;
  }

  public String getInvoiceNeed() {
    return invoiceNeed;
  }

  public String getRemark() {
    return remark;
  }

  public InquiryStatus getStatus() {
    return status;
  }

  public LocalDateTime getCreatedAt() {
    return createdAt;
  }

  public LocalDateTime getUpdatedAt() {
    return updatedAt;
  }

  public int getQuoteSupplierCount() {
    return quoteSupplierCount;
  }

  public void setStatus(InquiryStatus status) {
    this.status = status;
    this.updatedAt = LocalDateTime.now();
  }

  public void setQuoteSupplierCount(int quoteSupplierCount) {
    this.quoteSupplierCount = quoteSupplierCount;
    this.updatedAt = LocalDateTime.now();
  }
}
