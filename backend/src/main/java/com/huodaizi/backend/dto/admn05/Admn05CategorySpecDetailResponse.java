package com.huodaizi.backend.dto.admn05;

import java.util.List;

public record Admn05CategorySpecDetailResponse(
    String dictId,
    String categoryCode,
    String categoryName,
    String specName,
    String specValue,
    String sceneCode,
    String sceneText,
    String status,
    String statusText,
    int sortNo,
    String remark,
    String updatedBy,
    String createdAt,
    String updatedAt,
    List<String> availableActions) {}
