package com.huodaizi.backend.dto.warehouse;

import jakarta.validation.constraints.Size;

public record WarehouseAdminUpdateRequest(
    @Size(max = 120, message = "name 最大长度120") String name,
    @Size(max = 32, message = "city 最大长度32") String city,
    @Size(max = 32, message = "warehouseType 最大长度32") String warehouseType,
    @Size(max = 32, message = "capacity 最大长度32") String capacity,
    @Size(max = 64, message = "throughput 最大长度64") String throughput,
    @Size(max = 64, message = "capability 最大长度64") String capability,
    @Size(max = 128, message = "categoriesText 最大长度128")
    String categoriesText,
    @Size(max = 64, message = "price 最大长度64") String price,
    @Size(max = 128, message = "address 最大长度128") String address,
    @Size(max = 32, message = "contactName 最大长度32") String contactName,
    @Size(max = 64, message = "contactPhone 最大长度64") String contactPhone,
    @Size(max = 32, message = "status 最大长度32") String status,
    Boolean pinned) {}
