package com.huodaizi.backend.dto.storagedemand;

import jakarta.validation.constraints.Size;

public record StorageDemandAdminUpdateRequest(
    @Size(min = 8, max = 120, message = "title 长度需在8-120") String title,
    @Size(max = 32, message = "city 最大长度32") String city,
    @Size(max = 32, message = "goodsCategory 最大长度32") String goodsCategory,
    @Size(max = 32, message = "tonnage 最大长度32") String tonnage,
    @Size(max = 32, message = "storageDays 最大长度32") String storageDays,
    @Size(max = 32, message = "inboundDate 最大长度32") String inboundDate,
    Boolean needLoading,
    Boolean needSorting,
    @Size(max = 64, message = "companyName 最大长度64") String companyName,
    @Size(max = 64, message = "contactName 最大长度64") String contactName,
    @Size(max = 64, message = "contactPhone 最大长度64") String contactPhone,
    @Size(max = 500, message = "remark 最大长度500") String remark,
    @Size(max = 32, message = "status 最大长度32") String status,
    Boolean pinned) {}
