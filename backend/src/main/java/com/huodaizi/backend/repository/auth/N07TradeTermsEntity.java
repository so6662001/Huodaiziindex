package com.huodaizi.backend.repository.auth;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class N07TradeTermsEntity {
  private final String orderId;
  private final String userId;
  private final String orderNo;
  private final String inquiryNo;
  private final String buyerCompany;
  private final String supplierName;
  private final String goodsName;
  private final String specText;
  private final String quantityTon;
  private final String unitPrice;
  private final String totalAmount;
  private final String deliveryTerm;
  private final String paymentTerm;
  private final String invoiceTerm;
  private final String settlementMethod;
  private final String qualityStandard;
  private final String toleranceRange;
  private final String breachLiability;
  private final String disputeResolution;
  private final String otherClause;
  private final String effectiveDate;
  private final String expireDate;
  private final List<ClauseItem> clauses;
  private final List<AttachmentItem> attachments;
  private boolean confirmed;
  private String confirmedBy;
  private String confirmedAt;
  private String confirmRemark;
  private LocalDateTime updatedAt;
  private final LocalDateTime createdAt;

  public N07TradeTermsEntity(
      String orderId,
      String userId,
      String orderNo,
      String inquiryNo,
      String buyerCompany,
      String supplierName,
      String goodsName,
      String specText,
      String quantityTon,
      String unitPrice,
      String totalAmount,
      String deliveryTerm,
      String paymentTerm,
      String invoiceTerm,
      String settlementMethod,
      String qualityStandard,
      String toleranceRange,
      String breachLiability,
      String disputeResolution,
      String otherClause,
      String effectiveDate,
      String expireDate,
      List<ClauseItem> clauses,
      List<AttachmentItem> attachments,
      boolean confirmed,
      String confirmedBy,
      String confirmedAt,
      String confirmRemark,
      LocalDateTime updatedAt,
      LocalDateTime createdAt) {
    this.orderId = orderId;
    this.userId = userId;
    this.orderNo = orderNo;
    this.inquiryNo = inquiryNo;
    this.buyerCompany = buyerCompany;
    this.supplierName = supplierName;
    this.goodsName = goodsName;
    this.specText = specText;
    this.quantityTon = quantityTon;
    this.unitPrice = unitPrice;
    this.totalAmount = totalAmount;
    this.deliveryTerm = deliveryTerm;
    this.paymentTerm = paymentTerm;
    this.invoiceTerm = invoiceTerm;
    this.settlementMethod = settlementMethod;
    this.qualityStandard = qualityStandard;
    this.toleranceRange = toleranceRange;
    this.breachLiability = breachLiability;
    this.disputeResolution = disputeResolution;
    this.otherClause = otherClause;
    this.effectiveDate = effectiveDate;
    this.expireDate = expireDate;
    this.clauses = clauses == null ? new ArrayList<>() : new ArrayList<>(clauses);
    this.attachments = attachments == null ? new ArrayList<>() : new ArrayList<>(attachments);
    this.confirmed = confirmed;
    this.confirmedBy = confirmedBy;
    this.confirmedAt = confirmedAt;
    this.confirmRemark = confirmRemark;
    this.updatedAt = updatedAt;
    this.createdAt = createdAt;
  }

  public String getOrderId() {
    return orderId;
  }

  public String getUserId() {
    return userId;
  }

  public String getOrderNo() {
    return orderNo;
  }

  public String getInquiryNo() {
    return inquiryNo;
  }

  public String getBuyerCompany() {
    return buyerCompany;
  }

  public String getSupplierName() {
    return supplierName;
  }

  public String getGoodsName() {
    return goodsName;
  }

  public String getSpecText() {
    return specText;
  }

  public String getQuantityTon() {
    return quantityTon;
  }

  public String getUnitPrice() {
    return unitPrice;
  }

  public String getTotalAmount() {
    return totalAmount;
  }

  public String getDeliveryTerm() {
    return deliveryTerm;
  }

  public String getPaymentTerm() {
    return paymentTerm;
  }

  public String getInvoiceTerm() {
    return invoiceTerm;
  }

  public String getSettlementMethod() {
    return settlementMethod;
  }

  public String getQualityStandard() {
    return qualityStandard;
  }

  public String getToleranceRange() {
    return toleranceRange;
  }

  public String getBreachLiability() {
    return breachLiability;
  }

  public String getDisputeResolution() {
    return disputeResolution;
  }

  public String getOtherClause() {
    return otherClause;
  }

  public String getEffectiveDate() {
    return effectiveDate;
  }

  public String getExpireDate() {
    return expireDate;
  }

  public List<ClauseItem> getClauses() {
    return List.copyOf(clauses);
  }

  public List<AttachmentItem> getAttachments() {
    return List.copyOf(attachments);
  }

  public boolean isConfirmed() {
    return confirmed;
  }

  public String getConfirmedBy() {
    return confirmedBy;
  }

  public String getConfirmedAt() {
    return confirmedAt;
  }

  public String getConfirmRemark() {
    return confirmRemark;
  }

  public LocalDateTime getUpdatedAt() {
    return updatedAt;
  }

  public LocalDateTime getCreatedAt() {
    return createdAt;
  }

  public void confirm(String operator, String remark, LocalDateTime now) {
    confirmed = true;
    confirmedBy = operator;
    confirmedAt = now.toString();
    confirmRemark = remark;
    updatedAt = now;
  }

  public static final class ClauseItem {
    private final String clauseCode;
    private final String clauseName;
    private final String clauseContent;
    private final boolean required;

    public ClauseItem(String clauseCode, String clauseName, String clauseContent, boolean required) {
      this.clauseCode = clauseCode;
      this.clauseName = clauseName;
      this.clauseContent = clauseContent;
      this.required = required;
    }

    public String getClauseCode() {
      return clauseCode;
    }

    public String getClauseName() {
      return clauseName;
    }

    public String getClauseContent() {
      return clauseContent;
    }

    public boolean isRequired() {
      return required;
    }
  }

  public static final class AttachmentItem {
    private final String fileName;
    private final String fileType;
    private final String fileUrl;

    public AttachmentItem(String fileName, String fileType, String fileUrl) {
      this.fileName = fileName;
      this.fileType = fileType;
      this.fileUrl = fileUrl;
    }

    public String getFileName() {
      return fileName;
    }

    public String getFileType() {
      return fileType;
    }

    public String getFileUrl() {
      return fileUrl;
    }
  }
}
