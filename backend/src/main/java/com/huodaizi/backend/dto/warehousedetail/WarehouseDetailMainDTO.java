package com.huodaizi.backend.dto.warehousedetail;

import java.util.List;

public record WarehouseDetailMainDTO(
    String id,
    String name,
    String city,
    String type,
    String capacity,
    String throughput,
    String capability,
    String quote,
    String address,
    String workTime,
    List<String> serviceTags,
    String desc,
    String status,
    boolean pinned,
    String updatedAt) {}
