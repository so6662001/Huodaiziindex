package com.huodaizi.backend.repository.inquiry;

import java.time.LocalDateTime;

public class InquiryMessageCenterEntity {
  private final String messageId;
  private final String merchantId;
  private final String title;
  private final String content;
  private final String bizType;
  private final String bizId;
  private final String priority;
  private final String actionUrl;
  private final LocalDateTime sendAt;
  private String readStatus;
  private LocalDateTime readAt;
  private LocalDateTime updatedAt;

  public InquiryMessageCenterEntity(
      String messageId,
      String merchantId,
      String title,
      String content,
      String bizType,
      String bizId,
      String priority,
      String actionUrl,
      LocalDateTime sendAt,
      String readStatus,
      LocalDateTime readAt,
      LocalDateTime updatedAt) {
    this.messageId = messageId;
    this.merchantId = merchantId;
    this.title = title;
    this.content = content;
    this.bizType = bizType;
    this.bizId = bizId;
    this.priority = priority;
    this.actionUrl = actionUrl;
    this.sendAt = sendAt;
    this.readStatus = readStatus;
    this.readAt = readAt;
    this.updatedAt = updatedAt;
  }

  public String getMessageId() {
    return messageId;
  }

  public String getMerchantId() {
    return merchantId;
  }

  public String getTitle() {
    return title;
  }

  public String getContent() {
    return content;
  }

  public String getBizType() {
    return bizType;
  }

  public String getBizId() {
    return bizId;
  }

  public String getPriority() {
    return priority;
  }

  public String getActionUrl() {
    return actionUrl;
  }

  public LocalDateTime getSendAt() {
    return sendAt;
  }

  public String getReadStatus() {
    return readStatus;
  }

  public LocalDateTime getReadAt() {
    return readAt;
  }

  public LocalDateTime getUpdatedAt() {
    return updatedAt;
  }

  public void markRead(LocalDateTime now) {
    this.readStatus = "READ";
    this.readAt = now;
    this.updatedAt = now;
  }
}
