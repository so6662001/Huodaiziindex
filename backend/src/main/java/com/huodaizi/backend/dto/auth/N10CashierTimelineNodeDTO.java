package com.huodaizi.backend.dto.auth;

public record N10CashierTimelineNodeDTO(
    String nodeCode,
    String nodeName,
    String status,
    String statusText,
    String handler,
    String remark,
    String happenedAt) {}
