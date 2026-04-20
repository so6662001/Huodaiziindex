package com.huodaizi.backend.dto.auth;

import java.util.List;

public record H5N04NegotiationDetailResponse(
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
    String roleInSession,
    String channel,
    List<String> availableActions,
    List<H5N04NegotiationMessageDTO> messages) {}
