package com.huodaizi.backend.dto.quoteefficiency;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record A03QuoteEfficiencyListRequest(
    @NotBlank(message = "merchantId 不能为空")
    @Size(max = 64, message = "merchantId 最大长度64")
    String merchantId,
    @Size(max = 32, message = "status 最大长度32")
    String status,
    Boolean quoteTimeoutOnly,
    @Size(max = 16, message = "sortBy 最大长度16")
    String sortBy,
    @Size(max = 64, message = "keyword 最大长度64")
    String keyword,
    @Size(max = 64, message = "city 最大长度64")
    String city,
    @Min(value = 1, message = "page 最小为1")
    Integer page,
    @Min(value = 1, message = "pageSize 最小为1")
    @Max(value = 100, message = "pageSize 最大为100")
    Integer pageSize) {

  public int safePage() {
    return page == null ? 1 : page;
  }

  public int safePageSize() {
    return pageSize == null ? 10 : pageSize;
  }

  public boolean safeQuoteTimeoutOnly() {
    return quoteTimeoutOnly != null && quoteTimeoutOnly;
  }
}
