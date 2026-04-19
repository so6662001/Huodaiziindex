package com.huodaizi.backend.dto.inquiry;

public enum InquiryStatus {
  OPEN,
  QUOTING,
  DEAL_DONE,
  CLOSED;

  public boolean isClosedLike() {
    return this == DEAL_DONE || this == CLOSED;
  }
}
