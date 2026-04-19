package com.huodaizi.backend.dto.market;

import java.util.List;

public record MarketOverviewResponse(
    String category,
    String city,
    String range,
    List<MarketCategoryQuoteDTO> categoryQuotes,
    List<MarketQuotePanelDTO> quotePanels,
    List<MarketSnapshotItemDTO> snapshots,
    List<MarketSignalItemDTO> signals,
    List<String> insights) {}
