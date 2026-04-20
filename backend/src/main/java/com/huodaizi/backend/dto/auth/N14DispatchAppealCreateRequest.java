package com.huodaizi.backend.dto.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record N14DispatchAppealCreateRequest(
    @NotBlank(message = "sceneCode 不能为空")
    @Size(max = 32, message = "sceneCode 最大长度32")
    String sceneCode,
    @NotBlank(message = "targetId 不能为空")
    @Size(max = 48, message = "targetId 最大长度48")
    String targetId,
    @NotBlank(message = "appealType 不能为空")
    @Size(max = 32, message = "appealType 最大长度32")
    String appealType,
    @NotBlank(message = "appealReason 不能为空")
    @Size(max = 500, message = "appealReason 最大长度500")
    String appealReason,
    @Size(max = 500, message = "evidenceFiles 最大长度500")
    String evidenceFiles,
    @Size(max = 32, message = "operator 最大长度32")
    String operator) {}
