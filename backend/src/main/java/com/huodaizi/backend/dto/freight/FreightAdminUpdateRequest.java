package com.huodaizi.backend.dto.freight;

import jakarta.validation.constraints.Size;

public record FreightAdminUpdateRequest(
    @Size(max = 120, message = "provider 最大长度120") String provider,
    @Size(max = 32, message = "origin 最大长度32") String origin,
    @Size(max = 32, message = "destination 最大长度32") String destination,
    @Size(max = 32, message = "vehicleType 最大长度32") String vehicleType,
    @Size(max = 32, message = "loadRange 最大长度32") String loadRange,
    @Size(max = 32, message = "frequency 最大长度32") String frequency,
    Integer timelinessHours,
    @Size(max = 64, message = "price 最大长度64") String price,
    Boolean returnTruck,
    @Size(max = 128, message = "contactName 最大长度128") String contactName,
    @Size(max = 64, message = "contactPhone 最大长度64") String contactPhone,
    @Size(max = 32, message = "status 最大长度32") String status,
    Boolean pinned) {}
