package com.huodaizi.backend.dto.storagedemand;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record StorageDemandPublishRequest(
    @NotBlank(message = "title 不能为空")
    @Size(min = 8, max = 80, message = "title 长度需在8-80之间")
    String title,
    @NotBlank(message = "city 不能为空")
    @Size(max = 32, message = "city 最大长度32")
    String city,
    @NotBlank(message = "goodsCategory 不能为空")
    @Size(max = 32, message = "goodsCategory 最大长度32")
    String goodsCategory,
    @NotBlank(message = "tonnage 不能为空")
    @Pattern(regexp = "^\\d+(\\.\\d{1,2})?$", message = "tonnage 必须为数字")
    String tonnage,
    @NotBlank(message = "storageDays 不能为空")
    @Pattern(regexp = "^\\d+$", message = "storageDays 必须为正整数")
    String storageDays,
    @NotBlank(message = "inboundDate 不能为空")
    @Size(max = 16, message = "inboundDate 最大长度16")
    String inboundDate,
    Boolean needLoading,
    Boolean needSorting,
    @NotBlank(message = "contactName 不能为空")
    @Size(min = 2, max = 32, message = "contactName 长度需在2-32之间")
    String contactName,
    @NotBlank(message = "contactPhone 不能为空")
    @Pattern(regexp = "^1\\d{10}$", message = "contactPhone 必须为11位手机号")
    String contactPhone,
    @NotBlank(message = "companyName 不能为空")
    @Size(min = 4, max = 120, message = "companyName 长度需在4-120之间")
    String companyName,
    @Size(max = 1000, message = "remark 最大长度1000")
    String remark,
    Boolean agreed) {}
