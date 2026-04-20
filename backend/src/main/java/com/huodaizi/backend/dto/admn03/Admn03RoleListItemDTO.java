package com.huodaizi.backend.dto.admn03;

public record Admn03RoleListItemDTO(
    String roleCode,
    String roleName,
    String roleDesc,
    int userCount,
    int permissionCount,
    boolean systemRole,
    String status,
    String statusText,
    String updatedBy,
    String updatedAt) {}
