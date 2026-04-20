package com.huodaizi.backend.dto.admn05;

public record Admn05CategorySpecListItemDTO(
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
    String updatedBy,
    String updatedAt) {}
