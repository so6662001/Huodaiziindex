package com.huodaizi.backend.repository.auth;

import java.time.LocalDateTime;

public class N05NegotiationMessageEntity {
  private final String messageId;
  private final String senderRole;
  private final String messageType;
  private final String content;
  private final String offerPrice;
  private final String actionLabel;
  private final String unit;
  private final LocalDateTime createdAt;

  public N05NegotiationMessageEntity(
      String messageId,
      String senderRole,
      String messageType,
      String content,
      String offerPrice,
      String actionLabel,
      String unit,
      LocalDateTime createdAt) {
    this.messageId = messageId;
    this.senderRole = senderRole;
    this.messageType = messageType;
    this.content = content;
    this.offerPrice = offerPrice;
    this.actionLabel = actionLabel;
    this.unit = unit;
    this.createdAt = createdAt;
  }

  public String getMessageId() {
    return messageId;
  }

  public String getSenderRole() {
    return senderRole;
  }

  public String getMessageType() {
    return messageType;
  }

  public String getContent() {
    return content;
  }

  public String getOfferPrice() {
    return offerPrice;
  }

  public String getActionLabel() {
    return actionLabel;
  }

  public String getUnit() {
    return unit;
  }

  public LocalDateTime getCreatedAt() {
    return createdAt;
  }
}
