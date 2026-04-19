package com.huodaizi.backend.repository.siteadlead;

import com.huodaizi.backend.common.BaseException;
import com.huodaizi.backend.common.ErrorCode;
import com.huodaizi.backend.dto.siteadlead.SiteAdLeadAdminAssignRequest;
import com.huodaizi.backend.dto.siteadlead.SiteAdLeadAdminFollowRequest;
import com.huodaizi.backend.dto.siteadlead.SiteAdLeadAdminUpdateRequest;
import com.huodaizi.backend.dto.siteadlead.SiteAdLeadListRequest;
import com.huodaizi.backend.dto.siteadlead.SiteAdLeadMineRequest;
import com.huodaizi.backend.dto.siteadlead.SiteAdLeadStatus;
import com.huodaizi.backend.dto.siteadlead.SiteAdLeadSubmitRequest;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicLong;
import org.springframework.stereotype.Repository;

@Repository
public class InMemorySiteAdLeadRepository {

  private static final DateTimeFormatter NO_FMT = DateTimeFormatter.ofPattern("yyyyMMdd");

  private final AtomicLong seq = new AtomicLong(20260418000L);
  private final AtomicLong followSeq = new AtomicLong(1L);
  private final ConcurrentMap<String, SiteAdLeadEntity> store = new ConcurrentHashMap<>();

  public InMemorySiteAdLeadRepository() {
    seed();
  }

  public SiteAdLeadEntity submit(SiteAdLeadSubmitRequest request) {
    String id = "SAL" + seq.incrementAndGet();
    String leadNo = "ADL-" + NO_FMT.format(LocalDateTime.now()) + "-" + id;
    SiteAdLeadEntity entity =
        new SiteAdLeadEntity(
            id,
            leadNo,
            request.placementId().trim(),
            request.placementName().trim(),
            request.city().trim(),
            request.duration().trim(),
            defaultText(request.budget(), "-"),
            request.companyName().trim(),
            request.contactName().trim(),
            request.contactPhone().trim(),
            defaultText(request.remark(), "-"),
            SiteAdLeadStatus.SUBMITTED,
            "",
            "",
            "",
            LocalDateTime.now(),
            LocalDateTime.now());
    store.put(id, entity);
    appendFollow(entity, "SYSTEM", "SUBMIT", "系统创建线索，待商务分配", null);
    return entity;
  }

  public List<SiteAdLeadEntity> mine(SiteAdLeadMineRequest request) {
    String normalizedPhone = normalizePhone(request.contactPhone());
    String keyword = normalize(request.keyword());
    return store.values().stream()
        .filter(item -> normalizePhone(item.getContactPhone()).equals(normalizedPhone))
        .filter(
            item ->
                keyword == null
                    || item.getLeadNo().toLowerCase(Locale.ROOT).contains(keyword)
                    || item.getPlacementName().toLowerCase(Locale.ROOT).contains(keyword)
                    || item.getCompanyName().toLowerCase(Locale.ROOT).contains(keyword))
        .sorted(Comparator.comparing(SiteAdLeadEntity::getUpdatedAt, Comparator.reverseOrder()))
        .toList();
  }

  public List<SiteAdLeadEntity> adminList(SiteAdLeadListRequest request) {
    SiteAdLeadStatus status = normalizeStatusOrNull(request.status());
    String phone = normalizePhoneOrNull(request.phone());
    String keyword = normalize(request.keyword());
    return store.values().stream()
        .filter(item -> status == null || item.getStatus() == status)
        .filter(item -> phone == null || normalizePhone(item.getContactPhone()).startsWith(phone))
        .filter(
            item ->
                keyword == null
                    || item.getLeadNo().toLowerCase(Locale.ROOT).contains(keyword)
                    || item.getPlacementName().toLowerCase(Locale.ROOT).contains(keyword)
                    || item.getCompanyName().toLowerCase(Locale.ROOT).contains(keyword)
                    || item.getContactName().toLowerCase(Locale.ROOT).contains(keyword))
        .sorted(Comparator.comparing(SiteAdLeadEntity::getUpdatedAt, Comparator.reverseOrder()))
        .toList();
  }

  public SiteAdLeadEntity adminAssign(String id, SiteAdLeadAdminAssignRequest request) {
    SiteAdLeadEntity entity = requireById(id);
    String owner = defaultText(request.ownerName(), "").trim();
    if (owner.isEmpty()) {
      throw new BaseException(ErrorCode.BAD_REQUEST.getCode(), "ownerName 不能为空");
    }
    entity.setOwner(owner);
    if (request.team() != null && !request.team().isBlank()) {
      entity.setRemark(defaultText(entity.getRemark(), "-") + " | 归属团队:" + request.team().trim());
    }
    entity.setStatus(SiteAdLeadStatus.ASSIGNED);
    appendFollow(entity, owner, "ASSIGN", defaultText(request.comment(), "已分配跟进"), null);
    return entity;
  }

  public SiteAdLeadEntity adminUpdateStatus(String id, SiteAdLeadAdminUpdateRequest request) {
    SiteAdLeadEntity entity = requireById(id);
    String status = normalizeStatus(request.status());
    entity.setStatus(SiteAdLeadStatus.valueOf(status));
    String operator = defaultText(request.operatorName(), "SYSTEM");
    appendFollow(entity, operator, "STATUS_CHANGE", defaultText(request.comment(), "状态更新为 " + status), null);
    return entity;
  }

  public SiteAdLeadEntity adminAppendFollow(String id, SiteAdLeadAdminFollowRequest request) {
    SiteAdLeadEntity entity = requireById(id);
    String operator = defaultText(request.operator(), "SYSTEM");
    String content = defaultText(request.content(), "").trim();
    if (content.isEmpty()) {
      throw new BaseException(ErrorCode.BAD_REQUEST.getCode(), "content 不能为空");
    }
    appendFollow(entity, operator, "FOLLOW_UP", content, request.nextAction());
    return entity;
  }

  public SiteAdLeadEntity adminAppendFollow(
      String id, String content, String nextAction, String operator, String action) {
    SiteAdLeadEntity entity = requireById(id);
    String normalizedContent = defaultText(content, "").trim();
    if (normalizedContent.isEmpty()) {
      throw new BaseException(ErrorCode.BAD_REQUEST.getCode(), "content 不能为空");
    }
    appendFollow(
        entity,
        defaultText(operator, "SYSTEM"),
        defaultText(action, "FOLLOW_UP"),
        normalizedContent,
        defaultText(nextAction, ""));
    return entity;
  }

  public SiteAdLeadEntity getById(String id) {
    return requireById(id);
  }

  public List<SiteAdLeadEntity> allLeads() {
    return store.values().stream()
        .sorted(Comparator.comparing(SiteAdLeadEntity::getUpdatedAt, Comparator.reverseOrder()))
        .toList();
  }

  private SiteAdLeadEntity requireById(String id) {
    SiteAdLeadEntity entity = store.get(id);
    if (entity == null) {
      throw new BaseException(ErrorCode.NOT_FOUND.getCode(), "投放单不存在");
    }
    return entity;
  }

  private void appendFollow(
      SiteAdLeadEntity entity, String operator, String action, String content, String nextAction) {
    SiteAdLeadFollowEntity follow =
        new SiteAdLeadFollowEntity(
            "SALF" + followSeq.incrementAndGet(),
            entity.getId(),
            defaultText(operator, "SYSTEM"),
            defaultText(action, "FOLLOW_UP"),
            content,
            defaultText(nextAction, ""),
            LocalDateTime.now());
    entity.addFollow(follow);
  }

  private String normalize(String text) {
    if (text == null || text.isBlank()) {
      return null;
    }
    return text.trim().toLowerCase(Locale.ROOT);
  }

  private String defaultText(String text, String fallback) {
    return text == null || text.isBlank() ? fallback : text;
  }

  private String normalizeStatus(String status) {
    if (status == null || status.isBlank()) {
      throw new BaseException(ErrorCode.BAD_REQUEST.getCode(), "status 不能为空");
    }
    String normalized = status.trim().toUpperCase(Locale.ROOT);
    try {
      SiteAdLeadStatus.valueOf(normalized);
      return normalized;
    } catch (IllegalArgumentException ex) {
      throw new BaseException(
          ErrorCode.BAD_REQUEST.getCode(),
          "status 仅支持 SUBMITTED/ASSIGNED/CONTACTED/PROPOSAL_SENT/CONVERTED/CLOSED");
    }
  }

  private SiteAdLeadStatus normalizeStatusOrNull(String status) {
    if (status == null || status.isBlank()) {
      return null;
    }
    return SiteAdLeadStatus.valueOf(normalizeStatus(status));
  }

  private String normalizePhone(String phone) {
    return phone == null ? "" : phone.replaceAll("\\D", "");
  }

  private String normalizePhoneOrNull(String phone) {
    String normalized = normalizePhone(phone);
    return normalized.isBlank() ? null : normalized;
  }

  private void seed() {
    SiteAdLeadEntity a =
        submit(
            new SiteAdLeadSubmitRequest(
                "SA001",
                "分站首页焦点位",
                "唐山",
                "30天",
                "5000-10000",
                "唐山钢贸集团",
                "王经理",
                "13800138000",
                "主推螺纹钢供应",
                true));
    adminAssign(
        a.getId(),
        new SiteAdLeadAdminAssignRequest("李商务", "华北组", "优先跟进，已安排电话沟通"));
    adminUpdateStatus(
        a.getId(),
        new SiteAdLeadAdminUpdateRequest("CONTACTED", "李商务", "已电话沟通，准备报价方案"));

    SiteAdLeadEntity b =
        submit(
            new SiteAdLeadSubmitRequest(
                "SA003",
                "分站仓储物流推荐位",
                "无锡",
                "15天",
                "3000-5000",
                "无锡板材贸易",
                "张总",
                "13900139000",
                "重点覆盖仓配客户",
                true));
    adminAssign(
        b.getId(),
        new SiteAdLeadAdminAssignRequest("周商务", "华东组", "客户要求本周内上线"));
  }
}
