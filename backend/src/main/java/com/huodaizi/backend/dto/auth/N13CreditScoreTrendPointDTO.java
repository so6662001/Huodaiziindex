package com.huodaizi.backend.dto.auth;

public record N13CreditScoreTrendPointDTO(
    String month, String creditScore, String fulfillmentRate, String disputeRate, String responseMinutes) {}
