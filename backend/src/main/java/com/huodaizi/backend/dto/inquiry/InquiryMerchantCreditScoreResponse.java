package com.huodaizi.backend.dto.inquiry;

import java.util.List;

public record InquiryMerchantCreditScoreResponse(
    String merchantId,
    String merchantName,
    String score,
    String grade,
    String rankPercent,
    String scoreVersion,
    String updatedAt,
    List<InquiryMerchantCreditScoreDimensionDTO> dimensions,
    List<InquiryMerchantCreditScoreTrendPointDTO> trend,
    List<String> risks,
    List<String> suggestions) {}
