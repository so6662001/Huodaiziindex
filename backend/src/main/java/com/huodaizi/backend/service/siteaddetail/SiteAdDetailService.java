package com.huodaizi.backend.service.siteaddetail;

import com.huodaizi.backend.common.BaseException;
import com.huodaizi.backend.common.ErrorCode;
import com.huodaizi.backend.dto.siteaddetail.SiteAdDetailAdminCreateRequest;
import com.huodaizi.backend.dto.siteaddetail.SiteAdDetailAdminItemDTO;
import com.huodaizi.backend.dto.siteaddetail.SiteAdDetailAdminUpdateRequest;
import com.huodaizi.backend.dto.siteaddetail.SiteAdDetailBenefitDTO;
import com.huodaizi.backend.dto.siteaddetail.SiteAdDetailFaqDTO;
import com.huodaizi.backend.dto.siteaddetail.SiteAdDetailLeadSubmitRequest;
import com.huodaizi.backend.dto.siteaddetail.SiteAdDetailLeadSubmitResponse;
import com.huodaizi.backend.dto.siteaddetail.SiteAdDetailOverviewResponse;
import com.huodaizi.backend.dto.siteaddetail.SiteAdDetailPlacementDTO;
import com.huodaizi.backend.dto.siteaddetail.SiteAdDetailSectionType;
import com.huodaizi.backend.repository.siteaddetail.InMemorySiteAdDetailRepository;
import com.huodaizi.backend.repository.siteaddetail.SiteAdDetailEntity;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class SiteAdDetailService {

  private final InMemorySiteAdDetailRepository repository;

  public SiteAdDetailService(InMemorySiteAdDetailRepository repository) {
    this.repository = repository;
  }

  public List<SiteAdDetailPlacementDTO> products() {
    return repository.onlinePlacements().stream().map(this::toPlacement).toList();
  }

  public SiteAdDetailOverviewResponse detail(String placementId) {
    String normalizedId = normalizePlacementId(placementId);
    SiteAdDetailEntity placement =
        repository.onlineByPlacementIdAndSection(normalizedId, SiteAdDetailSectionType.PLACEMENT);

    List<SiteAdDetailBenefitDTO> benefits =
        repository.listOnlineByPlacementIdAndSection(normalizedId, SiteAdDetailSectionType.BENEFIT).stream()
            .map(item -> new SiteAdDetailBenefitDTO(item.getTitle(), item.getContent()))
            .toList();

    List<SiteAdDetailFaqDTO> faq =
        repository.listOnlineByPlacementIdAndSection(normalizedId, SiteAdDetailSectionType.FAQ).stream()
            .map(item -> new SiteAdDetailFaqDTO(item.getTitle(), item.getContent()))
            .toList();

    return new SiteAdDetailOverviewResponse(toPlacement(placement), benefits, faq);
  }

  public SiteAdDetailLeadSubmitResponse submitLead(SiteAdDetailLeadSubmitRequest request) {
    validateLeadRequest(request);
    String normalizedId = normalizePlacementId(request.placementId());
    SiteAdDetailEntity placement =
        repository.onlineByPlacementIdAndSection(normalizedId, SiteAdDetailSectionType.PLACEMENT);
    SiteAdDetailEntity lead = repository.submitLead(normalizedId, request);
    return new SiteAdDetailLeadSubmitResponse(
        lead.getId(),
        normalizedId,
        placement.getTitle(),
        request.city(),
        maskName(request.contactName()),
        maskPhone(request.contactPhone()),
        "SUBMITTED",
        lead.getUpdatedAt().toString());
  }

  public List<SiteAdDetailAdminItemDTO> adminList(
      String placementId, SiteAdDetailSectionType section) {
    String normalizedId = normalizePlacementId(placementId);
    return repository.adminListByPlacementIdAndSection(normalizedId, section).stream()
        .map(this::toAdminItem)
        .toList();
  }

  public SiteAdDetailAdminItemDTO adminCreate(
      String placementId, SiteAdDetailSectionType section, SiteAdDetailAdminCreateRequest request) {
    String normalizedId = normalizePlacementId(placementId);
    return toAdminItem(repository.adminCreate(normalizedId, section, request));
  }

  public SiteAdDetailAdminItemDTO adminUpdate(
      String placementId, SiteAdDetailSectionType section, String id, SiteAdDetailAdminUpdateRequest request) {
    String normalizedId = normalizePlacementId(placementId);
    return toAdminItem(repository.adminUpdate(normalizedId, section, id, request));
  }

  public SiteAdDetailAdminItemDTO adminChangeStatus(
      String placementId, SiteAdDetailSectionType section, String id, String status) {
    String normalizedId = normalizePlacementId(placementId);
    return toAdminItem(repository.adminChangeStatus(normalizedId, section, id, status));
  }

  public SiteAdDetailAdminItemDTO adminPin(
      String placementId, SiteAdDetailSectionType section, String id, Boolean pinned) {
    if (pinned == null) {
      throw new BaseException(ErrorCode.BAD_REQUEST.getCode(), "pinned 不能为空");
    }
    String normalizedId = normalizePlacementId(placementId);
    return toAdminItem(repository.adminPin(normalizedId, section, id, pinned));
  }

  public void adminDelete(String placementId, SiteAdDetailSectionType section, String id) {
    String normalizedId = normalizePlacementId(placementId);
    repository.adminDelete(normalizedId, section, id);
  }

  private SiteAdDetailPlacementDTO toPlacement(SiteAdDetailEntity placement) {
    return new SiteAdDetailPlacementDTO(
        placement.getPlacementId(),
        placement.getTitle(),
        placement.getValue(),
        placement.getContent(),
        placement.getExtra(),
        placement.getLink(),
        placement.getStatus(),
        placement.isPinned(),
        placement.getUpdatedAt().toString());
  }

  private SiteAdDetailAdminItemDTO toAdminItem(SiteAdDetailEntity item) {
    return new SiteAdDetailAdminItemDTO(
        item.getId(),
        item.getPlacementId(),
        item.getSectionType().name(),
        item.getTitle(),
        item.getValue(),
        item.getContent(),
        item.getExtra(),
        item.getLink(),
        item.getStatus(),
        item.isPinned(),
        item.getUpdatedAt().toString());
  }

  private void validateLeadRequest(SiteAdDetailLeadSubmitRequest request) {
    if (request.companyName() == null || request.companyName().trim().length() < 4) {
      throw new BaseException(ErrorCode.BAD_REQUEST.getCode(), "公司名称至少4个字符");
    }
    if (request.contactName() == null || request.contactName().trim().length() < 2) {
      throw new BaseException(ErrorCode.BAD_REQUEST.getCode(), "联系人至少2个字符");
    }
    if (request.contactPhone() == null || !request.contactPhone().trim().matches("^1\\d{10}$")) {
      throw new BaseException(ErrorCode.BAD_REQUEST.getCode(), "手机号格式不正确");
    }
    if (request.agreed() == null || !request.agreed()) {
      throw new BaseException(ErrorCode.BAD_REQUEST.getCode(), "请先同意广告投放服务协议");
    }
  }

  private String normalizePlacementId(String placementId) {
    if (placementId == null || placementId.isBlank()) {
      throw new BaseException(ErrorCode.BAD_REQUEST.getCode(), "placementId 不能为空");
    }
    return placementId.trim().toUpperCase();
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
}
