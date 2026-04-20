package com.huodaizi.backend.dto.admn05;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record Admn05CategorySpecUpsertRequest(
    @NotBlank(message = "categoryCode 不能为空")
    @Size(max = 32, message = "categoryCode 最大长度32")
    String categoryCode,
    @NotBlank(message = "categoryName 不能为空")
    @Size(max = 64, message = "categoryName 最大长度64")
    String categoryName,
    @NotBlank(message = "specName 不能为空")
    @Size(max = 64, message = "specName 最大长度64")
    String specName,
    @NotBlank(message = "specValue 不能为空")
    @Size(max = 64, message = "specValue 最大长度64")
    String specValue,
    @Size(max = 16, message = "sceneCode 最大长度16")
    String sceneCode,
    @Size(max = 16, message = "status 最大长度16")
    String status,
    @Min(value = 0, message = "sortNo 最小为0")
    Integer sortNo,
    @Size(max = 300, message = "remark 最大长度300")
    String remark,
    @Size(max = 64, message = "operator 最大长度64")
    String operator) {}
