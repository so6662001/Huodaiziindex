package com.huodaizi.backend.dto;

import java.util.List;

public record HomeOverviewResponse(
    HomeConfigDTO homeConfig,
    List<MarketQuoteDTO> marketQuotes,
    List<SupplyDemandItemDTO> latestSupplies,
    List<SupplyDemandItemDTO> latestDemands,
    List<LogisticsWarehouseDTO> warehouses,
    List<LogisticsFreightDTO> freights,
    List<StationDTO> hotStations,
    List<NewsItemDTO> latestNews,
    List<AdSlotDTO> adSlots
) {}
