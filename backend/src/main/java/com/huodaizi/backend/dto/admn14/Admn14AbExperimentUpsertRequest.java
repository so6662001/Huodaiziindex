package com.huodaizi.backend.dto.admn14;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.List;

public record Admn14AbExperimentUpsertRequest(
    @NotBlank(message = "experimentCode 不能为空")
    @Size(max = 40, message = "experimentCode 最大长度40")
    String experimentCode,
    @NotBlank(message = "experimentName 不能为空")
    @Size(max = 100, message = "experimentName 最大长度100")
    String experimentName,
    @NotBlank(message = "scenarioCode 不能为空")
    @Size(max = 24, message = "scenarioCode 最大长度24")
    String scenarioCode,
    @NotBlank(message = "experimentStatus 不能为空")
    @Size(max = 24, message = "experimentStatus 最大长度24")
    String experimentStatus,
    @NotBlank(message = "optimizationStage 不能为空")
    @Size(max = 24, message = "optimizationStage 最大长度24")
    String optimizationStage,
    @NotBlank(message = "trafficPercent 不能为空")
    @Size(max = 16, message = "trafficPercent 最大长度16")
    String trafficPercent,
    @NotBlank(message = "targetMetricCode 不能为空")
    @Size(max = 32, message = "targetMetricCode 最大长度32")
    String targetMetricCode,
    @NotBlank(message = "baselineValue 不能为空")
    @Size(max = 24, message = "baselineValue 最大长度24")
    String baselineValue,
    @NotBlank(message = "targetValue 不能为空")
    @Size(max = 24, message = "targetValue 最大长度24")
    String targetValue,
    @NotBlank(message = "startDate 不能为空")
    @Size(max = 20, message = "startDate 最大长度20")
    String startDate,
    @Size(max = 20, message = "endDate 最大长度20")
    String endDate,
    List<Admn14AbExperimentMetricDTO> metrics,
    @Size(max = 64, message = "owner 最大长度64")
    String owner,
    @Size(max = 300, message = "remark 最大长度300")
    String remark,
    @Size(max = 64, message = "operator 最大长度64")
    String operator) {}
