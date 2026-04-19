package com.huodaizi.backend.repository.inquiry;

import com.huodaizi.backend.dto.inquiry.InquiryPickupOrderStatus;
import java.time.LocalDateTime;

public class InquiryPickupOrderEntity {
  private final String pickupId;
  private final String pickupNo;
  private final String inquiryId;
  private final String inquiryNo;
  private final String quoteId;
  private final String supplierId;
  private final String supplierName;
  private final String buyerCompany;
  private final String buyerContact;
  private final String buyerPhone;
  private final String buyerPhoneMasked;
  private final String pickupAddress;
  private final String pickupDate;
  private final String driverName;
  private final String driverPhoneMasked;
  private final String truckNo;
  private final String quantityTon;
  private final String specText;
  private final String remark;
  private InquiryPickupOrderStatus status;
  private final LocalDateTime createdAt;
  private LocalDateTime updatedAt;

  public InquiryPickupOrderEntity(
      String pickupId,
      String pickupNo,
      String inquiryId,
      String inquiryNo,
      String quoteId,
      String supplierId,
      String supplierName,
      String buyerCompany,
      String buyerContact,
      String buyerPhone,
      String buyerPhoneMasked,
      String pickupAddress,
      String pickupDate,
      String driverName,
      String driverPhoneMasked,
      String truckNo,
      String quantityTon,
      String specText,
      String remark,
      InquiryPickupOrderStatus status,
      LocalDateTime createdAt,
      LocalDateTime updatedAt) {
    this.pickupId = pickupId;
    this.pickupNo = pickupNo;
    this.inquiryId = inquiryId;
    this.inquiryNo = inquiryNo;
    this.quoteId = quoteId;
    this.supplierId = supplierId;
    this.supplierName = supplierName;
    this.buyerCompany = buyerCompany;
    this.buyerContact = buyerContact;
    this.buyerPhone = buyerPhone;
    this.buyerPhoneMasked = buyerPhoneMasked;
    this.pickupAddress = pickupAddress;
    this.pickupDate = pickupDate;
    this.driverName = driverName;
    this.driverPhoneMasked = driverPhoneMasked;
    this.truckNo = truckNo;
    this.quantityTon = quantityTon;
    this.specText = specText;
    this.remark = remark;
    this.status = status;
    this.createdAt = createdAt;
    this.updatedAt = updatedAt;
  }

  public String getPickupId() {
    return pickupId;
  }

  public String getPickupNo() {
    return pickupNo;
  }

  public String getInquiryId() {
    return inquiryId;
  }

  public String getInquiryNo() {
    return inquiryNo;
  }

  public String getQuoteId() {
    return quoteId;
  }

  public String getSupplierId() {
    return supplierId;
  }

  public String getSupplierName() {
    return supplierName;
  }

  public String getBuyerCompany() {
    return buyerCompany;
  }

  public String getBuyerContact() {
    return buyerContact;
  }

  public String getBuyerPhoneMasked() {
    return buyerPhoneMasked;
  }

  public String getBuyerPhone() {
    return buyerPhone;
  }

  public String getPickupAddress() {
    return pickupAddress;
  }

  public String getPickupDate() {
    return pickupDate;
  }

  public String getDriverName() {
    return driverName;
  }

  public String getDriverPhoneMasked() {
    return driverPhoneMasked;
  }

  public String getTruckNo() {
    return truckNo;
  }

  public String getQuantityTon() {
    return quantityTon;
  }

  public String getSpecText() {
    return specText;
  }

  public String getRemark() {
    return remark;
  }

  public InquiryPickupOrderStatus getStatus() {
    return status;
  }

  public LocalDateTime getCreatedAt() {
    return createdAt;
  }

  public LocalDateTime getUpdatedAt() {
    return updatedAt;
  }

  public void setStatus(InquiryPickupOrderStatus status) {
    this.status = status;
    this.updatedAt = LocalDateTime.now();
  }
}
