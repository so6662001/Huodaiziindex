package com.huodaizi.backend.repository.leadops;

import com.huodaizi.backend.common.BaseException;
import com.huodaizi.backend.common.ErrorCode;
import com.huodaizi.backend.dto.inquiry.InquiryMerchantLeadQuoteRequest;
import com.huodaizi.backend.dto.inquiry.InquiryMerchantLeadStatus;
import com.huodaizi.backend.dto.inquiry.InquiryMerchantLeadStatusUpdateRequest;
import com.huodaizi.backend.dto.leadops.A02LeadOpsAssignRequest;
import com.huodaizi.backend.dto.leadops.A02LeadOpsFollowRequest;
import com.huodaizi.backend.dto.leadops.A02LeadOpsItemDTO;
import com.huodaizi.backend.dto.leadops.A02LeadOpsListRequest;
import com.huodaizi.backend.dto.leadops.A02LeadOpsListResponse;
import com.huodaizi.backend.dto.leadops.A02LeadOpsOverviewDTO;
import com.huodaizi.backend.dto.leadops.A02LeadOpsSource;
import com.huodaizi.backend.dto.leadops.A02LeadOpsStatusUpdateRequest;
import com.huodaizi.backend.repository.inquiry.InMemoryInquiryRepository;
import com.huodaizi.backend.repository.inquiry.InquiryMerchantLeadEntity;
import com.huodaizi.backend.repository.siteadlead.InMemorySiteAdLeadRepository;
import com.huodaizi.backend.repository.siteadlead.SiteAdLeadEntity;
import java.util.List;
import java.util.Locale;
import java.util.stream.Stream;
import org.springframework.stereotype.Repository;

@Repository
public class A02LeadOpsAggregateRepository {

  private final InMemoryInquiryRepository inquiryRepository;
  private final InMemorySiteAdLeadRepository siteAdLeadRepository;

  public A02LeadOpsAggregateRepository(
      InMemoryInquiryRepository inquiryRepository, InMemorySiteAdLeadRepository siteAdLeadRepository) {
    this.inquiryRepository = inquiryRepository;
    this.siteAdLeadRepository = siteAdLeadRepository;
  }

  public A02LeadOpsListResponse list(A02LeadOpsListRequest request) {
    String sourceFilter = normalizeSource(request.source());
    String statusFilter = normalize(request.status());
    String keyword = normalize(request.keyword());
    String owner = normalize(request.owner());
    String city = normalize(request.city());

    List<A02LeadOpsItemDTO> inquiryItems =
        inquiryRepository.allMerchantLeads().stream()
            .map(this::toInquiryItem)
            .filter(item -> sourceFilter == null || "INQUIRY".equals(sourceFilter))
            .filter(item -> statusFilter == null || normalize(item.status()).equals(statusFilter))
            .filter(item -> city == null || normalize(item.city()).contains(city))
            .filter(item -> owner == null || normalize(item.owner()).contains(owner))
            .filter(item -> keyword == null || containsKeyword(item, keyword))
            .toList();

    List<A02LeadOpsItemDTO> siteAdItems =
        siteAdLeadRepository.allLeads().stream()
            .map(this::toSiteAdItem)
            .filter(item -> sourceFilter == null || "SITE_AD".equals(sourceFilter))
            .filter(item -> statusFilter == null || normalize(item.status()).equals(statusFilter))
            .filter(item -> city == null || normalize(item.city()).contains(city))
            .filter(item -> owner == null || normalize(item.owner()).contains(owner))
            .filter(item -> keyword == null || containsKeyword(item, keyword))
            .toList();

    List<A02LeadOpsItemDTO> merged =
        Stream.concat(inquiryItems.stream(), siteAdItems.stream())
            .sorted((a, b) -> defaultText(b.updatedAt()).compareTo(defaultText(a.updatedAt())))
            .toList();

    int page = request.safePage();
    int pageSize = request.safePageSize();
    int from = Math.max((page - 1) * pageSize, 0);
    int to = Math.min(from + pageSize, merged.size());
    List<A02LeadOpsItemDTO> paged = from >= merged.size() ? List.of() : merged.subList(from, to);

    int newCount = countStatus(merged, "NEW");
    int followingCount = countStatus(merged, "FOLLOWING");
    int quotedOrProposalCount = countStatus(merged, "QUOTED");
    int wonOrConvertedCount = countStatus(merged, "WON");
    int closedOrLostCount = countStatus(merged, "CLOSED");

    return new A02LeadOpsListResponse(
        sourceFilter == null ? "ALL" : sourceFilter,
        new A02LeadOpsOverviewDTO(
            merged.size(),
            inquiryItems.size(),
            siteAdItems.size(),
            newCount,
            followingCount,
            quotedOrProposalCount,
            wonOrConvertedCount,
            closedOrLostCount),
        paged,
        merged.size(),
        page,
        pageSize);
  }

  public A02LeadOpsItemDTO detail(String source, String leadId) {
    return switch (parseSource(source)) {
      case INQUIRY -> toInquiryItem(inquiryRepository.getMerchantLeadById(leadId));
      case SITE_AD -> toSiteAdItem(siteAdLeadRepository.getById(leadId));
      case ALL -> throw new BaseException(ErrorCode.BAD_REQUEST.getCode(), "detail 不支持 source=ALL");
    };
  }

  public A02LeadOpsItemDTO assign(String source, String leadId, A02LeadOpsAssignRequest request) {
    return switch (parseSource(source)) {
      case INQUIRY -> {
        InquiryMerchantLeadEntity lead = inquiryRepository.getMerchantLeadById(leadId);
        lead.assignOwner(request.ownerName());
        if (lead.getStatus() == InquiryMerchantLeadStatus.NEW) {
          lead.updateStatus(
              InquiryMerchantLeadStatus.FOLLOWING, defaultText(request.comment(), "A02分配负责人"));
        } else if (request.comment() != null && !request.comment().isBlank()) {
          lead.updateStatus(lead.getStatus(), request.comment());
        }
        yield toInquiryItem(lead);
      }
      case SITE_AD ->
          toSiteAdItem(
              siteAdLeadRepository.adminAssign(
                  leadId,
                  new com.huodaizi.backend.dto.siteadlead.SiteAdLeadAdminAssignRequest(
                      request.ownerName(), request.team(), request.comment())));
      case ALL -> throw new BaseException(ErrorCode.BAD_REQUEST.getCode(), "assign 不支持 source=ALL");
    };
  }

  public A02LeadOpsItemDTO updateStatus(
      String source, String leadId, A02LeadOpsStatusUpdateRequest request) {
    return switch (parseSource(source)) {
      case INQUIRY -> {
        InquiryMerchantLeadEntity lead = inquiryRepository.getMerchantLeadById(leadId);
        lead.updateStatus(parseInquiryStatus(request.status()), request.comment());
        yield toInquiryItem(lead);
      }
      case SITE_AD ->
          toSiteAdItem(
              siteAdLeadRepository.adminUpdateStatus(
                  leadId,
                  new com.huodaizi.backend.dto.siteadlead.SiteAdLeadAdminUpdateRequest(
                      request.status(), request.operator(), request.comment())));
      case ALL -> throw new BaseException(ErrorCode.BAD_REQUEST.getCode(), "status 不支持 source=ALL");
    };
  }

  public A02LeadOpsItemDTO follow(String source, String leadId, A02LeadOpsFollowRequest request) {
    return switch (parseSource(source)) {
      case INQUIRY -> {
        InquiryMerchantLeadEntity lead = inquiryRepository.getMerchantLeadById(leadId);
        String content = defaultText(request.content());
        if (request.nextActionAt() != null && !request.nextActionAt().isBlank()) {
          content = content + " | 下次跟进:" + request.nextActionAt().trim();
        }
        lead.updateStatus(InquiryMerchantLeadStatus.FOLLOWING, content);
        yield toInquiryItem(lead);
      }
      case SITE_AD ->
          toSiteAdItem(
              siteAdLeadRepository.adminAppendFollow(
                  leadId,
                  new com.huodaizi.backend.dto.siteadlead.SiteAdLeadAdminFollowRequest(
                      request.content(), request.nextActionAt(), request.operator())));
      case ALL -> throw new BaseException(ErrorCode.BAD_REQUEST.getCode(), "follow 不支持 source=ALL");
    };
  }

  public A02LeadOpsItemDTO quickQuote(
      String source, String leadId, InquiryMerchantLeadQuoteRequest request) {
    if (parseSource(source) != A02LeadOpsSource.INQUIRY) {
      throw new BaseException(ErrorCode.BAD_REQUEST.getCode(), "仅询价线索支持快捷报价");
    }
    return toInquiryItem(inquiryRepository.merchantLeadQuote(leadId, request));
  }

  private A02LeadOpsItemDTO toInquiryItem(InquiryMerchantLeadEntity lead) {
    String follow = defaultText(lead.getQuoteRemark(), "待跟进");
    return new A02LeadOpsItemDTO(
        "INQUIRY",
        lead.getId(),
        lead.getLeadNo(),
        mapInquiryStatus(lead.getStatus()),
        inquiryStatusText(mapInquiryStatus(lead.getStatus())),
        defaultText(lead.getOwner(), lead.getMerchantName()),
        lead.getBuyerCompany(),
        lead.getContactNameMasked(),
        lead.getContactMobileMasked(),
        lead.getDeliveryCity(),
        lead.getSpecText(),
        defaultText(lead.getDemandQtyTon(), "-"),
        follow,
        "",
        lead.getCreatedAt().toString(),
        lead.getUpdatedAt().toString());
  }

  private A02LeadOpsItemDTO toSiteAdItem(SiteAdLeadEntity lead) {
    String latestFollow =
        lead.getFollowLogs().isEmpty() ? "待跟进" : defaultText(lead.getFollowLogs().get(0).getContent(), "待跟进");
    return new A02LeadOpsItemDTO(
        "SITE_AD",
        lead.getId(),
        lead.getLeadNo(),
        mapAdStatus(lead.getStatus()),
        adStatusText(mapAdStatus(lead.getStatus())),
        defaultText(lead.getOwner(), "待分配"),
        lead.getCompanyName(),
        maskName(lead.getContactName()),
        maskPhone(lead.getContactPhone()),
        lead.getCity(),
        lead.getPlacementName(),
        defaultText(lead.getBudget(), "-"),
        latestFollow,
        defaultText(lead.getNextFollowAt(), ""),
        lead.getCreatedAt().toString(),
        lead.getUpdatedAt().toString());
  }

  private int countStatus(List<A02LeadOpsItemDTO> items, String status) {
    return (int) items.stream().filter(item -> status.equalsIgnoreCase(item.status())).count();
  }

  private A02LeadOpsSource parseSource(String source) {
    String normalized = normalizeSource(source);
    if (normalized == null) {
      throw new BaseException(ErrorCode.BAD_REQUEST.getCode(), "source 不能为空");
    }
    return A02LeadOpsSource.valueOf(normalized);
  }

  private InquiryMerchantLeadStatus parseInquiryStatus(String status) {
    String normalized = normalize(status);
    if (normalized == null) {
      throw new BaseException(ErrorCode.BAD_REQUEST.getCode(), "status 不能为空");
    }
    return switch (normalized) {
      case "new" -> InquiryMerchantLeadStatus.NEW;
      case "following", "contacted" -> InquiryMerchantLeadStatus.FOLLOWING;
      case "quoted" -> InquiryMerchantLeadStatus.QUOTED;
      case "won" -> InquiryMerchantLeadStatus.WON;
      case "lost" -> InquiryMerchantLeadStatus.LOST;
      case "closed" -> InquiryMerchantLeadStatus.CLOSED;
      default -> throw new BaseException(ErrorCode.BAD_REQUEST.getCode(), "询价线索status不支持");
    };
  }

  private String mapInquiryStatus(InquiryMerchantLeadStatus status) {
    return switch (status) {
      case NEW -> "NEW";
      case FOLLOWING, CONTACTED -> "FOLLOWING";
      case QUOTED -> "QUOTED";
      case WON -> "WON";
      case LOST, CLOSED -> "CLOSED";
    };
  }

  private String mapAdStatus(com.huodaizi.backend.dto.siteadlead.SiteAdLeadStatus status) {
    return switch (status) {
      case SUBMITTED -> "NEW";
      case ASSIGNED, CONTACTED -> "FOLLOWING";
      case PROPOSAL_SENT -> "QUOTED";
      case CONVERTED -> "WON";
      case CLOSED -> "CLOSED";
    };
  }

  private String inquiryStatusText(String status) {
    return switch (status) {
      case "NEW" -> "新线索";
      case "FOLLOWING" -> "跟进中";
      case "QUOTED" -> "已报价";
      case "WON" -> "已赢单";
      case "CLOSED" -> "已关闭";
      default -> "未知";
    };
  }

  private String adStatusText(String status) {
    return switch (status) {
      case "NEW" -> "已提交";
      case "FOLLOWING" -> "跟进中";
      case "QUOTED" -> "已提案";
      case "WON" -> "已成交";
      case "CLOSED" -> "已关闭";
      default -> "未知";
    };
  }

  private String normalizeSource(String source) {
    if (source == null || source.isBlank() || "ALL".equalsIgnoreCase(source.trim())) {
      return null;
    }
    return source.trim().toUpperCase(Locale.ROOT);
  }

  private String normalize(String text) {
    if (text == null || text.isBlank()) {
      return null;
    }
    return text.trim().toLowerCase(Locale.ROOT);
  }

  private boolean containsKeyword(A02LeadOpsItemDTO item, String keyword) {
    return normalize(item.leadNo()).contains(keyword)
        || normalize(item.specOrPlacementName()).contains(keyword)
        || normalize(item.companyName()).contains(keyword)
        || normalize(item.city()).contains(keyword);
  }

  private String defaultText(String value) {
    return value == null ? "" : value.trim();
  }

  private String defaultText(String value, String fallback) {
    String text = defaultText(value);
    return text.isBlank() ? fallback : text;
  }

  private String maskName(String name) {
    if (name == null || name.isBlank()) {
      return "未公开";
    }
    String trimmed = name.trim();
    if (trimmed.length() <= 1) {
      return "*";
    }
    return trimmed.substring(0, 1) + "**";
  }

  private String maskPhone(String phone) {
    if (phone == null || phone.isBlank()) {
      return "未公开";
    }
    String digits = phone.replaceAll("\\D", "");
    if (digits.length() < 7) {
      return "***";
    }
    return digits.substring(0, 3) + "****" + digits.substring(digits.length() - 4);
  }
}
