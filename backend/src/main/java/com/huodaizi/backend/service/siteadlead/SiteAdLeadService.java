package com.huodaizi.backend.service.siteadlead;

import com.huodaizi.backend.common.BaseException;
import com.huodaizi.backend.common.ErrorCode;
import com.huodaizi.backend.dto.siteadlead.SiteAdLeadAdminAssignRequest;
import com.huodaizi.backend.dto.siteadlead.SiteAdLeadAdminFollowRequest;
import com.huodaizi.backend.dto.siteadlead.SiteAdLeadAdminUpdateRequest;
import com.huodaizi.backend.dto.siteadlead.SiteAdLeadFollowLogDTO;
import com.huodaizi.backend.dto.siteadlead.SiteAdLeadItemDTO;
import com.huodaizi.backend.dto.siteadlead.SiteAdLeadListRequest;
import com.huodaizi.backend.dto.siteadlead.SiteAdLeadListResponse;
import com.huodaizi.backend.dto.siteadlead.SiteAdLeadMineRequest;
import com.huodaizi.backend.dto.siteadlead.SiteAdLeadMineResponse;
import com.huodaizi.backend.dto.siteadlead.SiteAdLeadStatus;
import com.huodaizi.backend.dto.siteadlead.SiteAdLeadSubmitRequest;
import com.huodaizi.backend.dto.siteadlead.SiteAdLeadSubmitResponse;
import com.huodaizi.backend.repository.siteadlead.InMemorySiteAdLeadRepository;
import com.huodaizi.backend.repository.siteadlead.SiteAdLeadEntity;
import com.huodaizi.backend.repository.siteadlead.SiteAdLeadFollowEntity;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class SiteAdLeadService {

  private final InMemorySiteAdLeadRepository repository;

  public SiteAdLeadService(InMemorySiteAdLeadRepository repository) {
    this.repository = repository;
  }

  public SiteAdLeadSubmitResponse submit(SiteAdLeadSubmitRequest request) {
    validateSubmit(request);
    SiteAdLeadEntity lead = repository.submit(request);
    return new SiteAdLeadSubmitResponse(
        lead.getId(),
        lead.getLeadNo(),
        lead.getPlacementId(),
        lead.getPlacementName(),
        lead.getStatus().name(),
        "已提交，商务顾问将尽快联系");
  }

  public SiteAdLeadMineResponse mine(SiteAdLeadMineRequest request) {
    List<SiteAdLeadEntity> mine = repository.mine(request);
    List<SiteAdLeadItemDTO> items = mine.stream().map(this::toItem).toList();
    int page = request.safePage();
    int pageSize = request.safePageSize();
    int from = Math.max((page - 1) * pageSize, 0);
    int to = Math.min(from + pageSize, items.size());
    List<SiteAdLeadItemDTO> paged = from >= items.size() ? List.of() : items.subList(from, to);
    return new SiteAdLeadMineResponse(
        request.contactPhone(),
        paged,
        page,
        pageSize,
        items.size(),
        countByStatus(mine, SiteAdLeadStatus.SUBMITTED),
        countByStatus(mine, SiteAdLeadStatus.ASSIGNED),
        countByStatus(mine, SiteAdLeadStatus.CONTACTED),
        countByStatus(mine, SiteAdLeadStatus.PROPOSAL_SENT),
        countByStatus(mine, SiteAdLeadStatus.CONVERTED),
        countByStatus(mine, SiteAdLeadStatus.CLOSED));
  }

  public SiteAdLeadListResponse adminList(SiteAdLeadListRequest request) {
    List<SiteAdLeadEntity> all = repository.adminList(request);
    int page = request.safePage();
    int pageSize = request.safePageSize();
    int from = Math.max((page - 1) * pageSize, 0);
    int to = Math.min(from + pageSize, all.size());
    List<SiteAdLeadEntity> paged = from >= all.size() ? List.of() : all.subList(from, to);
    return new SiteAdLeadListResponse(
        paged.stream().map(this::toItem).toList(),
        all.size(),
        page,
        pageSize);
  }

  public SiteAdLeadItemDTO adminAssign(String id, SiteAdLeadAdminAssignRequest request) {
    if (request.ownerName() == null || request.ownerName().trim().isEmpty()) {
      throw new BaseException(ErrorCode.BAD_REQUEST.getCode(), "ownerName 不能为空");
    }
    return toItem(repository.adminAssign(id, request));
  }

  public SiteAdLeadItemDTO adminUpdateStatus(String id, SiteAdLeadAdminUpdateRequest request) {
    if (request.status() == null) {
      throw new BaseException(ErrorCode.BAD_REQUEST.getCode(), "status 不能为空");
    }
    return toItem(repository.adminUpdateStatus(id, request));
  }

  public SiteAdLeadItemDTO adminAddFollow(String id, SiteAdLeadAdminFollowRequest request) {
    if (request.content() == null || request.content().trim().isEmpty()) {
      throw new BaseException(ErrorCode.BAD_REQUEST.getCode(), "content 不能为空");
    }
    if (request.operator() == null || request.operator().trim().isEmpty()) {
      throw new BaseException(ErrorCode.BAD_REQUEST.getCode(), "operator 不能为空");
    }
    return toItem(repository.adminAppendFollow(id, request));
  }

  public SiteAdLeadItemDTO adminDetail(String id) {
    return toItem(repository.getById(id));
  }

  public List<SiteAdLeadFollowLogDTO> adminFollowLogs(String id) {
    return repository.getById(id).getFollowLogs().stream()
        .sorted((a, b) -> b.getCreatedAt().compareTo(a.getCreatedAt()))
        .map(this::toFollow)
        .toList();
  }

  private SiteAdLeadItemDTO toItem(SiteAdLeadEntity item) {
    return new SiteAdLeadItemDTO(
        item.getId(),
        item.getLeadNo(),
        item.getPlacementId(),
        item.getPlacementName(),
        item.getCity(),
        item.getDuration(),
        item.getBudget(),
        item.getCompanyName(),
        maskName(item.getContactName()),
        maskPhone(item.getContactPhone()),
        item.getRemark(),
        item.getOwner(),
        item.getStatus().name(),
        item.getFollowLogs().isEmpty()
            ? null
            : item.getFollowLogs().get(item.getFollowLogs().size() - 1).getCreatedAt().toString(),
        item.getFollowLogs().stream()
            .sorted((a, b) -> b.getCreatedAt().compareTo(a.getCreatedAt()))
            .map(this::toFollow)
            .toList(),
        item.getUpdatedAt().toString());
  }

  private SiteAdLeadFollowLogDTO toFollow(SiteAdLeadFollowEntity item) {
    return new SiteAdLeadFollowLogDTO(
        item.getContent(),
        item.getOperator(),
        item.getCreatedAt().toString());
  }

  private int countByStatus(List<SiteAdLeadEntity> items, SiteAdLeadStatus status) {
    return (int) items.stream().filter(i -> i.getStatus() == status).count();
  }

  private void validateSubmit(SiteAdLeadSubmitRequest request) {
    if (request.companyName() == null || request.companyName().trim().length() < 4) {
      throw new BaseException(ErrorCode.BAD_REQUEST.getCode(), "companyName 长度需至少4位");
    }
    if (request.contactName() == null || request.contactName().trim().length() < 2) {
      throw new BaseException(ErrorCode.BAD_REQUEST.getCode(), "contactName 长度需至少2位");
    }
    if (request.contactPhone() == null || !request.contactPhone().trim().matches("^1\\d{10}$")) {
      throw new BaseException(ErrorCode.BAD_REQUEST.getCode(), "contactPhone 格式不正确");
    }
    if (request.agreed() == null || !request.agreed()) {
      throw new BaseException(ErrorCode.BAD_REQUEST.getCode(), "请先同意广告投放服务协议");
    }
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
