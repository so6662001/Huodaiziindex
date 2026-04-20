package com.huodaizi.backend.repository.auth;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class N05NegotiationSessionEntity {
  private final String sessionId;
  private final String sessionNo;
  private final String userId;
  private final String account;
  private final String inquiryNo;
  private final String inquiryTitle;
  private final String productName;
  private final String specification;
  private final String quantityText;
  private final String city;
  private final String counterpartyName;
  private final String roleInSession;
  private final String buyerName;
  private final String supplierName;
  private final String targetPrice;
  private final String currency;
  private final boolean canBuyerSpeak;
  private final boolean canSupplierSpeak;
  private int unreadCount;
  private String status;
  private String roundNo;
  private String latestOfferPrice;
  private String latestMessage;
  private LocalDateTime latestMessageAt;
  private final LocalDateTime createdAt;
  private LocalDateTime updatedAt;
  private final List<N05NegotiationMessageEntity> messages;

  public N05NegotiationSessionEntity(
      String sessionId,
      String sessionNo,
      String userId,
      String account,
      String inquiryNo,
      String inquiryTitle,
      String productName,
      String specification,
      String quantityText,
      String city,
      String counterpartyName,
      String roleInSession,
      String buyerName,
      String supplierName,
      String status,
      String roundNo,
      String latestOfferPrice,
      String targetPrice,
      String currency,
      boolean canBuyerSpeak,
      boolean canSupplierSpeak,
      int unreadCount,
      String latestMessage,
      LocalDateTime latestMessageAt,
      LocalDateTime createdAt,
      LocalDateTime updatedAt,
      List<N05NegotiationMessageEntity> messages) {
    this.sessionId = sessionId;
    this.sessionNo = sessionNo;
    this.userId = userId;
    this.account = account;
    this.inquiryNo = inquiryNo;
    this.inquiryTitle = inquiryTitle;
    this.productName = productName;
    this.specification = specification;
    this.quantityText = quantityText;
    this.city = city;
    this.counterpartyName = counterpartyName;
    this.roleInSession = roleInSession;
    this.buyerName = buyerName;
    this.supplierName = supplierName;
    this.status = status;
    this.roundNo = roundNo;
    this.latestOfferPrice = latestOfferPrice;
    this.targetPrice = targetPrice;
    this.currency = currency;
    this.canBuyerSpeak = canBuyerSpeak;
    this.canSupplierSpeak = canSupplierSpeak;
    this.unreadCount = Math.max(0, unreadCount);
    this.latestMessage = latestMessage;
    this.latestMessageAt = latestMessageAt;
    this.createdAt = createdAt;
    this.updatedAt = updatedAt;
    this.messages = messages == null ? new ArrayList<>() : new ArrayList<>(messages);
  }

  public String getSessionId() {
    return sessionId;
  }

  public String getSessionNo() {
    return sessionNo;
  }

  public String getUserId() {
    return userId;
  }

  public String getAccount() {
    return account;
  }

  public String getInquiryNo() {
    return inquiryNo;
  }

  public String getInquiryTitle() {
    return inquiryTitle;
  }

  public String getProductName() {
    return productName;
  }

  public String getSpecification() {
    return specification;
  }

  public String getQuantityText() {
    return quantityText;
  }

  public String getCity() {
    return city;
  }

  public String getCounterpartyName() {
    return counterpartyName;
  }

  public String getRoleInSession() {
    return roleInSession;
  }

  public String getBuyerName() {
    return buyerName;
  }

  public String getSupplierName() {
    return supplierName;
  }

  public String getStatus() {
    return status;
  }

  public String getRoundNo() {
    return roundNo;
  }

  public String getLatestOfferPrice() {
    return latestOfferPrice;
  }

  public String getTargetPrice() {
    return targetPrice;
  }

  public String getCurrency() {
    return currency;
  }

  public boolean getCanBuyerSpeak() {
    return canBuyerSpeak;
  }

  public boolean getCanSupplierSpeak() {
    return canSupplierSpeak;
  }

  public int getUnreadCount() {
    return unreadCount;
  }

  public String getLatestMessage() {
    return latestMessage;
  }

  public LocalDateTime getLatestMessageAt() {
    return latestMessageAt;
  }

  public LocalDateTime getCreatedAt() {
    return createdAt;
  }

  public LocalDateTime getUpdatedAt() {
    return updatedAt;
  }

  public List<N05NegotiationMessageEntity> getMessages() {
    return List.copyOf(messages);
  }

  public void appendMessage(N05NegotiationMessageEntity message) {
    messages.add(message);
    latestMessage = message.getContent();
    latestMessageAt = message.getCreatedAt();
    if (message.getOfferPrice() != null && !message.getOfferPrice().isBlank()) {
      latestOfferPrice = message.getOfferPrice();
    }
    updatedAt = message.getCreatedAt();
    roundNo = "R" + String.format("%02d", messages.size());
    unreadCount = unreadCount + 1;
  }

  public void markRead() {
    unreadCount = 0;
  }

  public void updateStatus(String status, LocalDateTime now) {
    this.status = status;
    this.updatedAt = now;
  }
}
