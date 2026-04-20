package com.huodaizi.backend.dto.inquiry;

import java.util.List;

public record InquiryH5InquiryStep2InitResponse(
    String draftId,
    String categoryCode,
    String specText,
    String deliveryCity,
    String demandQtyTon,
    String invoiceNeed,
    String contactMobileMasked,
    List<InquiryH5InquiryStep1OptionDTO> expectedDeliveryOptions,
    List<InquiryH5InquiryStep1OptionDTO> settleTypeOptions,
    String tipText) {}
