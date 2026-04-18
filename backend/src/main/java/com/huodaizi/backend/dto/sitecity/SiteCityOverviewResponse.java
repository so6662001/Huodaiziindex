package com.huodaizi.backend.dto.sitecity;

import java.util.List;

public record SiteCityOverviewResponse(
    String citySlug,
    String cityName,
    String cityCode,
    List<SiteCityMarketItemDTO> marketItems,
    List<SiteCitySimpleItemDTO> supplyItems,
    List<SiteCitySimpleItemDTO> buyItems,
    List<SiteCitySimpleItemDTO> logisticsItems,
    List<SiteCitySimpleItemDTO> companyItems,
    List<SiteCitySimpleItemDTO> adItems) {}
