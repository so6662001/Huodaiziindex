package com.huodaizi.backend.dto.dispatchstrategy;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import java.util.List;

public record A06DispatchStrategyUpdateRequest(
    @NotBlank(message = "sceneCode 不能为空")
    @Size(max = 32, message = "sceneCode 最大长度32")
    String sceneCode,
    @NotBlank(message = "ruleVersion 不能为空")
    @Size(max = 32, message = "ruleVersion 最大长度32")
    String ruleVersion,
    @NotBlank(message = "sceneName 不能为空")
    @Size(max = 64, message = "sceneName 最大长度64")
    String sceneName,
    @NotBlank(message = "scoreFormula 不能为空")
    @Size(max = 300, message = "scoreFormula 最大长度300")
    String scoreFormula,
    @NotBlank(message = "updateCycle 不能为空")
    @Size(max = 64, message = "updateCycle 最大长度64")
    String updateCycle,
    @NotBlank(message = "paidFactorDesc 不能为空")
    @Size(max = 200, message = "paidFactorDesc 最大长度200")
    String paidFactorDesc,
    @NotEmpty(message = "dimensions 不能为空")
    List<A06DimensionRequest> dimensions,
    @NotEmpty(message = "bonuses 不能为空")
    List<A06BonusRequest> bonuses,
    @NotEmpty(message = "penalties 不能为空")
    List<A06PenaltyRequest> penalties,
    @NotEmpty(message = "disclosures 不能为空")
    List<@NotBlank(message = "disclosure 不能为空") @Size(max = 200, message = "disclosure 最大长度200") String>
        disclosures,
    @NotBlank(message = "operator 不能为空")
    @Size(max = 32, message = "operator 最大长度32")
    String operator,
    @Size(max = 300, message = "remark 最大长度300")
    String remark) {
  public record A06DimensionRequest(
      @NotBlank(message = "code 不能为空")
      @Size(max = 32, message = "code 最大长度32")
      String code,
      @NotBlank(message = "name 不能为空")
      @Size(max = 64, message = "name 最大长度64")
      String name,
      @Min(value = 0, message = "weight 最小为0")
      @Max(value = 100, message = "weight 最大为100")
      int weight,
      @NotBlank(message = "description 不能为空")
      @Size(max = 300, message = "description 最大长度300")
      String description,
      @NotBlank(message = "scoreMethod 不能为空")
      @Size(max = 200, message = "scoreMethod 最大长度200")
      String scoreMethod,
      @NotBlank(message = "dataSource 不能为空")
      @Size(max = 200, message = "dataSource 最大长度200")
      String dataSource) {}

  public record A06BonusRequest(
      @NotBlank(message = "code 不能为空")
      @Size(max = 32, message = "code 最大长度32")
      String code,
      @NotBlank(message = "name 不能为空")
      @Size(max = 64, message = "name 最大长度64")
      String name,
      @NotBlank(message = "scoreChange 不能为空")
      @Size(max = 16, message = "scoreChange 最大长度16")
      String scoreChange,
      @NotBlank(message = "trigger 不能为空")
      @Size(max = 200, message = "trigger 最大长度200")
      String trigger,
      @NotBlank(message = "cap 不能为空")
      @Size(max = 120, message = "cap 最大长度120")
      String cap) {}

  public record A06PenaltyRequest(
      @NotBlank(message = "code 不能为空")
      @Size(max = 32, message = "code 最大长度32")
      String code,
      @NotBlank(message = "name 不能为空")
      @Size(max = 64, message = "name 最大长度64")
      String name,
      @NotBlank(message = "scoreChange 不能为空")
      @Size(max = 16, message = "scoreChange 最大长度16")
      String scoreChange,
      @NotBlank(message = "trigger 不能为空")
      @Size(max = 200, message = "trigger 最大长度200")
      String trigger,
      @NotBlank(message = "recovery 不能为空")
      @Size(max = 200, message = "recovery 最大长度200")
      String recovery) {}
}
