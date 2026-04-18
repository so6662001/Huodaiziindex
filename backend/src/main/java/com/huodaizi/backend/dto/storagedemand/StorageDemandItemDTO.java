package com.huodaizi.backend.dto.storagedemand;

public record StorageDemandItemDTO(
    String id,
    String title,
    String city,
    String goodsCategory,
    String tonnage,
    String storageDays,
    String inboundDate,
    String serviceNeed,
    String companyName,
    String contactNameMasked,
    String contactPhoneMasked,
    String remark,
    String status,
    boolean pinned,
    String updatedAt) {}
