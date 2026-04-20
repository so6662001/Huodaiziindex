package com.huodaizi.backend.service.admn03;

import com.huodaizi.backend.dto.admn03.Admn03RoleDetailResponse;
import com.huodaizi.backend.dto.admn03.Admn03RoleListItemDTO;
import com.huodaizi.backend.dto.admn03.Admn03RoleListRequest;
import com.huodaizi.backend.dto.admn03.Admn03RoleListResponse;
import com.huodaizi.backend.dto.admn03.Admn03RolePermissionUpdateRequest;
import com.huodaizi.backend.dto.admn03.Admn03RoleUpsertRequest;
import com.huodaizi.backend.repository.auth.Admn03RolePermissionEntity;
import com.huodaizi.backend.repository.auth.InMemoryAuthRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;
import org.springframework.stereotype.Service;

@Service
public class Admn03RolePermissionAdminService {
  private final InMemoryAuthRepository repository;

  public Admn03RolePermissionAdminService(InMemoryAuthRepository repository) {
    this.repository = repository;
  }

  public Admn03RoleListResponse list(Admn03RoleListRequest request) {
    int page = request == null ? 1 : request.safePage();
    int pageSize = request == null ? 10 : request.safePageSize();
    List<Admn03RolePermissionEntity> all =
        repository.listRolesForAdmin(request == null ? null : request.keyword(), request == null ? null : request.status());
    int from = Math.min((page - 1) * pageSize, all.size());
    int to = Math.min(from + pageSize, all.size());
    List<Admn03RoleListItemDTO> records = all.subList(from, to).stream().map(this::toListItem).toList();
    int activeCount = (int) all.stream().filter(item -> "ACTIVE".equalsIgnoreCase(safeText(item.getStatus()))).count();
    return new Admn03RoleListResponse(
        all.size(),
        page,
        pageSize,
        request == null ? "" : safeText(request.keyword()),
        request == null ? "" : safeText(request.status()),
        activeCount,
        all.size() - activeCount,
        records);
  }

  public Admn03RoleDetailResponse detail(String roleId) {
    return toDetail(repository.getRoleForAdmin(roleId));
  }

  public Admn03RoleDetailResponse upsert(Admn03RoleUpsertRequest request) {
    Admn03RolePermissionEntity entity =
        repository.upsertRoleForAdmin(
            request.roleCode(), request.roleName(), request.description(), request.permissionCodes(), request.operator());
    repository.appendAuditLogForAdmin(
        "ADMN03",
        "ROLE_UPSERT",
        "ROLE",
        entity.getRoleId(),
        safeText(request.operator()),
        "ADMIN",
        "TRACE_ADMN03_" + entity.getRoleId(),
        "SUCCESS",
        "LOW",
        "角色新增/编辑：" + entity.getRoleCode(),
        "",
        "roleCode=" + entity.getRoleCode() + ", permissions=" + String.join(",", entity.getPermissionCodes()),
        "127.0.0.1",
        "admin-console");
    return toDetail(entity);
  }

  public Admn03RoleDetailResponse updatePermissions(String roleId, Admn03RolePermissionUpdateRequest request) {
    Admn03RolePermissionEntity entity =
        repository.updateRolePermissionsForAdmin(roleId, request.permissionCodes(), request.operator());
    repository.appendAuditLogForAdmin(
        "ADMN03",
        "ROLE_PERMISSION_UPDATE",
        "ROLE",
        entity.getRoleId(),
        safeText(request.operator()),
        "ADMIN",
        "TRACE_ADMN03_PERM_" + entity.getRoleId(),
        "SUCCESS",
        "MEDIUM",
        "角色权限更新：" + entity.getRoleCode(),
        "",
        "permissions=" + String.join(",", entity.getPermissionCodes()),
        "127.0.0.1",
        "admin-console");
    return toDetail(entity);
  }

  private Admn03RoleListItemDTO toListItem(Admn03RolePermissionEntity entity) {
    return new Admn03RoleListItemDTO(
        entity.getRoleCode(),
        entity.getRoleName(),
        safeText(entity.getDescription()),
        repository.countUsersByRoleCodeForAdmin(entity.getRoleCode()),
        entity.getPermissionCodes().size(),
        "SYSTEM".equalsIgnoreCase(safeText(entity.getRoleType())),
        entity.getStatus(),
        statusText(entity.getStatus()),
        safeText(entity.getOperator()),
        toText(entity.getUpdatedAt()));
  }

  private Admn03RoleDetailResponse toDetail(Admn03RolePermissionEntity entity) {
    List<String> permissionCodes = entity.getPermissionCodes();
    List<String> permissionNames = permissionCodes.stream().map(this::permissionName).toList();
    return new Admn03RoleDetailResponse(
        entity.getRoleId(),
        entity.getRoleCode(),
        entity.getRoleName(),
        entity.getRoleType(),
        roleTypeText(entity.getRoleType()),
        entity.getStatus(),
        statusText(entity.getStatus()),
        safeText(entity.getDescription()),
        repository.countUsersByRoleCodeForAdmin(entity.getRoleCode()),
        permissionCodes,
        permissionNames,
        safeText(entity.getOperator()),
        toText(entity.getCreatedAt()),
        toText(entity.getUpdatedAt()),
        availableActions(entity));
  }

  private List<String> availableActions(Admn03RolePermissionEntity entity) {
    if ("SYSTEM".equalsIgnoreCase(safeText(entity.getRoleType()))
        && "SUPER_ADMIN".equalsIgnoreCase(safeText(entity.getRoleCode()))) {
      return List.of("VIEW");
    }
    return List.of("VIEW", "EDIT_ROLE", "EDIT_PERMISSIONS");
  }

  private String permissionName(String permissionCode) {
    return switch (safeText(permissionCode).toUpperCase(Locale.ROOT)) {
      case "DASHBOARD_VIEW" -> "经营看板查看";
      case "LEAD_OPS_MANAGE" -> "线索运营管理";
      case "RISK_ALERT_MANAGE" -> "风险预警管理";
      case "ADMN01_CERT_REVIEW" -> "商家认证审核";
      case "ADMN02_BLACKLIST_MANAGE" -> "买家黑名单管理";
      case "ADMN03_RBAC_MANAGE" -> "角色权限管理";
      case "ADMN04_AUDIT_LOG_VIEW" -> "操作审计日志查看";
      case "ADMN05_DICT_MANAGE" -> "类目规格词库管理";
      case "ADMN06_LEAD_QA_MANAGE" -> "线索质检中心";
      case "ADMN08_FUNNEL_VIEW" -> "成交漏斗分析";
      case "ADMN09_ARBITRATION_MANAGE" -> "仲裁工单中心";
      case "ADMN10_BILLING_RULE_MANAGE" -> "计费规则配置";
      case "ADMN11_PAYMENT_REFUND_MANAGE" -> "支付与退款管理";
      case "ADMN12_AD_SLOT_SCHEDULE_MANAGE" -> "广告位排期中心";
      default -> "权限-" + safeText(permissionCode);
    };
  }

  private String roleTypeText(String roleType) {
    return "SYSTEM".equalsIgnoreCase(safeText(roleType)) ? "系统角色" : "自定义角色";
  }

  private String statusText(String status) {
    return switch (safeText(status).toUpperCase(Locale.ROOT)) {
      case "ACTIVE" -> "启用";
      case "DISABLED" -> "禁用";
      default -> "未知";
    };
  }

  private String safeText(String text) {
    return text == null ? "" : text.trim();
  }

  private String toText(LocalDateTime value) {
    return value == null ? "" : value.toString();
  }
}
