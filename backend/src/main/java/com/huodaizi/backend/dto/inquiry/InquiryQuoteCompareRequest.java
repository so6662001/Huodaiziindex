package com.huodaizi.backend.dto.inquiry;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record InquiryQuoteCompareRequest(
    @NotBlank(message = "inquiryId 不能为空")
    @Size(max = 64, message = "inquiryId 最大长度64")
    String inquiryId,
    @Pattern(regexp = "^1\\d{10}$", message = "contactMobile 必须为11位手机号")
    String contactMobile,
    @Size(max = 32, message = "deliveryCycle 最大长度32")
    String deliveryCycle,
    @Size(max = 32, message = "invoiceType 最大长度32")
    String invoiceType,
    @Size(max = 16, message = "sortBy 最大长度16")
    String sortBy,
    @Min(value = 1, message = "page 最小为1")
    Integer page,
    @Min(value = 1, message = "pageSize 最小为1")
    @Max(value = 50, message = "pageSize 最大为50")
    Integer pageSize) {

  public int safePage() {
    return page == null ? 1 : page;
  }

  public int safePageSize() {
    return pageSize == null ? 10 : pageSize;
  }
}
