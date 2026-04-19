package com.huodaizi.backend.repository.inquiry;

import com.huodaizi.backend.dto.inquiry.InquiryMerchantLeadStatus;
import java.time.LocalDateTime;

public class InquiryMerchantLeadEntity {
  private final String id;
  private final String leadNo;
  private final String inquiryId;
  private final String inquiryNo;
  private final String merchantId;
  private final String merchantName;
  private final String buyerCompany;
  private final String contactNameMasked;
  private final String contactMobileMasked;
  private final String specText;
  private final String demandQtyTon;
  private final String deliveryCity;
  private final String invoiceNeed;
  private final String expectedDeliveryAt;
  private InquiryMerchantLeadStatus status;
  private String unitPrice;
  private String totalAmount;
  private String deliveryDays;
  private String responseMinutes;
  private String paymentTerm;
  private String quoteRemark;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;

  public InquiryMerchantLeadEntity(
      String id,
      String leadNo,
      String inquiryId,
      String inquiryNo,
      String merchantId,
      String merchantName,
      String buyerCompany,
      String contactNameMasked,
      String contactMobileMasked,
      String specText,
      String demandQtyTon,
      String deliveryCity,
      String invoiceNeed,
      String expectedDeliveryAt,
      InquiryMerchantLeadStatus status,
      String unitPrice,
      String totalAmount,
      String deliveryDays,
      String responseMinutes,
      String paymentTerm,
      String quoteRemark,
      LocalDateTime createdAt,
      LocalDateTime updatedAt) {
    this.id = id;
    this.leadNo = leadNo;
    this.inquiryId = inquiryId;
    this.inquiryNo = inquiryNo;
    this.merchantId = merchantId;
    this.merchantName = merchantName;
    this.buyerCompany = buyerCompany;
    this.contactNameMasked = contactNameMasked;
    this.contactMobileMasked = contactMobileMasked;
    this.specText = specText;
    this.demandQtyTon = demandQtyTon;
    this.deliveryCity = deliveryCity;
    this.invoiceNeed = invoiceNeed;
    this.expectedDeliveryAt = expectedDeliveryAt;
    this.status = status;
    this.createdAt = createdAt;
    this.updatedAt = updatedAt;
    this.unitPrice = unitPrice;
    this.totalAmount = totalAmount;
    this.deliveryDays = deliveryDays;
    this.responseMinutes = responseMinutes;
    this.paymentTerm = paymentTerm;
    this.quoteRemark = quoteRemark;
  }

  public String getId() {
    return id;
  }

  public String getLeadNo() {
    return leadNo;
  }

  public String getInquiryId() {
    return inquiryId;
  }

  public String getInquiryNo() {
    return inquiryNo;
  }

  public String getMerchantId() {
    return merchantId;
  }

  public String getMerchantName() {
    return merchantName;
  }

  public String getBuyerCompany() {
    return buyerCompany;
  }

  public String getContactNameMasked() {
    return contactNameMasked;
  }

  public String getSpecText() {
    return specText;
  }

  public String getDemandQtyTon() {
    return demandQtyTon;
  }

  public String getDeliveryCity() {
    return deliveryCity;
  }

  public String getInvoiceNeed() {
    return invoiceNeed;
  }

  public String getExpectedDeliveryAt() {
    return expectedDeliveryAt;
  }

  public String getContactMobileMasked() {
    return contactMobileMasked;
  }

  public InquiryMerchantLeadStatus getStatus() {
    return status;
  }

  public LocalDateTime getCreatedAt() {
    return createdAt;
  }

  public String getUnitPrice() {
    return unitPrice;
  }

  public String getTotalAmount() {
    return totalAmount;
  }

  public String getDeliveryDays() {
    return deliveryDays;
  }

  public String getResponseMinutes() {
    return responseMinutes;
  }

  public String getPaymentTerm() {
    return paymentTerm;
  }

  public String getQuoteRemark() {
    return quoteRemark;
  }

  public LocalDateTime getUpdatedAt() {
    return updatedAt;
  }

  public void setStatus(InquiryMerchantLeadStatus status) {
    this.status = status;
    this.updatedAt = LocalDateTime.now();
  }

  public void updateQuote(
      String unitPrice,
      String totalAmount,
      String deliveryDays,
      String paymentTerm,
      String quoteRemark) {
    this.unitPrice = unitPrice;
    this.totalAmount = totalAmount;
    this.deliveryDays = deliveryDays;
    this.paymentTerm = paymentTerm;
    this.quoteRemark = quoteRemark;
    this.responseMinutes = "5";
    this.status = InquiryMerchantLeadStatus.QUOTED;
    this.updatedAt = LocalDateTime.now();
  }

  public void updateStatus(InquiryMerchantLeadStatus status, String remark) {
    this.status = status;
    if (remark != null && !remark.isBlank()) {
      this.quoteRemark = remark.trim();
    }
    this.updatedAt = LocalDateTime.now();
  }
}
