package com.huodaizi.backend.dto.inquiry;

import java.util.List;

public record InquiryMessageCenterItemDTO(
    String messageId,
    String merchantId,
    String title,
    String content,
    String messageType,
    String messageTypeText,
    String bizType,
    String bizId,
    String status,
    String statusText,
    boolean unread,
    boolean pinned,
    int priority,
    List<String> tags,
    String actionText,
    String actionUrl,
    String createdAt,
    String readAt) {}
