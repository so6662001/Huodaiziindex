package com.huodaizi.backend.dto.admn10;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;

public record Admn10BillingRuleUpsertRequest(
    @NotBlank(message = "ruleCode 不能为空")
    @Size(max = 32, message = "ruleCode 最大长度32")
    String ruleCode,
    @NotBlank(message = "ruleName 不能为空")
    @Size(max = 80, message = "ruleName 最大长度80")
    String ruleName,
    @NotBlank(message = "sceneCode 不能为空")
    @Size(max = 24, message = "sceneCode 最大长度24")
    String sceneCode,
    @NotBlank(message = "billingMode 不能为空")
    @Size(max = 16, message = "billingMode 最大长度16")
    String billingMode,
    @NotBlank(message = "feeCurrency 不能为空")
    @Size(max = 8, message = "feeCurrency 最大长度8")
    String feeCurrency,
    @NotBlank(message = "basePriceYuan 不能为空")
    @DecimalMin(value = "0.00", message = "basePriceYuan 最小为0")
    @Size(max = 32, message = "basePriceYuan 最大长度32")
    String basePriceYuan,
    @NotBlank(message = "minFeeYuan 不能为空")
    @DecimalMin(value = "0.00", message = "minFeeYuan 最小为0")
    @Size(max = 32, message = "minFeeYuan 最大长度32")
    String minFeeYuan,
    @NotBlank(message = "maxFeeYuan 不能为空")
    @DecimalMin(value = "0.00", message = "maxFeeYuan 最小为0")
    @Size(max = 32, message = "maxFeeYuan 最大长度32")
    String maxFeeYuan,
    @Size(max = 300, message = "ladderConfig 最大长度300")
    String ladderConfig,
    @NotBlank(message = "effectiveFrom 不能为空")
    @Size(max = 16, message = "effectiveFrom 最大长度16")
    String effectiveFrom,
    @Size(max = 16, message = "effectiveTo 最大长度16")
    String effectiveTo,
    @NotBlank(message = "ruleStatus 不能为空")
    @Size(max = 16, message = "ruleStatus 最大长度16")
    String ruleStatus,
    @Size(max = 300, message = "remark 最大长度300")
    String remark,
    @Size(max = 64, message = "operator 最大长度64")
    String operator) {}
