package com.huodaizi.backend.dto.transportdemand;

import jakarta.validation.constraints.Size;

public record TransportDemandAdminUpdateRequest(
    @Size(min = 8, max = 120, message = "title 长度需在8-120") String title,
    @Size(max = 32, message = "originCity 最大长度32") String originCity,
    @Size(max = 32, message = "destinationCity 最大长度32") String destinationCity,
    @Size(max = 32, message = "goodsCategory 最大长度32") String goodsCategory,
    @Size(max = 32, message = "tonnage 最大长度32") String tonnage,
    @Size(max = 32, message = "vehicleType 最大长度32") String vehicleType,
    @Size(max = 32, message = "timeliness 最大长度32") String timeliness,
    @Size(max = 32, message = "loadDate 最大长度32") String loadDate,
    Boolean needInvoice,
    Boolean needLoading,
    @Size(max = 64, message = "companyName 最大长度64") String companyName,
    @Size(max = 64, message = "contactName 最大长度64") String contactName,
    @Size(max = 64, message = "contactPhone 最大长度64") String contactPhone,
    @Size(max = 500, message = "remark 最大长度500") String remark,
    @Size(max = 32, message = "status 最大长度32") String status,
    Boolean pinned) {}
