package com.huodaizi.backend.dto.freightdemand;

import java.util.List;

public record FreightDemandFilterOptionsResponse(
    List<String> originCities,
    List<String> destinationCities,
    List<String> goodsCategories,
    List<String> vehicleTypes,
    List<String> timelinessOptions,
    List<String> invoiceNeeds,
    List<String> loadingNeeds) {}
