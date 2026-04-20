package com.huodaizi.backend.dto.dashboard;

public record A01DashboardTrendPointDTO(
    String date, int inquiryCount, int dealCount, int pickupCount, int paidCount, int adLeadCount) {}
