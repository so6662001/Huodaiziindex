package com.huodaizi.backend.dto.inquiry;

import java.util.List;

public record InquiryH5InquiryStep1InitResponse(
    String city,
    String defaultCategoryCode,
    String draftId,
    List<InquiryH5InquiryStep1OptionDTO> categories,
    List<InquiryH5InquiryStep1OptionDTO> deliveryCities,
    String tipText) {}
