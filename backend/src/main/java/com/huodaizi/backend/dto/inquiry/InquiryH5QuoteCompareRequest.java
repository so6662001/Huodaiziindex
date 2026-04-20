package com.huodaizi.backend.dto.inquiry;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record InquiryH5QuoteCompareRequest(
    @NotBlank(message = "draftId 不能为空")
    @Size(max = 64, message = "draftId 最大长度64")
    String draftId,
    @Size(max = 16, message = "sortBy 最大长度16")
    String sortBy,
    @Size(max = 32, message = "deliveryCycle 最大长度32")
    String deliveryCycle,
    @Size(max = 32, message = "invoiceType 最大长度32")
    String invoiceType,
    Integer page,
    Integer pageSize) {

  public int safePage() {
    return page == null || page < 1 ? 1 : page;
  }

  public int safePageSize() {
    if (pageSize == null || pageSize < 1) {
      return 10;
    }
    return Math.min(pageSize, 50);
  }
}
