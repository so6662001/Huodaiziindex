package com.huodaizi.backend.dto.warehousedetail;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record WarehouseDetailAdminCreateRequest(
    @NotBlank(message = "title 不能为空")
    @Size(max = 120, message = "title 最大长度120")
    String title,
    @Size(max = 120, message = "city 最大长度120")
    String city,
    @Size(max = 120, message = "warehouseType 最大长度120")
    String warehouseType,
    @Size(max = 120, message = "capacity 最大长度120")
    String capacity,
    @Size(max = 120, message = "throughput 最大长度120")
    String throughput,
    @Size(max = 120, message = "capability 最大长度120")
    String capability,
    @Size(max = 120, message = "quote 最大长度120")
    String quote,
    @Size(max = 300, message = "address 最大长度300")
    String address,
    @Size(max = 120, message = "workTime 最大长度120")
    String workTime,
    @Size(max = 1000, message = "serviceTags 最大长度1000")
    String serviceTags,
    @Size(max = 1000, message = "description 最大长度1000")
    String description,
    @Size(max = 120, message = "relatedId 最大长度120")
    String relatedId,
    @Size(max = 120, message = "contactName 最大长度120")
    String contactName,
    @Size(max = 32, message = "contactPhone 最大长度32")
    String contactPhone,
    @Size(max = 120, message = "serviceStatus 最大长度120")
    String serviceStatus,
    @Size(max = 500, message = "link 最大长度500")
    String link,
    @Size(max = 32, message = "status 最大长度32")
    String status,
    Boolean pinned) {}
