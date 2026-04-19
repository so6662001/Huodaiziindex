package com.huodaizi.backend.dto.warehousedetail;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record WarehouseDetailRequest(
    @NotBlank(message = "id 不能为空") @Size(max = 32, message = "id 最大长度32") String id) {}
