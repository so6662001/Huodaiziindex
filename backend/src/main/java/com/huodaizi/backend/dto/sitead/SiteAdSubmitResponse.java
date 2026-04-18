package com.huodaizi.backend.dto.sitead;

public record SiteAdSubmitResponse(
    String id,
    String demandNo,
    String message,
    String city,
    String placement,
    String duration,
    String companyName,
    String contactNameMasked,
    String phoneMasked,
    String status,
    String submittedAt) {}
