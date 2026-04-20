package com.huodaizi.backend.dto.inquiry;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record InquiryH5InquiryStep2SubmitRequest(
    @NotBlank(message = "draftId 不能为空")
    @Size(max = 64, message = "draftId 最大长度64")
    String draftId,
    @Size(max = 64, message = "expectedDeliveryAt 最大长度64")
    String expectedDeliveryAt,
    @Size(max = 32, message = "deliveryTimeRange 最大长度32")
    String deliveryTimeRange,
    @Size(max = 16, message = "unloadSupport 最大长度16")
    String unloadSupport,
    @NotNull(message = "needInvoice 不能为空")
    @Pattern(regexp = "^(Y|N)$", message = "needInvoice 必须为Y或N")
    String needInvoice,
    @Size(max = 500, message = "step2Remark 最大长度500")
    String step2Remark) {}
