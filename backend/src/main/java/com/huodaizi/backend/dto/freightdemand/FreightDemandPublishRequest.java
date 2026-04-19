package com.huodaizi.backend.dto.freightdemand;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record FreightDemandPublishRequest(
    @NotBlank(message = "title 不能为空")
    @Size(max = 120, message = "title 最大长度120")
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
    Boolean needInvoice,
    Boolean needLoading,
    @NotBlank(message = "contactName 不能为空")
    @Size(max = 32, message = "contactName 最大长度32")
    String contactName,
    @NotBlank(message = "contactPhone 不能为空")
    @Size(max = 32, message = "contactPhone 最大长度32")
    String contactPhone,
    @NotBlank(message = "companyName 不能为空")
    @Size(max = 120, message = "companyName 最大长度120")
    String companyName,
    @Size(max = 500, message = "remark 最大长度500")
    String remark,
    Boolean agreed) {}
