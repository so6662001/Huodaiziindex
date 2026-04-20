package com.huodaizi.backend.dto.admn03;

import java.util.List;

public record Admn03RoleDetailResponse(
    String roleId,
    String roleCode,
    String roleName,
    String roleType,
    String roleTypeText,
    String status,
    String statusText,
    String description,
    int userCount,
    List<String> permissionCodes,
    List<String> permissionNames,
    String updatedBy,
    String createdAt,
    String updatedAt,
    List<String> availableActions) {}
