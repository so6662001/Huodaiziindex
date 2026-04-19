package com.huodaizi.backend.dto.inquiry;

import com.huodaizi.backend.common.BaseException;
import com.huodaizi.backend.common.ErrorCode;
import java.util.Locale;

public enum InquiryQuoteSortBy {
  TOTAL_PRICE,
  UNIT_PRICE,
  DELIVERY_HOURS,
  SUPPLIER_SCORE,
  RESPONSE_MINUTES;

  public static InquiryQuoteSortBy fromOrDefault(String sortBy) {
    if (sortBy == null || sortBy.isBlank()) {
      return TOTAL_PRICE;
    }
    String normalized = sortBy.trim().toUpperCase(Locale.ROOT);
    try {
      return InquiryQuoteSortBy.valueOf(normalized);
    } catch (IllegalArgumentException ex) {
      throw new BaseException(
          ErrorCode.BAD_REQUEST.getCode(),
          "sortBy 仅支持 TOTAL_PRICE/UNIT_PRICE/DELIVERY_HOURS/SUPPLIER_SCORE/RESPONSE_MINUTES");
    }
  }
}
