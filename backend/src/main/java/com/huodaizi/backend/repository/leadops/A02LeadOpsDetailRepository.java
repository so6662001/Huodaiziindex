package com.huodaizi.backend.repository.leadops;

import com.huodaizi.backend.common.BaseException;
import com.huodaizi.backend.common.ErrorCode;
import com.huodaizi.backend.dto.inquiry.InquiryMerchantLeadQuoteRequest;
import com.huodaizi.backend.dto.inquiry.InquiryMerchantLeadStatus;
import com.huodaizi.backend.dto.inquiry.InquiryMerchantLeadStatusUpdateRequest;
import com.huodaizi.backend.dto.leadops.A02LeadOpsAssignRequest;
import com.huodaizi.backend.dto.leadops.A02LeadOpsFollowRequest;
import com.huodaizi.backend.dto.leadops.A02LeadOpsItemDTO;
import com.huodaizi.backend.dto.leadops.A02LeadOpsSource;
import com.huodaizi.backend.dto.leadops.A02LeadOpsStatusUpdateRequest;
import com.huodaizi.backend.repository.inquiry.InMemoryInquiryRepository;
import com.huodaizi.backend.repository.inquiry.InquiryMerchantLeadEntity;
import com.huodaizi.backend.repository.siteadlead.InMemorySiteAdLeadRepository;
import com.huodaizi.backend.repository.siteadlead.SiteAdLeadEntity;
import java.util.Locale;
import org.springframework.stereotype.Repository;

@Repository
public class A02LeadOpsDetailRepository {
  private final InMemoryInquiryRepository inquiryRepository;
  private final InMemorySiteAdLeadRepository siteAdLeadRepository;

  public A02LeadOpsDetailRepository(
      InMemoryInquiryRepository inquiryRepository, InMemorySiteAdLeadRepository siteAdLeadRepository) {
    this.inquiryRepository = inquiryRepository;
    this.siteAdLeadRepository = siteAdLeadRepository;
  }

  public A02LeadOpsItemDTO detail(A02LeadOpsSource source, String leadId) {
    return switch (source) {
      case INQUIRY -> toInquiryItem(inquiryRepository.getMerchantLeadById(leadId));
      case SITE_AD -> toSiteAdItem(siteAdLeadRepository.getById(leadId));
      case ALL -> throw new BaseException(ErrorCode.BAD_REQUEST.getCode(), "detail 不支持 source=ALL");
    };
  }

  public A02LeadOpsItemDTO assign(A02LeadOpsSource source, String leadId, A02LeadOpsAssignRequest request) {
    return switch (source) {
      case INQUIRY -> assignInquiry(leadId, request);
      case SITE_AD -> assignSiteAd(leadId, request);
      case ALL -> throw new BaseException(ErrorCode.BAD_REQUEST.getCode(), "assign 不支持 source=ALL");
    };
  }

  public A02LeadOpsItemDTO updateStatus(
      A02LeadOpsSource source, String leadId, A02LeadOpsStatusUpdateRequest request) {
    return switch (source) {
      case INQUIRY -> updateInquiryStatus(leadId, request);
      case SITE_AD -> updateSiteAdStatus(leadId, request);
      case ALL -> throw new BaseException(ErrorCode.BAD_REQUEST.getCode(), "status 不支持 source=ALL");
    };
  }

  public A02LeadOpsItemDTO follow(A02LeadOpsSource source, String leadId, A02LeadOpsFollowRequest request) {
    return switch (source) {
      case INQUIRY -> followInquiry(leadId, request);
      case SITE_AD -> followSiteAd(leadId, request);
      case ALL -> throw new BaseException(ErrorCode.BAD_REQUEST.getCode(), "follow 不支持 source=ALL");
    };
  }

  public A02LeadOpsItemDTO quickQuote(String source, String leadId, InquiryMerchantLeadQuoteRequest request) {
    if (!"INQUIRY".equalsIgnoreCase(defaultText(source, ""))) {
      throw new BaseException(ErrorCode.BAD_REQUEST.getCode(), "仅询价线索支持快捷报价");
    }
    InquiryMerchantLeadEntity updated = inquiryRepository.merchantLeadQuote(leadId, request);
    return toInquiryItem(updated);
  }

  private A02LeadOpsItemDTO assignInquiry(String leadId, A02LeadOpsAssignRequest request) {
    if (request.ownerName() == null || request.ownerName().isBlank()) {
      throw new BaseException(ErrorCode.BAD_REQUEST.getCode(), "ownerName 不能为空");
    }
    InquiryMerchantLeadEntity lead = inquiryRepository.getMerchantLeadById(leadId);
    lead.assignOwner(request.ownerName());
    if (lead.getStatus() == InquiryMerchantLeadStatus.NEW) {
      lead.updateStatus(InquiryMerchantLeadStatus.FOLLOWING, "");
    }
    String note = defaultText(request.comment(), "已分配负责人");
    lead.updateStatus(lead.getStatus(), "A02分配[" + defaultText(request.operator(), "SYSTEM") + "] " + note);
    return toInquiryItem(lead);
  }

  private A02LeadOpsItemDTO assignSiteAd(String leadId, A02LeadOpsAssignRequest request) {
    SiteAdLeadEntity updated =
        siteAdLeadRepository.adminAssign(
            leadId,
            new com.huodaizi.backend.dto.siteadlead.SiteAdLeadAdminAssignRequest(
                request.ownerName(), request.team(), request.comment()));
    return toSiteAdItem(updated);
  }

  private A02LeadOpsItemDTO updateInquiryStatus(String leadId, A02LeadOpsStatusUpdateRequest request) {
    InquiryMerchantLeadEntity lead = inquiryRepository.getMerchantLeadById(leadId);
    String merchantId = defaultText(lead.getMerchantId(), "S001");
    InquiryMerchantLeadEntity updated =
        inquiryRepository.merchantLeadUpdateStatus(
            leadId,
            new InquiryMerchantLeadStatusUpdateRequest(
                merchantId, request.status(), request.operator(), request.comment()));
    return toInquiryItem(updated);
  }

  private A02LeadOpsItemDTO updateSiteAdStatus(String leadId, A02LeadOpsStatusUpdateRequest request) {
    SiteAdLeadEntity updated =
        siteAdLeadRepository.adminUpdateStatus(
            leadId,
            new com.huodaizi.backend.dto.siteadlead.SiteAdLeadAdminUpdateRequest(
                request.status(), request.operator(), request.comment()));
    return toSiteAdItem(updated);
  }

  private A02LeadOpsItemDTO followInquiry(String leadId, A02LeadOpsFollowRequest request) {
    InquiryMerchantLeadEntity lead = inquiryRepository.getMerchantLeadById(leadId);
    String follow = defaultText(request.content(), "").trim();
    if (follow.isBlank()) {
      throw new BaseException(ErrorCode.BAD_REQUEST.getCode(), "content 不能为空");
    }
    String append = "A02跟进[" + defaultText(request.operator(), "SYSTEM") + "] " + follow;
    if (request.nextActionAt() != null && !request.nextActionAt().isBlank()) {
      append += " -> " + request.nextActionAt().trim();
    }
    lead.updateStatus(InquiryMerchantLeadStatus.FOLLOWING, append);
    return toInquiryItem(lead);
  }

  private A02LeadOpsItemDTO followSiteAd(String leadId, A02LeadOpsFollowRequest request) {
    SiteAdLeadEntity updated =
        siteAdLeadRepository.adminAppendFollow(
            leadId,
            new com.huodaizi.backend.dto.siteadlead.SiteAdLeadAdminFollowRequest(
                request.content(), request.nextActionAt(), request.operator()));
    return toSiteAdItem(updated);
  }

  private A02LeadOpsItemDTO toInquiryItem(InquiryMerchantLeadEntity lead) {
    return new A02LeadOpsItemDTO(
        A02LeadOpsSource.INQUIRY.name(),
        lead.getId(),
        lead.getLeadNo(),
        lead.getStatus().name(),
        mapInquiryStatusText(lead.getStatus().name()),
        defaultText(lead.getOwner(), defaultText(lead.getMerchantName(), "待分配")),
        lead.getBuyerCompany(),
        lead.getContactNameMasked(),
        lead.getContactMobileMasked(),
        lead.getDeliveryCity(),
        lead.getSpecText(),
        lead.getDemandQtyTon() + "吨",
        defaultText(lead.getQuoteRemark(), "待跟进"),
        lead.getExpectedDeliveryAt(),
        lead.getCreatedAt().toString(),
        lead.getUpdatedAt().toString());
  }

  private A02LeadOpsItemDTO toSiteAdItem(SiteAdLeadEntity lead) {
    String latestFollow =
        lead.getFollowLogs().isEmpty() ? "待跟进" : defaultText(lead.getFollowLogs().getLast().getContent(), "待跟进");
    return new A02LeadOpsItemDTO(
        A02LeadOpsSource.SITE_AD.name(),
        lead.getId(),
        lead.getLeadNo(),
        lead.getStatus().name(),
        mapAdStatusText(lead.getStatus().name()),
        defaultText(lead.getOwner(), "待分配"),
        lead.getCompanyName(),
        maskName(lead.getContactName()),
        maskPhone(lead.getContactPhone()),
        lead.getCity(),
        lead.getPlacementName(),
        defaultText(lead.getBudget(), "-"),
        latestFollow,
        defaultText(lead.getNextFollowAt(), "-"),
        lead.getCreatedAt().toString(),
        lead.getUpdatedAt().toString());
  }

  private String mapInquiryStatusText(String status) {
    return switch (defaultText(status, "").trim().toUpperCase(Locale.ROOT)) {
      case "NEW" -> "新线索";
      case "FOLLOWING", "CONTACTED" -> "跟进中";
      case "QUOTED" -> "已报价";
      case "WON" -> "已赢单";
      case "LOST", "CLOSED" -> "已关闭";
      default -> "新线索";
    };
  }

  private String mapAdStatusText(String status) {
    return switch (defaultText(status, "").trim().toUpperCase(Locale.ROOT)) {
      case "SUBMITTED" -> "新线索";
      case "ASSIGNED", "CONTACTED" -> "跟进中";
      case "PROPOSAL_SENT" -> "已提案";
      case "CONVERTED" -> "已成交";
      case "CLOSED" -> "已关闭";
      default -> "新线索";
    };
  }

  private String defaultText(String text, String fallback) {
    return text == null || text.isBlank() ? fallback : text.trim();
  }

  private String maskName(String name) {
    if (name == null || name.isBlank()) {
      return "未公开";
    }
    String trimmed = name.trim();
    if (trimmed.length() == 1) {
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
