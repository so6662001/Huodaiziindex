package com.huodaizi.backend.dto.admn09;

public record Admn09ArbitrationTicketProgressNodeDTO(
    String nodeCode,
    String nodeName,
    String status,
    String statusText,
    String handler,
    String remark,
    String happenedAt) {}
