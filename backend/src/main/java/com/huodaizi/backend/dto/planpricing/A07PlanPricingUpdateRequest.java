package com.huodaizi.backend.dto.planpricing;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;

public record A07PlanPricingUpdateRequest(
    @NotBlank(message = "planCode 不能为空")
    @Size(max = 32, message = "planCode 最大长度32")
    String planCode,
    @NotBlank(message = "planName 不能为空")
    @Size(max = 64, message = "planName 最大长度64")
    String planName,
    @NotBlank(message = "planType 不能为空")
    @Size(max = 32, message = "planType 最大长度32")
    String planType,
    @NotBlank(message = "billingCycle 不能为空")
    @Size(max = 16, message = "billingCycle 最大长度16")
    String billingCycle,
    @NotBlank(message = "price 不能为空")
    @Size(max = 32, message = "price 最大长度32")
    String price,
    @NotBlank(message = "originalPrice 不能为空")
    @Size(max = 32, message = "originalPrice 最大长度32")
    String originalPrice,
    @NotBlank(message = "suitableFor 不能为空")
    @Size(max = 120, message = "suitableFor 最大长度120")
    String suitableFor,
    @NotNull(message = "recommended 不能为空")
    Boolean recommended,
    @NotNull(message = "enabled 不能为空")
    Boolean enabled,
    @NotBlank(message = "operator 不能为空")
    @Size(max = 32, message = "operator 最大长度32")
    String operator,
    @Size(max = 300, message = "remark 最大长度300")
    String remark,
    @NotEmpty(message = "features 不能为空")
    List<A07PlanPricingFeatureRequest> features) {

  public record A07PlanPricingFeatureRequest(
      @NotBlank(message = "code 不能为空")
      @Size(max = 32, message = "code 最大长度32")
      String code,
      @NotBlank(message = "name 不能为空")
      @Size(max = 64, message = "name 最大长度64")
      String name,
      @NotBlank(message = "value 不能为空")
      @Size(max = 200, message = "value 最大长度200")
      String value,
      @Size(max = 32, message = "highlight 最大长度32")
      String highlight,
      @Min(value = 1, message = "sort 最小为1")
      @Max(value = 99, message = "sort 最大为99")
      Integer sort) {}
}
