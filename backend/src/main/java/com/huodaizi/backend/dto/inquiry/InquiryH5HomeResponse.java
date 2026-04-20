package com.huodaizi.backend.dto.inquiry;

import java.util.List;

public record InquiryH5HomeResponse(
    String city,
    String weather,
    String updateTime,
    List<InquiryH5HomeQuickNavItemDTO> quickNavs,
    List<InquiryH5HomeBannerItemDTO> banners,
    List<InquiryH5HomeMarketCardDTO> marketCards,
    List<InquiryH5HomeRecommendationItemDTO> recommendations) {}
