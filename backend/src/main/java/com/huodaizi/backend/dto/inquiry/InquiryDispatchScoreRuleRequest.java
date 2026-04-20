package com.huodaizi.backend.dto.inquiry;

import jakarta.validation.constraints.Size;

public record InquiryDispatchScoreRuleRequest(
    @Size(max = 32, message = "sceneCode 最大长度32")
    String sceneCode) {}
