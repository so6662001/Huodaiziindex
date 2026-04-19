package com.huodaizi.backend.dto.logistics;

import java.util.List;

public record LogisticsPageResponse(
    List<LogisticsQuickEntryDTO> quickEntries,
    List<LogisticsWarehouseCardDTO> warehouses,
    List<LogisticsFreightCardDTO> freights,
    List<LogisticsDemandCardDTO> storageDemands,
    List<LogisticsDemandCardDTO> transportDemands,
    List<String> stations,
    LogisticsWarehouseCardDTO adCard) {}
