package com.huodaizi.backend.dto.warehousedetail;

import java.util.List;

public record WarehouseDetailResponse(
    WarehouseDetailMainDTO detail,
    List<WarehouseDetailRelatedWarehouseDTO> relatedWarehouses,
    List<WarehouseDetailRelatedDemandDTO> relatedDemands,
    WarehouseDetailContactDTO contact,
    WarehouseDetailRelatedWarehouseDTO ad) {}
