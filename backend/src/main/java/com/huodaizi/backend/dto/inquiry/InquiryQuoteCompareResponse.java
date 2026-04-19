package com.huodaizi.backend.dto.inquiry;

import java.util.List;

public record InquiryQuoteCompareResponse(
    String inquiryId,
    String inquiryNo,
    String specText,
    String demandQtyTon,
    String deliveryCity,
    String inquiryStatus,
    List<InquiryQuoteCompareItemDTO> quotes,
    int total,
    int page,
    int pageSize) {}
