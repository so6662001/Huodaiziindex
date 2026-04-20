package com.huodaizi.backend.dto.leadops;

import com.huodaizi.backend.common.BaseException;
import com.huodaizi.backend.common.ErrorCode;
import java.util.Locale;

public enum A02LeadOpsSource {
  ALL,
  INQUIRY,
  SITE_AD;

  public static A02LeadOpsSource fromOrThrow(String source) {
    if (source == null || source.isBlank()) {
      throw new BaseException(ErrorCode.BAD_REQUEST.getCode(), "source 不能为空");
    }
    String normalized = source.trim().toUpperCase(Locale.ROOT);
    try {
      return A02LeadOpsSource.valueOf(normalized);
    } catch (IllegalArgumentException ex) {
      throw new BaseException(ErrorCode.BAD_REQUEST.getCode(), "source 仅支持 ALL/INQUIRY/SITE_AD");
    }
  }
}
