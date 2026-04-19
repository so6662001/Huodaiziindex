package com.huodaizi.backend.dto.inquiry;

public record InquiryH5HomeRecommendationItemDTO(
    String merchantId,
    String merchantName,
    String tag,
    String score,
    String responseMinutes,
    String mainSpec,
    String city,
    String actionUrl) {}
