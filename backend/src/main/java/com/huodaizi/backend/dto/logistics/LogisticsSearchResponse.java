package com.huodaizi.backend.dto.logistics;

import java.util.List;

public record LogisticsSearchResponse(
    List<LogisticsSearchResultDTO> items,
    Integer total) {}
