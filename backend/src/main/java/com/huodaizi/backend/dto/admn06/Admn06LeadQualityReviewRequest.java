package com.huodaizi.backend.dto.admn06;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record Admn06LeadQualityReviewRequest(
    @NotBlank(message = "qualityStatus 不能为空")
    @Size(max = 16, message = "qualityStatus 最大长度16")
    String qualityStatus,
    @Size(max = 16, message = "riskLevel 最大长度16")
    String riskLevel,
    @Min(value = 0, message = "qualityScore 最小为0")
    @Max(value = 100, message = "qualityScore 最大为100")
    Integer qualityScore,
    @Size(max = 64, message = "ruleCode 最大长度64")
    String ruleCode,
    @Size(max = 300, message = "reviewRemark 最大长度300")
    String reviewRemark,
    @Size(max = 64, message = "reviewer 最大长度64")
    String reviewer) {}
