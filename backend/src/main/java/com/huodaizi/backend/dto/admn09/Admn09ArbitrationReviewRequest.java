package com.huodaizi.backend.dto.admn09;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record Admn09ArbitrationReviewRequest(
    @NotBlank(message = "action 不能为空")
    @Size(max = 32, message = "action 最大长度32")
    String action,
    @Size(max = 200, message = "resolutionSummary 最大长度200")
    String resolutionSummary,
    @Size(max = 300, message = "resolutionDetail 最大长度300")
    String resolutionDetail,
    @Size(max = 64, message = "operator 最大长度64")
    String operator) {}
