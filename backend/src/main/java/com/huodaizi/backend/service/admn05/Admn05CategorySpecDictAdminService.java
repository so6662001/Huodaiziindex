package com.huodaizi.backend.service.admn05;

import com.huodaizi.backend.dto.admn05.Admn05CategorySpecDetailResponse;
import com.huodaizi.backend.dto.admn05.Admn05CategorySpecListItemDTO;
import com.huodaizi.backend.dto.admn05.Admn05CategorySpecListRequest;
import com.huodaizi.backend.dto.admn05.Admn05CategorySpecListResponse;
import com.huodaizi.backend.dto.admn05.Admn05CategorySpecUpsertRequest;
import com.huodaizi.backend.repository.auth.Admn05CategorySpecDictEntity;
import com.huodaizi.backend.repository.auth.InMemoryAuthRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;
import org.springframework.stereotype.Service;

@Service
public class Admn05CategorySpecDictAdminService {
  private final InMemoryAuthRepository repository;

  public Admn05CategorySpecDictAdminService(InMemoryAuthRepository repository) {
    this.repository = repository;
  }

  public Admn05CategorySpecListResponse list(Admn05CategorySpecListRequest request) {
    int page = request == null ? 1 : request.safePage();
    int pageSize = request == null ? 10 : request.safePageSize();
    List<Admn05CategorySpecDictEntity> all =
        repository.listCategorySpecDictsForAdmin(
            request == null ? null : request.keyword(),
            request == null ? null : request.status(),
            request == null ? null : request.sceneCode());
    int from = Math.min((page - 1) * pageSize, all.size());
    int to = Math.min(from + pageSize, all.size());
    List<Admn05CategorySpecListItemDTO> records = all.subList(from, to).stream().map(this::toListItem).toList();
    int activeCount =
        (int) all.stream().filter(item -> "ACTIVE".equalsIgnoreCase(safeText(item.getStatus()))).count();
    return new Admn05CategorySpecListResponse(
        all.size(),
        page,
        pageSize,
        request == null ? "" : safeText(request.keyword()),
        request == null ? "" : safeText(request.status()),
        request == null ? "" : safeText(request.sceneCode()),
        activeCount,
        all.size() - activeCount,
        records);
  }

  public Admn05CategorySpecDetailResponse detail(String dictId) {
    return toDetail(repository.getCategorySpecDictForAdmin(dictId));
  }

  public Admn05CategorySpecDetailResponse upsert(Admn05CategorySpecUpsertRequest request) {
    Admn05CategorySpecDictEntity entity =
        repository.upsertCategorySpecDictForAdmin(
            request.categoryCode(),
            request.categoryName(),
            request.specName(),
            request.specValue(),
            request.sceneCode(),
            request.status(),
            request.sortNo(),
            request.remark(),
            request.operator());
    repository.appendAuditLogForAdmin(
        "ADMN05",
        "DICT_UPSERT",
        "CATEGORY_SPEC_DICT",
        entity.getDictId(),
        safeText(request.operator()),
        "ADMIN",
        "TRACE_ADMN05_UPSERT_" + entity.getDictId(),
        "SUCCESS",
        "ACTIVE".equalsIgnoreCase(entity.getStatus()) ? "LOW" : "MEDIUM",
        "类目规格词库新增/更新：" + entity.getCategoryCode() + "/" + entity.getSpecName(),
        "",
        "sceneCode="
            + entity.getSceneCode()
            + ", status="
            + entity.getStatus()
            + ", specValue="
            + entity.getSpecValue(),
        "127.0.0.1",
        "admn05-service");
    return toDetail(entity);
  }

  public void appendAuditQueryLogForAdmin(String operator, String summary, String traceId, String targetId) {
    repository.appendAuditLogForAdmin(
        "ADMN05",
        "DICT_QUERY",
        "CATEGORY_SPEC_DICT",
        safeText(targetId).isBlank() ? "LIST" : targetId,
        operator,
        "ADMIN",
        traceId,
        "SUCCESS",
        "LOW",
        summary,
        "",
        "",
        "127.0.0.1",
        "admn05-service");
  }

  private Admn05CategorySpecListItemDTO toListItem(Admn05CategorySpecDictEntity entity) {
    return new Admn05CategorySpecListItemDTO(
        entity.getDictId(),
        entity.getCategoryCode(),
        entity.getCategoryName(),
        entity.getSpecName(),
        entity.getSpecValue(),
        entity.getSceneCode(),
        repository.admn05SceneText(entity.getSceneCode()),
        entity.getStatus(),
        statusText(entity.getStatus()),
        entity.getSortNo(),
        safeText(entity.getOperator()),
        toText(entity.getUpdatedAt()));
  }

  private Admn05CategorySpecDetailResponse toDetail(Admn05CategorySpecDictEntity entity) {
    return new Admn05CategorySpecDetailResponse(
        entity.getDictId(),
        entity.getCategoryCode(),
        entity.getCategoryName(),
        entity.getSpecName(),
        entity.getSpecValue(),
        entity.getSceneCode(),
        repository.admn05SceneText(entity.getSceneCode()),
        entity.getStatus(),
        statusText(entity.getStatus()),
        entity.getSortNo(),
        safeText(entity.getRemark()),
        safeText(entity.getOperator()),
        toText(entity.getCreatedAt()),
        toText(entity.getUpdatedAt()),
        availableActions(entity.getStatus()));
  }

  private List<String> availableActions(String status) {
    if ("DISABLED".equalsIgnoreCase(safeText(status))) {
      return List.of("VIEW", "ENABLE", "EDIT");
    }
    return List.of("VIEW", "DISABLE", "EDIT");
  }

  private String statusText(String status) {
    return switch (safeText(status).toUpperCase(Locale.ROOT)) {
      case "ACTIVE" -> "启用";
      case "DISABLED" -> "禁用";
      default -> "未知";
    };
  }

  private String safeText(String value) {
    return value == null ? "" : value.trim();
  }

  private String toText(LocalDateTime value) {
    return value == null ? "" : value.toString();
  }
}
