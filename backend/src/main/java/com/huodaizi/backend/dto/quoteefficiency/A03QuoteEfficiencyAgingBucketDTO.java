package com.huodaizi.backend.dto.quoteefficiency;

public record A03QuoteEfficiencyAgingBucketDTO(
    int within10Minutes, int within30Minutes, int within60Minutes, int over60Minutes, int timeoutCount) {}
