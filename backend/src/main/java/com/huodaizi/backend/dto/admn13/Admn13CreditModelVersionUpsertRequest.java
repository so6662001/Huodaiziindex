package com.huodaizi.backend.dto.admn13;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.List;

public record Admn13CreditModelVersionUpsertRequest(
    @NotBlank(message = "modelCode 不能为空")
    @Size(max = 40, message = "modelCode 最大长度40")
    String modelCode,
    @NotBlank(message = "modelName 不能为空")
    @Size(max = 80, message = "modelName 最大长度80")
    String modelName,
    @NotBlank(message = "versionNo 不能为空")
    @Size(max = 24, message = "versionNo 最大长度24")
    String versionNo,
    @NotBlank(message = "versionStatus 不能为空")
    @Size(max = 24, message = "versionStatus 最大长度24")
    String versionStatus,
    @NotBlank(message = "applicableScope 不能为空")
    @Size(max = 24, message = "applicableScope 最大长度24")
    String applicableScope,
    @NotBlank(message = "effectiveFrom 不能为空")
    @Size(max = 20, message = "effectiveFrom 最大长度20")
    String effectiveFrom,
    @Size(max = 20, message = "effectiveTo 最大长度20")
    String effectiveTo,
    @NotBlank(message = "baseScore 不能为空")
    @Size(max = 16, message = "baseScore 最大长度16")
    String baseScore,
    @NotBlank(message = "passThreshold 不能为空")
    @Size(max = 16, message = "passThreshold 最大长度16")
    String passThreshold,
    @NotBlank(message = "riskThreshold 不能为空")
    @Size(max = 16, message = "riskThreshold 最大长度16")
    String riskThreshold,
    List<Admn13CreditModelFactorWeightDTO> factors,
    @Size(max = 300, message = "remark 最大长度300")
    String remark,
    @Size(max = 64, message = "operator 最大长度64")
    String operator) {}
