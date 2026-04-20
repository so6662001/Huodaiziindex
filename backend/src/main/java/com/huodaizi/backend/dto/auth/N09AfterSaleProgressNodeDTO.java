package com.huodaizi.backend.dto.auth;

public record N09AfterSaleProgressNodeDTO(
    String nodeCode,
    String nodeName,
    String status,
    String statusText,
    String handler,
    String remark,
    String happenedAt) {}
