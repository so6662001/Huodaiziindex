package com.huodaizi.backend.dto.marketdetail;

import java.util.List;

public record MarketDetailResponse(
    String symbol,
    String city,
    String title,
    List<MarketDetailSummaryDTO> quoteSummary,
    List<String> relatedNews,
    List<String> relatedSupply,
    List<MarketDetailItemDTO> actions,
    List<MarketDetailItemDTO> ads) {}
