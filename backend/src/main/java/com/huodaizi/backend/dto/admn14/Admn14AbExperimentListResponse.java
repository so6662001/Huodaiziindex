package com.huodaizi.backend.dto.admn14;

import java.util.List;

public record Admn14AbExperimentListResponse(
    int total,
    int page,
    int pageSize,
    String experimentStatus,
    String scenarioCode,
    String optimizationStage,
    String keyword,
    int runningCount,
    int draftCount,
    int completedCount,
    int pausedCount,
    List<Admn14AbExperimentListItemDTO> records) {}
