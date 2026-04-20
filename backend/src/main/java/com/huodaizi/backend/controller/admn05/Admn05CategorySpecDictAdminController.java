package com.huodaizi.backend.controller.admn05;

import com.huodaizi.backend.common.ApiResponse;
import com.huodaizi.backend.dto.admn05.Admn05CategorySpecDetailResponse;
import com.huodaizi.backend.dto.admn05.Admn05CategorySpecListRequest;
import com.huodaizi.backend.dto.admn05.Admn05CategorySpecListResponse;
import com.huodaizi.backend.dto.admn05.Admn05CategorySpecUpsertRequest;
import com.huodaizi.backend.service.admn05.Admn05CategorySpecDictAdminService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/category-spec-dicts")
public class Admn05CategorySpecDictAdminController {
  private final Admn05CategorySpecDictAdminService service;
  private static final String OPERATOR = "admn05-admin";

  public Admn05CategorySpecDictAdminController(Admn05CategorySpecDictAdminService service) {
    this.service = service;
  }

  @GetMapping
  public ApiResponse<Admn05CategorySpecListResponse> list(
      @Valid @ModelAttribute Admn05CategorySpecListRequest request) {
    service.appendAuditQueryLogForAdmin(
        OPERATOR,
        "查询类目规格词库 scene=" + (request == null ? "" : request.sceneCode()),
        "TRACE_ADMN05_QUERY_" + System.currentTimeMillis(),
        "LIST");
    return ApiResponse.success(service.list(request));
  }

  @GetMapping("/{dictId}")
  public ApiResponse<Admn05CategorySpecDetailResponse> detail(@PathVariable("dictId") String dictId) {
    service.appendAuditQueryLogForAdmin(
        OPERATOR, "查看类目规格词库详情 dictId=" + dictId, "TRACE_ADMN05_DETAIL_" + dictId, dictId);
    return ApiResponse.success(service.detail(dictId));
  }

  @PostMapping
  public ApiResponse<Admn05CategorySpecDetailResponse> upsert(
      @Valid @RequestBody Admn05CategorySpecUpsertRequest request) {
    return ApiResponse.success(service.upsert(request));
  }
}
