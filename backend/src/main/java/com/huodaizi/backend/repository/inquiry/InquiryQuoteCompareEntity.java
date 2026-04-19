package com.huodaizi.backend.repository.inquiry;

import java.time.LocalDateTime;

public class InquiryQuoteCompareEntity {
  private final String inquiryId;
  private final String quoteId;
  private final String supplierId;
  private final String supplierName;
  private final String supplierLevel;
  private final String pricePerTon;
  private final String totalAmount;
  private final String taxMode;
  private final String paymentTerm;
  private final String deliveryDays;
  private final String inventoryLocation;
  private final String serviceScore;
  private final String fulfillmentRate;
  private final String responseMinutes;
  private final String canInvoice;
  private final String canFreight;
  private final String quoteRemark;
  private final LocalDateTime quoteTime;

  public InquiryQuoteCompareEntity(
      String inquiryId,
      String quoteId,
      String supplierId,
      String supplierName,
      String supplierLevel,
      String pricePerTon,
      String totalAmount,
      String taxMode,
      String paymentTerm,
      String deliveryDays,
      String inventoryLocation,
      String serviceScore,
      String fulfillmentRate,
      String responseMinutes,
      String canInvoice,
      String canFreight,
      String quoteRemark,
      LocalDateTime quoteTime) {
    this.inquiryId = inquiryId;
    this.quoteId = quoteId;
    this.supplierId = supplierId;
    this.supplierName = supplierName;
    this.supplierLevel = supplierLevel;
    this.pricePerTon = pricePerTon;
    this.totalAmount = totalAmount;
    this.taxMode = taxMode;
    this.paymentTerm = paymentTerm;
    this.deliveryDays = deliveryDays;
    this.inventoryLocation = inventoryLocation;
    this.serviceScore = serviceScore;
    this.fulfillmentRate = fulfillmentRate;
    this.responseMinutes = responseMinutes;
    this.canInvoice = canInvoice;
    this.canFreight = canFreight;
    this.quoteRemark = quoteRemark;
    this.quoteTime = quoteTime;
  }

  public String getInquiryId() {
    return inquiryId;
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

  public String getSupplierLevel() {
    return supplierLevel;
  }

  public String getPricePerTon() {
    return pricePerTon;
  }

  public String getTotalAmount() {
    return totalAmount;
  }

  public String getTaxMode() {
    return taxMode;
  }

  public String getPaymentTerm() {
    return paymentTerm;
  }

  public String getDeliveryDays() {
    return deliveryDays;
  }

  public String getInventoryLocation() {
    return inventoryLocation;
  }

  public String getServiceScore() {
    return serviceScore;
  }

  public String getFulfillmentRate() {
    return fulfillmentRate;
  }

  public String getResponseMinutes() {
    return responseMinutes;
  }

  public String getCanInvoice() {
    return canInvoice;
  }

  public String getCanFreight() {
    return canFreight;
  }

  public String getQuoteRemark() {
    return quoteRemark;
  }

  public LocalDateTime getQuoteTime() {
    return quoteTime;
  }
}
