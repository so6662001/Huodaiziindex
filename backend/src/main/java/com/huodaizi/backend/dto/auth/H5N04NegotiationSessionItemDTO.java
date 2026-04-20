package com.huodaizi.backend.dto.auth;

public record H5N04NegotiationSessionItemDTO(
    String sessionId,
    String inquiryNo,
    String goodsName,
    String specText,
    String counterpartyName,
    String roleInSession,
    String status,
    String statusText,
    String latestQuotedPrice,
    String lastMessagePreview,
    String lastMessageAt,
    int unreadCount,
    String quickActionText) {}
