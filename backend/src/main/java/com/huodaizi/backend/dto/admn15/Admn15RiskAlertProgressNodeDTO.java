package com.huodaizi.backend.dto.admn15;

public record Admn15RiskAlertProgressNodeDTO(
    String nodeCode,
    String nodeName,
    String status,
    String statusText,
    String handler,
    String remark,
    String happenedAt) {}
