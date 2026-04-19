package com.huodaizi.backend.dto.transportdemand;

public record TransportDemandItemDTO(
    String id,
    String title,
    String originCity,
    String destinationCity,
    String route,
    String goodsCategory,
    String tonnage,
    String vehicleType,
    String timeliness,
    String loadDate,
    String invoiceNeed,
    String loadingNeed,
    String companyName,
    String contactNameMasked,
    String contactPhoneMasked,
    String remark,
    String status,
    boolean pinned,
    String updatedAt) {}
