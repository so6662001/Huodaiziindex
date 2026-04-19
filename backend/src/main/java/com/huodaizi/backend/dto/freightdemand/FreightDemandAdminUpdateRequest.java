package com.huodaizi.backend.dto.freightdemand;

import jakarta.validation.constraints.Size;

public record FreightDemandAdminUpdateRequest(
    @Size(max = 120, message = "title 最大长度120") String title,
    @Size(max = 32, message = "originCity 最大长度32") String originCity,
    @Size(max = 32, message = "destinationCity 最大长度32") String destinationCity,
    @Size(max = 32, message = "goodsCategory 最大长度32") String goodsCategory,
    @Size(max = 32, message = "tonnage 最大长度32") String tonnage,
    @Size(max = 32, message = "vehicleType 最大长度32") String vehicleType,
    @Size(max = 32, message = "timeliness 最大长度32") String timeliness,
    @Size(max = 32, message = "loadDate 最大长度32") String loadDate,
    Boolean needInvoice,
    Boolean needLoading,
    @Size(max = 120, message = "contactName 最大长度120") String contactName,
    @Size(max = 32, message = "contactPhone 最大长度32") String contactPhone,
    @Size(max = 200, message = "companyName 最大长度200") String companyName,
    @Size(max = 1000, message = "remark 最大长度1000") String remark,
    @Size(max = 32, message = "status 最大长度32") String status,
    Boolean pinned) {}
