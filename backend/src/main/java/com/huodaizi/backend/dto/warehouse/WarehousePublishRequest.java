package com.huodaizi.backend.dto.warehouse;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record WarehousePublishRequest(
    @NotBlank(message = "name 不能为空")
    @Size(max = 120, message = "name 最大长度120")
    String name,
    @NotBlank(message = "city 不能为空")
    @Size(max = 32, message = "city 最大长度32")
    String city,
    @NotBlank(message = "type 不能为空")
    @Size(max = 32, message = "type 最大长度32")
    String type,
    @NotBlank(message = "capacity 不能为空")
    @Size(max = 32, message = "capacity 最大长度32")
    String capacity,
    @NotBlank(message = "throughput 不能为空")
    @Size(max = 64, message = "throughput 最大长度64")
    String throughput,
    @NotBlank(message = "capability 不能为空")
    @Size(max = 64, message = "capability 最大长度64")
    String capability,
    @Size(max = 128, message = "categoriesText 最大长度128")
    String categoriesText,
    @NotBlank(message = "price 不能为空")
    @Size(max = 64, message = "price 最大长度64")
    String price,
    @Size(max = 128, message = "address 最大长度128")
    String address,
    @Size(max = 64, message = "contactName 最大长度64")
    String contactName,
    @Size(max = 64, message = "contactPhone 最大长度64")
    String contactPhone) {}
