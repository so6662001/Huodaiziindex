package com.huodaizi.backend.dto.auth;

import java.util.List;

public record H5N04NegotiationSessionListResponse(
    int pageNo,
    int pageSize,
    long total,
    String channel,
    String activeSessionId,
    List<H5N04NegotiationSessionItemDTO> records) {}
