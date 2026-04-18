package com.huodaizi.backend.dto.warehouse;

import java.util.List;

public record WarehouseItemDTO(
    String id,
    String name,
    String city,
    String type,
    String capacity,
    String throughput,
    String capability,
    List<String> categories,
    String price,
    String contactPhone,
    String status,
    boolean pinned,
    String updatedAt) {}
