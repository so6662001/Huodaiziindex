package com.huodaizi.backend.dto.admn11;

public record Admn11PaymentRefundProgressNodeDTO(
    String nodeCode,
    String nodeName,
    String status,
    String statusText,
    String handler,
    String remark,
    String happenedAt) {}
