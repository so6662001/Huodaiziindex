package com.huodaizi.backend.dto.dashboard;

import java.util.List;

public record A01DashboardResponse(
    String generatedAt,
    int windowDays,
    String selectedCity,
    A01DashboardOverviewDTO overview,
    List<A01DashboardTrendPointDTO> trends,
    A01DashboardFunnelDTO funnel,
    A01DashboardOperationDTO operations) {}
