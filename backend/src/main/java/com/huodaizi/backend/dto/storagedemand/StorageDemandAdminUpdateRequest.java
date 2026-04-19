package com.huodaizi.backend.dto.storagedemand;

import jakarta.validation.constraints.Size;

public record StorageDemandAdminUpdateRequest(
    @Size(max = 120, message = "title 最大长度120") String title,
    @Size(max = 32, message = "city 最大长度32") String city,
    @Size(max = 32, message = "goodsCategory 最大长度32") String goodsCategory,
    @Size(max = 32, message = "tonnage 最大长度32") String tonnage,
    @Size(max = 32, message = "storageDays 最大长度32") String storageDays,
    @Size(max = 32, message = "inboundDate 最大长度32") String inboundDate,
    Boolean needLoading,
    Boolean needSorting,
    @Size(max = 80, message = "contactName 最大长度80") String contactName,
    @Size(max = 32, message = "contactPhone 最大长度32") String contactPhone,
    @Size(max = 200, message = "companyName 最大长度200") String companyName,
    @Size(max = 1000, message = "remark 最大长度1000") String remark,
    @Size(max = 32, message = "status 最大长度32") String status,
    Boolean pinned) {}
