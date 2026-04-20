package com.huodaizi.backend.dto.auth;

public record N05NegotiationSessionItemDTO(
    String sessionId,
    String inquiryNo,
    String productName,
    String specification,
    String quantityText,
    String city,
    String counterpartyName,
    String roleInSession,
    String status,
    String statusText,
    String latestOfferPrice,
    String latestMessage,
    String latestMessageAt,
    int unreadCount) {}
