package com.huodaizi.backend.dto.inquiry;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record InquiryH5InquiryStep2InitRequest(
    @NotBlank(message = "draftId 不能为空")
    @Size(max = 64, message = "draftId 最大长度64")
    String draftId) {}
