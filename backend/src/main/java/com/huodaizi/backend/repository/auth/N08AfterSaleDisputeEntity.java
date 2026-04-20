package com.huodaizi.backend.repository.auth;

import java.util.ArrayList;
import java.util.List;
import java.time.LocalDateTime;

public class N08AfterSaleDisputeEntity {
  private final String disputeId;
  private final String userId;
  private final String orderId;
  private final String orderNo;
  private final String inquiryNo;
  private final String buyerCompany;
  private final String supplierName;
  private final String issueType;
  private final String issueTypeText;
  private final String issueSummary;
  private final String issueDescription;
  private final String expectedResolution;
  private final String contactName;
  private final String contactPhoneMasked;
  private final String evidenceFiles;
  private String status;
  private String statusText;
  private String latestRemark;
  private final LocalDateTime createdAt;
  private LocalDateTime updatedAt;
  private final List<ProgressNode> progressNodes;

  public N08AfterSaleDisputeEntity(
      String disputeId,
      String userId,
      String orderId,
      String orderNo,
      String inquiryNo,
      String buyerCompany,
      String supplierName,
      String issueType,
      String issueTypeText,
      String issueSummary,
      String issueDescription,
      String expectedResolution,
      String contactName,
      String contactPhoneMasked,
      String evidenceFiles,
      String status,
      String statusText,
      String latestRemark,
      LocalDateTime createdAt,
      LocalDateTime updatedAt) {
    this.disputeId = disputeId;
    this.userId = userId;
    this.orderId = orderId;
    this.orderNo = orderNo;
    this.inquiryNo = inquiryNo;
    this.buyerCompany = buyerCompany;
    this.supplierName = supplierName;
    this.issueType = issueType;
    this.issueTypeText = issueTypeText;
    this.issueSummary = issueSummary;
    this.issueDescription = issueDescription;
    this.expectedResolution = expectedResolution;
    this.contactName = contactName;
    this.contactPhoneMasked = contactPhoneMasked;
    this.evidenceFiles = evidenceFiles;
    this.status = status;
    this.statusText = statusText;
    this.latestRemark = latestRemark;
    this.createdAt = createdAt;
    this.updatedAt = updatedAt;
    this.progressNodes = new ArrayList<>();
    appendProgressNode(
        "INIT_" + status,
        stageNameByStatus(status),
        status,
        statusText,
        "system",
        latestRemark,
        createdAt == null ? null : createdAt.toString());
  }

  public String getDisputeId() {
    return disputeId;
  }

  public String getUserId() {
    return userId;
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

  public String getBuyerCompany() {
    return buyerCompany;
  }

  public String getSupplierName() {
    return supplierName;
  }

  public String getIssueType() {
    return issueType;
  }

  public String getIssueTypeText() {
    return issueTypeText;
  }

  public String getIssueSummary() {
    return issueSummary;
  }

  public String getIssueDescription() {
    return issueDescription;
  }

  public String getExpectedResolution() {
    return expectedResolution;
  }

  public String getContactName() {
    return contactName;
  }

  public String getContactPhoneMasked() {
    return contactPhoneMasked;
  }

  public String getEvidenceFiles() {
    return evidenceFiles;
  }

  public String getStatus() {
    return status;
  }

  public String getStatusText() {
    return statusText;
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

  public void updateStatus(
      String status, String statusText, String remark, String operator, LocalDateTime now) {
    this.status = status;
    this.statusText = statusText;
    this.latestRemark = remark;
    this.updatedAt = now;
    appendProgressNode(
        "STATUS_" + status,
        stageNameByStatus(status),
        status,
        statusText,
        operator == null || operator.isBlank() ? "system" : operator,
        remark,
        now == null ? null : now.toString());
  }

  public void appendProgressNode(
      String nodeCode,
      String nodeName,
      String status,
      String statusText,
      String handler,
      String remark,
      String happenedAt) {
    progressNodes.add(
        new ProgressNode(nodeCode, nodeName, status, statusText, handler, remark, happenedAt));
  }

  private String stageNameByStatus(String status) {
    if ("SUBMITTED".equalsIgnoreCase(status)) {
      return "争议已提交";
    }
    if ("PROCESSING".equalsIgnoreCase(status)) {
      return "平台处理中";
    }
    if ("RESOLVED".equalsIgnoreCase(status)) {
      return "争议已解决";
    }
    if ("CLOSED".equalsIgnoreCase(status)) {
      return "争议已关闭";
    }
    return "处理中";
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
