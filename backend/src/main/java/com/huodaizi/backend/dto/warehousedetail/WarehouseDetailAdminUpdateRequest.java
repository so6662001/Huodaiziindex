package com.huodaizi.backend.dto.warehousedetail;

import jakarta.validation.constraints.Size;

public record WarehouseDetailAdminUpdateRequest(
    @Size(max = 120, message = "title 最大长度120") String title,
    @Size(max = 120, message = "city 最大长度120") String city,
    @Size(max = 80, message = "warehouseType 最大长度80") String warehouseType,
    @Size(max = 80, message = "capacity 最大长度80") String capacity,
    @Size(max = 80, message = "throughput 最大长度80") String throughput,
    @Size(max = 120, message = "capability 最大长度120") String capability,
    @Size(max = 80, message = "quote 最大长度80") String quote,
    @Size(max = 500, message = "address 最大长度500") String address,
    @Size(max = 80, message = "workTime 最大长度80") String workTime,
    @Size(max = 1000, message = "serviceTags 最大长度1000") String serviceTags,
    @Size(max = 1000, message = "description 最大长度1000") String description,
    @Size(max = 120, message = "relatedId 最大长度120") String relatedId,
    @Size(max = 120, message = "contactName 最大长度120") String contactName,
    @Size(max = 32, message = "contactPhone 最大长度32") String contactPhone,
    @Size(max = 120, message = "serviceStatus 最大长度120") String serviceStatus,
    @Size(max = 500, message = "link 最大长度500") String link,
    @Size(max = 32, message = "status 最大长度32") String status,
    Boolean pinned) {}
