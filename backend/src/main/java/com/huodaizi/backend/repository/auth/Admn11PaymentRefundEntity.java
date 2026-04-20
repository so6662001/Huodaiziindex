package com.huodaizi.backend.repository.auth;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Admn11PaymentRefundEntity {
  private final String refundId;
  private final String refundNo;
  private final String cashierOrderId;
  private final String orderId;
  private final String orderNo;
  private final String inquiryNo;
  private final String userId;
  private final String buyerCompany;
  private final String supplierName;
  private final String goodsName;
  private final String payChannel;
  private final String payChannelText;
  private final String payableAmountYuan;
  private final String paidAmountYuan;
  private final String refundType;
  private final String refundReasonCode;
  private String refundAmountYuan;
  private String approvedAmountYuan;
  private String refundedAmountYuan;
  private String refundStatus;
  private String rejectReason;
  private final String applicant;
  private String reviewer;
  private String financeOperator;
  private String latestRemark;
  private final String requestedAt;
  private String reviewedAt;
  private String refundedAt;
  private final LocalDateTime createdAt;
  private LocalDateTime updatedAt;
  private final List<ProgressNode> progressNodes;

  public Admn11PaymentRefundEntity(
      String refundId,
      String refundNo,
      String cashierOrderId,
      String orderId,
      String orderNo,
      String inquiryNo,
      String userId,
      String buyerCompany,
      String supplierName,
      String goodsName,
      String payChannel,
      String payChannelText,
      String payableAmountYuan,
      String paidAmountYuan,
      String refundType,
      String refundReasonCode,
      String refundAmountYuan,
      String approvedAmountYuan,
      String refundedAmountYuan,
      String refundStatus,
      String rejectReason,
      String applicant,
      String reviewer,
      String financeOperator,
      String latestRemark,
      String requestedAt,
      String reviewedAt,
      String refundedAt,
      LocalDateTime createdAt,
      LocalDateTime updatedAt) {
    this.refundId = refundId;
    this.refundNo = refundNo;
    this.cashierOrderId = cashierOrderId;
    this.orderId = orderId;
    this.orderNo = orderNo;
    this.inquiryNo = inquiryNo;
    this.userId = userId;
    this.buyerCompany = buyerCompany;
    this.supplierName = supplierName;
    this.goodsName = goodsName;
    this.payChannel = payChannel;
    this.payChannelText = payChannelText;
    this.payableAmountYuan = payableAmountYuan;
    this.paidAmountYuan = paidAmountYuan;
    this.refundType = refundType;
    this.refundReasonCode = refundReasonCode;
    this.refundAmountYuan = refundAmountYuan;
    this.approvedAmountYuan = approvedAmountYuan;
    this.refundedAmountYuan = refundedAmountYuan;
    this.refundStatus = refundStatus;
    this.rejectReason = rejectReason;
    this.applicant = applicant;
    this.reviewer = reviewer;
    this.financeOperator = financeOperator;
    this.latestRemark = latestRemark;
    this.requestedAt = requestedAt;
    this.reviewedAt = reviewedAt;
    this.refundedAt = refundedAt;
    this.createdAt = createdAt;
    this.updatedAt = updatedAt;
    this.progressNodes = new ArrayList<>();
  }

  public String getRefundId() {
    return refundId;
  }

  public String getRefundNo() {
    return refundNo;
  }

  public String getCashierOrderId() {
    return cashierOrderId;
  }

  // Backward-compatible alias
  public String getCashierId() {
    return cashierOrderId;
  }

  public String getOrderId() {
    return orderId;
  }

  public String getOrderNo() {
    return orderNo;
  }

  public String getInquiryNo() {
    return inquiryNo;
  }

  public String getUserId() {
    return userId;
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

  public String getPayChannel() {
    return payChannel;
  }

  public String getPayChannelText() {
    return payChannelText;
  }

  public String getPayableAmountYuan() {
    return payableAmountYuan;
  }

  public String getPaidAmountYuan() {
    return paidAmountYuan;
  }

  public String getRefundType() {
    return refundType;
  }

  public String getRefundReasonCode() {
    return refundReasonCode;
  }

  // Backward-compatible alias
  public String getRefundReason() {
    return refundReasonCode;
  }

  // Backward-compatible alias used by list mapping.
  public String getRefundReasonText() {
    return refundReasonCode;
  }

  public String getRefundAmountYuan() {
    return refundAmountYuan;
  }

  public String getApprovedAmountYuan() {
    return approvedAmountYuan;
  }

  public String getRefundedAmountYuan() {
    return refundedAmountYuan;
  }

  public String getRemainingAmountYuan() {
    java.math.BigDecimal paid = parseDecimal(paidAmountYuan);
    java.math.BigDecimal refunded = parseDecimal(refundedAmountYuan);
    java.math.BigDecimal remaining = paid.subtract(refunded);
    if (remaining.compareTo(java.math.BigDecimal.ZERO) < 0) {
      return "0";
    }
    return remaining.stripTrailingZeros().toPlainString();
  }

  public String getRefundStatus() {
    return refundStatus;
  }

  public String getRejectReason() {
    return rejectReason;
  }

  public String getApplicant() {
    return applicant;
  }

  public String getReviewer() {
    return reviewer;
  }

  public String getFinanceOperator() {
    return financeOperator;
  }

  public String getRequestedAt() {
    return requestedAt;
  }

  public String getReviewedAt() {
    return reviewedAt;
  }

  public String getRefundedAt() {
    return refundedAt;
  }

  // Backward-compatible alias
  public String getRefundChannel() {
    return payChannel;
  }

  public String getLatestRemark() {
    return latestRemark;
  }

  public LocalDateTime getCreatedAt() {
    return createdAt;
  }

  public LocalDateTime getUpdatedAt() {
    return updatedAt;
  }

  public List<ProgressNode> getProgressNodes() {
    return List.copyOf(progressNodes);
  }

  public void review(
      String nextStatus,
      String approvedAmountYuan,
      String rejectReason,
      String latestRemark,
      String reviewer,
      String financeOperator,
      String reviewedAt,
      String refundedAt,
      LocalDateTime now) {
    this.refundStatus = nextStatus;
    if (approvedAmountYuan != null && !approvedAmountYuan.isBlank()) {
      this.approvedAmountYuan = approvedAmountYuan.trim();
    }
    if ("REFUNDED".equalsIgnoreCase(nextStatus)) {
      this.refundedAmountYuan = this.approvedAmountYuan;
    }
    this.rejectReason = rejectReason == null ? "" : rejectReason.trim();
    this.latestRemark = latestRemark;
    this.reviewer = reviewer;
    this.financeOperator = financeOperator;
    this.reviewedAt = reviewedAt;
    this.refundedAt = refundedAt;
    this.updatedAt = now;
  }

  // Backward-compatible signature used by repository.
  public void review(
      String nextStatus, String latestRemark, String operator, String refundedAt, LocalDateTime now) {
    review(
        nextStatus,
        this.approvedAmountYuan,
        this.rejectReason,
        latestRemark,
        operator,
        "REFUNDED".equalsIgnoreCase(nextStatus) ? operator : this.financeOperator,
        now == null ? "" : now.toString(),
        refundedAt,
        now);
  }

  private java.math.BigDecimal parseDecimal(String value) {
    String normalized = value == null ? "" : value.trim();
    if (normalized.isBlank()) {
      return java.math.BigDecimal.ZERO;
    }
    try {
      return new java.math.BigDecimal(normalized);
    } catch (NumberFormatException ex) {
      return java.math.BigDecimal.ZERO;
    }
  }

  public void appendProgress(
      String nodeCode,
      String nodeName,
      String status,
      String statusText,
      String handler,
      String remark,
      String happenedAt) {
    progressNodes.add(new ProgressNode(nodeCode, nodeName, status, statusText, handler, remark, happenedAt));
  }

  // Backward-compatible alias
  public void appendProgressNode(
      String nodeCode,
      String nodeName,
      String status,
      String statusText,
      String handler,
      String remark,
      String happenedAt) {
    appendProgress(nodeCode, nodeName, status, statusText, handler, remark, happenedAt);
  }

  public static final class ProgressNode {
    private final String nodeCode;
    private final String nodeName;
    private final String status;
    private final String statusText;
    private final String handler;
    private final String remark;
    private final String happenedAt;

    public ProgressNode(
        String nodeCode,
        String nodeName,
        String status,
        String statusText,
        String handler,
        String remark,
        String happenedAt) {
      this.nodeCode = nodeCode;
      this.nodeName = nodeName;
      this.status = status;
      this.statusText = statusText;
      this.handler = handler;
      this.remark = remark;
      this.happenedAt = happenedAt;
    }

    public String getNodeCode() {
      return nodeCode;
    }

    public String getNodeName() {
      return nodeName;
    }

    public String getStatus() {
      return status;
    }

    public String getStatusText() {
      return statusText;
    }

    public String getHandler() {
      return handler;
    }

    public String getRemark() {
      return remark;
    }

    public String getHappenedAt() {
      return happenedAt;
    }
  }
}
