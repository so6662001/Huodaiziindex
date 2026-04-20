package com.huodaizi.backend.dto.admn12;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record Admn12AdSlotScheduleUpsertRequest(
    @NotBlank(message = "slotCode 不能为空")
    @Size(max = 40, message = "slotCode 最大长度40")
    String slotCode,
    @NotBlank(message = "slotName 不能为空")
    @Size(max = 80, message = "slotName 最大长度80")
    String slotName,
    @NotBlank(message = "slotType 不能为空")
    @Size(max = 24, message = "slotType 最大长度24")
    String slotType,
    @NotBlank(message = "cityCode 不能为空")
    @Size(max = 24, message = "cityCode 最大长度24")
    String cityCode,
    @Size(max = 24, message = "cityName 最大长度24")
    String cityName,
    @NotBlank(message = "scheduleStatus 不能为空")
    @Size(max = 24, message = "scheduleStatus 最大长度24")
    String scheduleStatus,
    @Size(max = 24, message = "scheduleFillStatus 最大长度24")
    String scheduleFillStatus,
    @NotBlank(message = "startDate 不能为空")
    @Size(max = 20, message = "startDate 最大长度20")
    String startDate,
    @NotBlank(message = "endDate 不能为空")
    @Size(max = 20, message = "endDate 最大长度20")
    String endDate,
    @NotBlank(message = "totalSlots 不能为空")
    @Size(max = 16, message = "totalSlots 最大长度16")
    String totalSlots,
    @NotBlank(message = "soldSlots 不能为空")
    @Size(max = 16, message = "soldSlots 最大长度16")
    String soldSlots,
    @NotBlank(message = "pricePerDay 不能为空")
    @Size(max = 32, message = "pricePerDay 最大长度32")
    String pricePerDay,
    @Size(max = 200, message = "creativeUrl 最大长度200")
    String creativeUrl,
    @Size(max = 80, message = "advertiserName 最大长度80")
    String advertiserName,
    @Size(max = 120, message = "campaignName 最大长度120")
    String campaignName,
    @Size(max = 300, message = "remark 最大长度300")
    String remark,
    @Size(max = 64, message = "operator 最大长度64")
    String operator) {}
