package com.huodaizi.backend.dto.transportdemand;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record TransportDemandPublishRequest(
    @NotBlank(message = "title 不能为空")
    @Size(min = 8, max = 120, message = "title 长度需在8-120")
    String title,
    @NotBlank(message = "originCity 不能为空")
    @Size(max = 32, message = "originCity 最大长度32")
    String originCity,
    @NotBlank(message = "destinationCity 不能为空")
    @Size(max = 32, message = "destinationCity 最大长度32")
    String destinationCity,
    @NotBlank(message = "goodsCategory 不能为空")
    @Size(max = 32, message = "goodsCategory 最大长度32")
    String goodsCategory,
    @NotBlank(message = "tonnage 不能为空")
    @Size(max = 32, message = "tonnage 最大长度32")
    String tonnage,
    @NotBlank(message = "vehicleType 不能为空")
    @Size(max = 32, message = "vehicleType 最大长度32")
    String vehicleType,
    @NotBlank(message = "timeliness 不能为空")
    @Size(max = 32, message = "timeliness 最大长度32")
    String timeliness,
    @NotBlank(message = "loadDate 不能为空")
    @Size(max = 32, message = "loadDate 最大长度32")
    String loadDate,
    @NotNull(message = "needInvoice 不能为空")
    Boolean needInvoice,
    @NotNull(message = "needLoading 不能为空")
    Boolean needLoading,
    @Size(max = 64, message = "companyName 最大长度64")
    String companyName,
    @Size(max = 64, message = "contactName 最大长度64")
    String contactName,
    @Size(max = 64, message = "contactPhone 最大长度64")
    String contactPhone,
    @Size(max = 500, message = "remark 最大长度500")
    String remark) {}
