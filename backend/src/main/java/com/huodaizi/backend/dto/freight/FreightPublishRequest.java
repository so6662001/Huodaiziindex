package com.huodaizi.backend.dto.freight;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record FreightPublishRequest(
    @NotBlank(message = "provider 不能为空")
    @Size(max = 120, message = "provider 最大长度120")
    String provider,
    @NotBlank(message = "origin 不能为空")
    @Size(max = 32, message = "origin 最大长度32")
    String origin,
    @NotBlank(message = "destination 不能为空")
    @Size(max = 32, message = "destination 最大长度32")
    String destination,
    @NotBlank(message = "vehicleType 不能为空")
    @Size(max = 32, message = "vehicleType 最大长度32")
    String vehicleType,
    @NotBlank(message = "loadRange 不能为空")
    @Size(max = 32, message = "loadRange 最大长度32")
    String loadRange,
    @NotBlank(message = "frequency 不能为空")
    @Size(max = 32, message = "frequency 最大长度32")
    String frequency,
    Integer timelinessHours,
    @NotBlank(message = "price 不能为空")
    @Size(max = 64, message = "price 最大长度64")
    String price,
    Boolean returnTruck,
    @Size(max = 64, message = "contactName 最大长度64")
    String contactName,
    @Size(max = 64, message = "contactPhone 最大长度64")
    String contactPhone) {}
