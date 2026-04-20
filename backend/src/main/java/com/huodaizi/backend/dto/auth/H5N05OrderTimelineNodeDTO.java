package com.huodaizi.backend.dto.auth;

public record H5N05OrderTimelineNodeDTO(
    String nodeCode,
    String nodeName,
    String status,
    String statusText,
    String owner,
    String happenedAt,
    String remark) {}
