package com.huodaizi.backend.dto.auth;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.util.List;

public record H5N11MessageSettingsUpdateRequest(
    Boolean systemNoticeEnabled,
    Boolean orderNoticeEnabled,
    Boolean financeNoticeEnabled,
    Boolean marketingNoticeEnabled,
    Boolean pushEnabled,
    Boolean smsEnabled,
    Boolean emailEnabled,
    Boolean doNotDisturbEnabled,
    @Pattern(regexp = "^([01]\\d|2[0-3]):[0-5]\\d$", message = "quietStart 必须为HH:mm格式")
    String quietStart,
    @Pattern(regexp = "^([01]\\d|2[0-3]):[0-5]\\d$", message = "quietEnd 必须为HH:mm格式")
    String quietEnd,
    List<@Size(max = 24, message = "extraMutedScene 项最大长度24") String> extraMutedScenes,
    @Size(max = 32, message = "operator 最大长度32")
    String operator) {}
