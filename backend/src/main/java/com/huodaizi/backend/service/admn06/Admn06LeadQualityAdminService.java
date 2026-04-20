package com.huodaizi.backend.service.admn06;

import com.huodaizi.backend.common.BaseException;
import com.huodaizi.backend.common.ErrorCode;
import com.huodaizi.backend.dto.admn06.Admn06LeadQualityDetailResponse;
import com.huodaizi.backend.dto.admn06.Admn06LeadQualityListItemDTO;
import com.huodaizi.backend.dto.admn06.Admn06LeadQualityListRequest;
import com.huodaizi.backend.dto.admn06.Admn06LeadQualityListResponse;
import com.huodaizi.backend.dto.admn06.Admn06LeadQualityReviewRequest;
import com.huodaizi.backend.repository.auth.Admn06LeadQualityEntity;
import com.huodaizi.backend.repository.auth.InMemoryAuthRepository;
import com.huodaizi.backend.repository.inquiry.InMemoryInquiryRepository;
import com.huodaizi.backend.repository.inquiry.InquiryMerchantLeadEntity;
import com.huodaizi.backend.repository.siteadlead.InMemorySiteAdLeadRepository;
import com.huodaizi.backend.repository.siteadlead.SiteAdLeadEntity;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;
import org.springframework.stereotype.Service;

@Service
public class Admn06LeadQualityAdminService {
  private final InMemoryAuthRepository repository;
  private final InMemoryInquiryRepository inquiryRepository;
  private final InMemorySiteAdLeadRepository siteAdLeadRepository;

  public Admn06LeadQualityAdminService(
      InMemoryAuthRepository repository,
      InMemoryInquiryRepository inquiryRepository,
      InMemorySiteAdLeadRepository siteAdLeadRepository) {
    this.repository = repository;
    this.inquiryRepository = inquiryRepository;
    this.siteAdLeadRepository = siteAdLeadRepository;
  }

  public Admn06LeadQualityListResponse list(Admn06LeadQualityListRequest request) {
    int page = request == null ? 1 : request.safePage();
    int pageSize = request == null ? 10 : request.safePageSize();
    List<Admn06LeadQualityEntity> all =
        repository.listLeadQualityForAdmin(
            request == null ? null : request.source(),
            request == null ? null : request.qualityStatus(),
            request == null ? null : request.riskLevel(),
            request == null ? null : request.reviewer(),
            request == null ? null : request.keyword());
    int from = Math.min((page - 1) * pageSize, all.size());
    int to = Math.min(from + pageSize, all.size());
    List<Admn06LeadQualityListItemDTO> records = all.subList(from, to).stream().map(this::toListItem).toList();
    int passCount =
        (int) all.stream().filter(item -> "PASS".equalsIgnoreCase(safeText(item.getQualityStatus()))).count();
    int rejectCount =
        (int) all.stream().filter(item -> "REJECT".equalsIgnoreCase(safeText(item.getQualityStatus()))).count();
    return new Admn06LeadQualityListResponse(
        all.size(),
        page,
        pageSize,
        request == null ? "" : safeText(request.source()),
        request == null ? "" : safeText(request.qualityStatus()),
        request == null ? "" : safeText(request.riskLevel()),
        request == null ? "" : safeText(request.reviewer()),
        request == null ? "" : safeText(request.keyword()),
        passCount,
        rejectCount,
        all.size() - passCount - rejectCount,
        records);
  }

  public Admn06LeadQualityDetailResponse detail(String qualityId) {
    return toDetail(repository.getLeadQualityForAdmin(qualityId));
  }

  public Admn06LeadQualityDetailResponse review(String qualityId, Admn06LeadQualityReviewRequest request) {
    Admn06LeadQualityEntity entity =
        repository.reviewLeadQualityForAdmin(
            qualityId,
            request.qualityStatus(),
            request.riskLevel(),
            request.qualityScore(),
            request.ruleCode(),
            request.reviewRemark(),
            request.reviewer());
    String normalizedRisk = safeText(entity.getRiskLevel()).toUpperCase(Locale.ROOT);
    if (normalizedRisk.isBlank()) {
      normalizedRisk = "LOW";
    }
    repository.appendAuditLogForAdmin(
        "ADMN06",
        "LEAD_QA_REVIEW",
        "LEAD_QUALITY",
        entity.getQualityId(),
        safeText(request.reviewer()),
        "ADMIN",
        "TRACE_ADMN06_REVIEW_" + entity.getQualityId(),
        "SUCCESS",
        normalizedRisk,
        "线索质检复核：" + entity.getLeadNo() + " -> " + entity.getQualityStatus(),
        "",
        "status="
            + entity.getQualityStatus()
            + ", score="
            + entity.getQualityScore()
            + ", risk="
            + entity.getRiskLevel(),
        "127.0.0.1",
        "admn06-service");
    return toDetail(entity);
  }

  public void appendAuditQueryLogForAdmin(String operator, String summary, String traceId, String targetId) {
    repository.appendAuditLogForAdmin(
        "ADMN06",
        "LEAD_QA_QUERY",
        "LEAD_QUALITY",
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
        "admn06-service");
  }

  private Admn06LeadQualityListItemDTO toListItem(Admn06LeadQualityEntity entity) {
    LeadMeta meta = resolveLeadMeta(entity);
    return new Admn06LeadQualityListItemDTO(
        entity.getQualityId(),
        entity.getSource(),
        repository.admn06SourceText(entity.getSource()),
        entity.getLeadId(),
        entity.getLeadNo(),
        meta.companyName(),
        meta.city(),
        entity.getQualityStatus(),
        qualityStatusText(entity.getQualityStatus()),
        entity.getRiskLevel(),
        riskLevelText(entity.getRiskLevel()),
        entity.getQualityScore(),
        firstIssueTag(entity.getIssueTags()),
        safeText(entity.getReviewer()),
        toText(entity.getUpdatedAt()),
        toText(entity.getCreatedAt()));
  }

  private Admn06LeadQualityDetailResponse toDetail(Admn06LeadQualityEntity entity) {
    LeadMeta meta = resolveLeadMeta(entity);
    return new Admn06LeadQualityDetailResponse(
        entity.getQualityId(),
        entity.getSource(),
        repository.admn06SourceText(entity.getSource()),
        entity.getLeadId(),
        entity.getLeadNo(),
        meta.companyName(),
        meta.city(),
        meta.owner(),
        entity.getQualityStatus(),
        qualityStatusText(entity.getQualityStatus()),
        entity.getRiskLevel(),
        riskLevelText(entity.getRiskLevel()),
        String.valueOf(entity.getQualityScore()),
        issueCountText(entity.getIssueTags()),
        firstIssueTag(entity.getIssueTags()),
        safeText(entity.getIssueTags()),
        safeText(entity.getReviewer()),
        safeText(entity.getReviewRemark()),
        toText(entity.getUpdatedAt()),
        toText(entity.getCreatedAt()),
        toText(entity.getUpdatedAt()),
        availableActions(entity.getQualityStatus()));
  }

  private LeadMeta resolveLeadMeta(Admn06LeadQualityEntity entity) {
    String source = safeText(entity.getSource()).toUpperCase(Locale.ROOT);
    if ("INQUIRY".equals(source)) {
      InquiryMerchantLeadEntity lead = findInquiryLead(entity.getLeadId());
      return new LeadMeta(
          lead == null ? "-" : safeText(lead.getBuyerCompany()),
          lead == null ? "-" : safeText(lead.getDeliveryCity()),
          lead == null ? "-" : safeText(lead.getOwner()));
    }
    if ("SITE_AD".equals(source)) {
      SiteAdLeadEntity lead = findSiteAdLead(entity.getLeadId());
      return new LeadMeta(
          lead == null ? "-" : safeText(lead.getCompanyName()),
          lead == null ? "-" : safeText(lead.getCity()),
          lead == null ? "-" : safeText(lead.getOwner()));
    }
    return new LeadMeta("-", "-", "-");
  }

  private InquiryMerchantLeadEntity findInquiryLead(String leadId) {
    return inquiryRepository.allMerchantLeads().stream()
        .filter(item -> safeText(leadId).equalsIgnoreCase(safeText(item.getId())))
        .findFirst()
        .orElse(null);
  }

  private SiteAdLeadEntity findSiteAdLead(String leadId) {
    return siteAdLeadRepository.allLeads().stream()
        .filter(item -> safeText(leadId).equalsIgnoreCase(safeText(item.getId())))
        .findFirst()
        .orElse(null);
  }

  private List<String> availableActions(String qualityStatus) {
    String normalized = safeText(qualityStatus).toUpperCase(Locale.ROOT);
    return switch (normalized) {
      case "PASS" -> List.of("VIEW", "RECHECK", "REJECT");
      case "REJECT" -> List.of("VIEW", "RECHECK", "PASS");
      default -> List.of("VIEW", "PASS", "REJECT");
    };
  }

  private String qualityStatusText(String qualityStatus) {
    return switch (safeText(qualityStatus).toUpperCase(Locale.ROOT)) {
      case "PASS" -> "通过";
      case "REJECT" -> "驳回";
      case "RECHECK" -> "待复检";
      case "PENDING" -> "待审核";
      default -> "未知";
    };
  }

  private String riskLevelText(String riskLevel) {
    return switch (safeText(riskLevel).toUpperCase(Locale.ROOT)) {
      case "LOW" -> "低风险";
      case "MEDIUM" -> "中风险";
      case "HIGH" -> "高风险";
      default -> "未知";
    };
  }

  private String firstIssueTag(String issueTags) {
    String text = safeText(issueTags);
    if (text.isBlank()) {
      return "";
    }
    int idx = text.indexOf(',');
    return idx < 0 ? text : text.substring(0, idx);
  }

  private String issueCountText(String issueTags) {
    String text = safeText(issueTags);
    if (text.isBlank()) {
      return "0项";
    }
    return text.split(",").length + "项";
  }

  private String safeText(String value) {
    return value == null ? "" : value.trim();
  }

  private String toText(LocalDateTime value) {
    return value == null ? "" : value.toString();
  }

  private record LeadMeta(String companyName, String city, String owner) {}
}
