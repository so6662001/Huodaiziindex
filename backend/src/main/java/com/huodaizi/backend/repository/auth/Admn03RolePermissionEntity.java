package com.huodaizi.backend.repository.auth;

import java.time.LocalDateTime;
import java.util.List;

public class Admn03RolePermissionEntity {
  private final String roleId;
  private String roleCode;
  private String roleName;
  private String roleType;
  private String status;
  private String description;
  private List<String> permissionCodes;
  private String operator;
  private final LocalDateTime createdAt;
  private LocalDateTime updatedAt;

  public Admn03RolePermissionEntity(
      String roleId,
      String roleCode,
      String roleName,
      String roleType,
      String status,
      String description,
      List<String> permissionCodes,
      String operator,
      LocalDateTime createdAt,
      LocalDateTime updatedAt) {
    this.roleId = roleId;
    this.roleCode = roleCode;
    this.roleName = roleName;
    this.roleType = roleType;
    this.status = status;
    this.description = description;
    this.permissionCodes = permissionCodes == null ? List.of() : List.copyOf(permissionCodes);
    this.operator = operator;
    this.createdAt = createdAt;
    this.updatedAt = updatedAt;
  }

  public String getRoleId() {
    return roleId;
  }

  public String getRoleCode() {
    return roleCode;
  }

  public String getRoleName() {
    return roleName;
  }

  public String getRoleType() {
    return roleType;
  }

  public String getStatus() {
    return status;
  }

  public String getDescription() {
    return description;
  }

  public List<String> getPermissionCodes() {
    return List.copyOf(permissionCodes);
  }

  public String getOperator() {
    return operator;
  }

  public LocalDateTime getCreatedAt() {
    return createdAt;
  }

  public LocalDateTime getUpdatedAt() {
    return updatedAt;
  }

  public void updateBasic(
      String newRoleCode,
      String newRoleName,
      String newRoleType,
      String newStatus,
      String newDescription,
      String newOperator,
      LocalDateTime now) {
    this.roleCode = newRoleCode;
    this.roleName = newRoleName;
    this.roleType = newRoleType;
    this.status = newStatus;
    this.description = newDescription;
    this.operator = newOperator;
    this.updatedAt = now;
  }

  public void updatePermissions(List<String> newPermissionCodes, String newOperator, LocalDateTime now) {
    this.permissionCodes = newPermissionCodes == null ? List.of() : List.copyOf(newPermissionCodes);
    this.operator = newOperator;
    this.updatedAt = now;
  }
}
