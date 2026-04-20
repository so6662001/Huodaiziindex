package com.huodaizi.backend.dto.inquiry;

import java.util.List;

public record InquiryH5QuoteCompareResponse(
    String draftId,
    String inquiryId,
    String inquiryNo,
    String inquiryStatus,
    String specText,
    String demandQtyTon,
    String deliveryCity,
    String contactMobileMasked,
    String selectedSortBy,
    int quoteCount,
    List<InquiryQuoteCompareItemDTO> quotes,
    int total,
    int page,
    int pageSize,
    String nextActionTip,
    String dealConfirmBaseUrl) {}
