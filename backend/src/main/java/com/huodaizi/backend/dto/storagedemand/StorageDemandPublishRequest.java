package com.huodaizi.backend.dto.storagedemand;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record StorageDemandPublishRequest(
    @NotBlank(message = "title 不能为空")
    @Size(min = 8, max = 120, message = "title 长度需在8-120")
    String title,
    @NotBlank(message = "city 不能为空")
    @Size(max = 32, message = "city 最大长度32")
    String city,
    @NotBlank(message = "goodsCategory 不能为空")
    @Size(max = 32, message = "goodsCategory 最大长度32")
    String goodsCategory,
    @NotBlank(message = "tonnage 不能为空")
    @Size(max = 32, message = "tonnage 最大长度32")
    String tonnage,
    @NotBlank(message = "storageDays 不能为空")
    @Size(max = 32, message = "storageDays 最大长度32")
    String storageDays,
    @NotBlank(message = "inboundDate 不能为空")
    @Size(max = 32, message = "inboundDate 最大长度32")
    String inboundDate,
    @NotNull(message = "needLoading 不能为空")
    Boolean needLoading,
    @NotNull(message = "needSorting 不能为空")
    Boolean needSorting,
    @Size(max = 64, message = "companyName 最大长度64")
    String companyName,
    @Size(max = 64, message = "contactName 最大长度64")
    String contactName,
    @Size(max = 64, message = "contactPhone 最大长度64")
    String contactPhone,
    @Size(max = 500, message = "remark 最大长度500")
    String remark) {}
