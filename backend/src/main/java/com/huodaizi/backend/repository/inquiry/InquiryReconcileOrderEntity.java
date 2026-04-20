package com.huodaizi.backend.repository.inquiry;

import com.huodaizi.backend.dto.inquiry.InquiryReconcileOrderStatus;
import java.time.LocalDateTime;

public class InquiryReconcileOrderEntity {
  private final String reconcileId;
  private final String reconcileNo;
  private final String pickupOrderId;
  private final String pickupOrderNo;
  private final String inquiryId;
  private final String inquiryNo;
  private final String goodsSummary;
  private final String quoteId;
  private final String supplierId;
  private final String supplierName;
  private final String buyerCompany;
  private final String contactMobile;
  private final String contactMobileMasked;
  private final String statementMonth;
  private final String dueDate;
  private final String invoiceAmount;
  private final String deductionAmount;
  private final String receivableAmount;
  private String paidAmount;
  private String outstandingAmount;
  private String latestRemark;
  private InquiryReconcileOrderStatus status;
  private final LocalDateTime createdAt;
  private LocalDateTime updatedAt;

  public InquiryReconcileOrderEntity(
      String reconcileId,
      String reconcileNo,
      String pickupOrderId,
      String pickupOrderNo,
      String inquiryId,
      String inquiryNo,
      String goodsSummary,
      String quoteId,
      String supplierId,
      String supplierName,
      String buyerCompany,
      String contactMobile,
      String contactMobileMasked,
      String statementMonth,
      String dueDate,
      String invoiceAmount,
      String deductionAmount,
      String receivableAmount,
      String paidAmount,
      String outstandingAmount,
      String latestRemark,
      InquiryReconcileOrderStatus status,
      LocalDateTime createdAt,
      LocalDateTime updatedAt) {
    this.reconcileId = reconcileId;
    this.reconcileNo = reconcileNo;
    this.pickupOrderId = pickupOrderId;
    this.pickupOrderNo = pickupOrderNo;
    this.inquiryId = inquiryId;
    this.inquiryNo = inquiryNo;
    this.goodsSummary = goodsSummary;
    this.quoteId = quoteId;
    this.supplierId = supplierId;
    this.supplierName = supplierName;
    this.buyerCompany = buyerCompany;
    this.contactMobile = contactMobile;
    this.contactMobileMasked = contactMobileMasked;
    this.statementMonth = statementMonth;
    this.dueDate = dueDate;
    this.invoiceAmount = invoiceAmount;
    this.deductionAmount = deductionAmount;
    this.receivableAmount = receivableAmount;
    this.paidAmount = paidAmount;
    this.outstandingAmount = outstandingAmount;
    this.latestRemark = latestRemark;
    this.status = status;
    this.createdAt = createdAt;
    this.updatedAt = updatedAt;
  }

  public String getReconcileId() {
    return reconcileId;
  }

  public String getReconcileNo() {
    return reconcileNo;
  }

  public String getPickupOrderId() {
    return pickupOrderId;
  }

  public String getPickupOrderNo() {
    return pickupOrderNo;
  }

  public String getInquiryId() {
    return inquiryId;
  }

  public String getInquiryNo() {
    return inquiryNo;
  }

  public String getGoodsSummary() {
    return goodsSummary;
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

  public String getContactMobile() {
    return contactMobile;
  }

  public String getContactMobileMasked() {
    return contactMobileMasked;
  }

  public String getStatementMonth() {
    return statementMonth;
  }

  public String getDueDate() {
    return dueDate;
  }

  public String getInvoiceAmount() {
    return invoiceAmount;
  }

  public String getDeductionAmount() {
    return deductionAmount;
  }

  public String getReceivableAmount() {
    return receivableAmount;
  }

  public String getPaidAmount() {
    return paidAmount;
  }

  public String getOutstandingAmount() {
    return outstandingAmount;
  }

  public String getLatestRemark() {
    return latestRemark;
  }

  public InquiryReconcileOrderStatus getStatus() {
    return status;
  }

  public LocalDateTime getCreatedAt() {
    return createdAt;
  }

  public LocalDateTime getUpdatedAt() {
    return updatedAt;
  }

  public void setStatus(InquiryReconcileOrderStatus status) {
    this.status = status;
    this.updatedAt = LocalDateTime.now();
  }

  public void updateAmounts(String paidAmount, String outstandingAmount) {
    this.paidAmount = paidAmount;
    this.outstandingAmount = outstandingAmount;
    this.updatedAt = LocalDateTime.now();
  }

  public void setLatestRemark(String latestRemark) {
    this.latestRemark = latestRemark;
    this.updatedAt = LocalDateTime.now();
  }
}
