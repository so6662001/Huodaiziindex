package com.huodaizi.backend.dto.inquiry;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.List;

public record InquiryQuoteWorkbenchBatchUpdateRequest(
    @NotBlank(message = "merchantId 不能为空")
    @Size(max = 64, message = "merchantId 最大长度64")
    String merchantId,
    @NotEmpty(message = "leadIds 不能为空")
    List<@NotBlank(message = "leadId 不能为空") @Size(max = 64, message = "leadId 最大长度64") String> leadIds,
    @NotBlank(message = "status 不能为空")
    @Size(max = 32, message = "status 最大长度32")
    String status,
    @Size(max = 64, message = "operator 最大长度64")
    String operator,
    @Size(max = 300, message = "comment 最大长度300")
    String comment) {}
