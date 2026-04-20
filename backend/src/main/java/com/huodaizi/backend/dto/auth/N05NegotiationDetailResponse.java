package com.huodaizi.backend.dto.auth;

import java.util.List;

public record N05NegotiationDetailResponse(
    String sessionId,
    String inquiryNo,
    String goodsName,
    String specText,
    String buyerName,
    String sellerName,
    String status,
    String statusText,
    String latestQuotedPrice,
    String targetPrice,
    String currentRound,
    String unreadCount,
    String lastMessageAt,
    String updatedAt,
    List<N05NegotiationMessageDTO> messages) {}
