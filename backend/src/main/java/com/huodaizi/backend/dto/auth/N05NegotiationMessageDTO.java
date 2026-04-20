package com.huodaizi.backend.dto.auth;

public record N05NegotiationMessageDTO(
    String messageId,
    String senderRole,
    String senderName,
    String contentType,
    String content,
    String quotePrice,
    String quoteQuantity,
    String sentAt) {}
