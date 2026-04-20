package com.huodaizi.backend.dto.auth;

public record N11PaymentResultNodeDTO(
    String nodeCode,
    String nodeName,
    String status,
    String statusText,
    String happenedAt,
    String remark) {}
