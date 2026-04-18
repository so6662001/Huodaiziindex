package com.huodaizi.backend.service.sitead;

import com.huodaizi.backend.common.BaseException;
import com.huodaizi.backend.common.ErrorCode;
import com.huodaizi.backend.dto.sitead.SiteAdAdminCreateRequest;
import com.huodaizi.backend.dto.sitead.SiteAdAdminItemDTO;
import com.huodaizi.backend.dto.sitead.SiteAdAdminUpdateRequest;
import com.huodaizi.backend.dto.sitead.SiteAdOptionsResponse;
import com.huodaizi.backend.dto.sitead.SiteAdProductDTO;
import com.huodaizi.backend.dto.sitead.SiteAdPublishRequest;
import com.huodaizi.backend.dto.sitead.SiteAdSectionType;
import com.huodaizi.backend.dto.sitead.SiteAdSubmitResponse;
import com.huodaizi.backend.repository.sitead.InMemorySiteAdRepository;
import com.huodaizi.backend.repository.sitead.SiteAdEntity;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class SiteAdService {

  private final InMemorySiteAdRepository repository;

  public SiteAdService(InMemorySiteAdRepository repository) {
    this.repository = repository;
  }

  public List<SiteAdProductDTO> products() {
    return repository.listOnlineBySection(SiteAdSectionType.PRODUCT).stream().map(this::toProduct).toList();
  }

  public SiteAdOptionsResponse options() {
    return repository.options();
  }

  public SiteAdSubmitResponse publish(SiteAdPublishRequest request) {
    validatePublishRequest(request);
    SiteAdEntity entity = repository.submitDemand(request);
    String demandNo = repository.generateDemandNo(entity.getId());
    return new SiteAdSubmitResponse(
        entity.getId(),
        demandNo,
        "投放需求已提交，商务顾问将尽快与您联系",
        entity.getCity(),
        entity.getPlacement(),
        entity.getDuration(),
        entity.getCompanyName(),
        maskName(entity.getContactName()),
        maskPhone(entity.getPhone()),
        entity.getStatus(),
        entity.getUpdatedAt().toString());
  }

  public SiteAdSubmitResponse submit(SiteAdPublishRequest request) {
    return publish(request);
  }

  public List<SiteAdAdminItemDTO> adminList(SiteAdSectionType section) {
    return repository.adminListBySection(section).stream().map(this::toAdminItem).toList();
  }

  public SiteAdAdminItemDTO adminCreate(SiteAdSectionType section, SiteAdAdminCreateRequest request) {
    return toAdminItem(repository.adminCreate(section, request));
  }

  public SiteAdAdminItemDTO adminUpdate(
      SiteAdSectionType section, String id, SiteAdAdminUpdateRequest request) {
    return toAdminItem(repository.adminUpdate(section, id, request));
  }

  public SiteAdAdminItemDTO adminChangeStatus(SiteAdSectionType section, String id, String status) {
    return toAdminItem(repository.adminChangeStatus(section, id, status));
  }

  public SiteAdAdminItemDTO adminPin(SiteAdSectionType section, String id, Boolean pinned) {
    if (pinned == null) {
      throw new BaseException(ErrorCode.BAD_REQUEST.getCode(), "pinned 不能为空");
    }
    return toAdminItem(repository.adminPin(section, id, pinned));
  }

  public void adminDelete(SiteAdSectionType section, String id) {
    repository.adminDelete(section, id);
  }

  private void validatePublishRequest(SiteAdPublishRequest request) {
    if (request.companyName() == null || request.companyName().trim().length() < 4) {
      throw new BaseException(ErrorCode.BAD_REQUEST.getCode(), "公司名称至少4个字符");
    }
    if (request.contactName() == null || request.contactName().trim().length() < 2) {
      throw new BaseException(ErrorCode.BAD_REQUEST.getCode(), "联系人至少2个字符");
    }
    if (request.phone() == null || !request.phone().trim().matches("^1\\d{10}$")) {
      throw new BaseException(ErrorCode.BAD_REQUEST.getCode(), "手机号格式不正确");
    }
    if (request.agreed() == null || !request.agreed()) {
      throw new BaseException(ErrorCode.BAD_REQUEST.getCode(), "请先同意广告投放服务协议");
    }
  }

  private SiteAdProductDTO toProduct(SiteAdEntity entity) {
    return new SiteAdProductDTO(entity.getTitle(), entity.getValue(), entity.getSubtitle(), entity.getLink());
  }

  private SiteAdAdminItemDTO toAdminItem(SiteAdEntity entity) {
    return new SiteAdAdminItemDTO(
        entity.getId(),
        entity.getSectionType().name(),
        entity.getCity(),
        entity.getPlacement(),
        entity.getTitle(),
        entity.getSubtitle(),
        entity.getValue(),
        entity.getExtra(),
        entity.getLink(),
        entity.getDuration(),
        entity.getCompanyName(),
        maskName(entity.getContactName()),
        maskPhone(entity.getPhone()),
        entity.getBudget(),
        entity.getRemark(),
        entity.getStatus(),
        entity.isPinned(),
        entity.getUpdatedAt().toString());
  }

  private String maskPhone(String phone) {
    if (phone == null || phone.isBlank()) {
      return "-";
    }
    String digits = phone.replaceAll("\\D", "");
    if (digits.length() < 7) {
      return "***";
    }
    return digits.substring(0, 3) + "****" + digits.substring(digits.length() - 4);
  }

  private String maskName(String name) {
    if (name == null || name.isBlank()) {
      return "-";
    }
    String trimmed = name.trim();
    if (trimmed.length() == 1) {
      return "*";
    }
    return trimmed.charAt(0) + "*" + trimmed.substring(trimmed.length() - 1);
  }
}
