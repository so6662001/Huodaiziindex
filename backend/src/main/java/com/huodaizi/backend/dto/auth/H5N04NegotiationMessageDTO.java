package com.huodaizi.backend.dto.auth;

public record H5N04NegotiationMessageDTO(
    String messageId,
    String senderRole,
    String senderName,
    String contentType,
    String content,
    String quotePrice,
    String actionLabel,
    String sentAt) {}
