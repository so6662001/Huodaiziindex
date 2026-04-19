package com.huodaizi.backend.dto.freightdemand;

public record FreightDemandItemDTO(
    String id,
    String title,
    String originCity,
    String destinationCity,
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
