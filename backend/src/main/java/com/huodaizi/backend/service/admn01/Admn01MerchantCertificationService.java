package com.huodaizi.backend.service.admn01;

import com.huodaizi.backend.common.BaseException;
import com.huodaizi.backend.common.ErrorCode;
import com.huodaizi.backend.dto.admn01.Admn01MerchantCertificationDetailResponse;
import com.huodaizi.backend.dto.admn01.Admn01MerchantCertificationItemDTO;
import com.huodaizi.backend.dto.admn01.Admn01MerchantCertificationListRequest;
import com.huodaizi.backend.dto.admn01.Admn01MerchantCertificationListResponse;
import com.huodaizi.backend.dto.admn01.Admn01MerchantCertificationReviewRequest;
import com.huodaizi.backend.repository.auth.EnterpriseCertificationEntity;
import com.huodaizi.backend.repository.auth.InMemoryAuthRepository;
import java.util.List;
import java.util.Locale;
import org.springframework.stereotype.Service;

@Service
public class Admn01MerchantCertificationService {
  private final InMemoryAuthRepository repository;

  public Admn01MerchantCertificationService(InMemoryAuthRepository repository) {
    this.repository = repository;
  }

  public Admn01MerchantCertificationListResponse list(Admn01MerchantCertificationListRequest request) {
    int page = request == null ? 1 : request.safePage();
    int pageSize = request == null ? 10 : request.safePageSize();
    List<EnterpriseCertificationEntity> all =
        repository.listAllCertifications(
            request == null ? null : request.status(),
            request == null ? null : request.keyword(),
            page,
            pageSize);
    int from = Math.min((page - 1) * pageSize, all.size());
    int to = Math.min(from + pageSize, all.size());
    List<Admn01MerchantCertificationItemDTO> records =
        all.subList(from, to).stream().map(this::toItem).toList();
    return new Admn01MerchantCertificationListResponse(
        all.size(),
        page,
        pageSize,
        request == null ? "" : safeText(request.status()),
        request == null ? "" : safeText(request.keyword()),
        countByStatus(all, "PENDING_REVIEW"),
        countByStatus(all, "APPROVED"),
        countByStatus(all, "REJECTED"),
        records);
  }

  public Admn01MerchantCertificationDetailResponse detail(String certificationId) {
    EnterpriseCertificationEntity entity = repository.getCertificationById(certificationId);
    return toDetail(entity);
  }

  public Admn01MerchantCertificationDetailResponse review(
      String certificationId, Admn01MerchantCertificationReviewRequest request) {
    String action = safeText(request.action()).toUpperCase(Locale.ROOT);
    if (!"APPROVE".equals(action) && !"REJECT".equals(action)) {
      throw new BaseException(ErrorCode.BAD_REQUEST.getCode(), "action 仅支持 APPROVE/REJECT");
    }
    EnterpriseCertificationEntity entity =
        repository.reviewCertification(
            certificationId, action, request.reviewRemark(), request.reviewer());
    return toDetail(entity);
  }

  private int countByStatus(List<EnterpriseCertificationEntity> all, String status) {
    return (int) all.stream().filter(item -> status.equalsIgnoreCase(item.getStatus())).count();
  }

  private Admn01MerchantCertificationItemDTO toItem(EnterpriseCertificationEntity entity) {
    return new Admn01MerchantCertificationItemDTO(
        entity.getCertificationId(),
        entity.getUserId(),
        entity.getCompanyName(),
        entity.getUnifiedSocialCreditCode(),
        entity.getContactName(),
        entity.getContactMobileMasked(),
        safeText(entity.getProvince()),
        safeText(entity.getCity()),
        entity.getStatus(),
        statusText(entity.getStatus()),
        riskTag(entity.getStatus()),
        safeText(entity.getOperator()),
        toText(entity.getSubmittedAt()),
        toText(entity.getUpdatedAt()));
  }

  private Admn01MerchantCertificationDetailResponse toDetail(EnterpriseCertificationEntity entity) {
    return new Admn01MerchantCertificationDetailResponse(
        entity.getCertificationId(),
        entity.getUserId(),
        entity.getAccount(),
        entity.getCompanyName(),
        entity.getUnifiedSocialCreditCode(),
        entity.getLegalPersonName(),
        maskIdNo(entity.getLegalPersonIdNo()),
        entity.getContactName(),
        entity.getContactMobileMasked(),
        entity.getBusinessLicenseUrl(),
        entity.getLegalIdFrontUrl(),
        entity.getLegalIdBackUrl(),
        entity.getBankAccountName(),
        maskBankAccount(entity.getBankAccountNo()),
        entity.getBankName(),
        entity.getProvince(),
        entity.getCity(),
        entity.getAddress(),
        entity.getStatus(),
        statusText(entity.getStatus()),
        safeText(entity.getRemark()),
        safeText(entity.getOperator()),
        toText(entity.getSubmittedAt()),
        toText(entity.getUpdatedAt()),
        availableActions(entity.getStatus()));
  }

  private String statusText(String status) {
    return switch (safeText(status).toUpperCase()) {
      case "PENDING_REVIEW" -> "待审核";
      case "APPROVED" -> "已通过";
      case "REJECTED" -> "已驳回";
      case "UNSUBMITTED" -> "未提交";
      default -> "处理中";
    };
  }

  private String safeText(String value) {
    return value == null ? "" : value.trim();
  }

  private String toText(java.time.LocalDateTime value) {
    return value == null ? "" : value.toString();
  }

  private String riskTag(String status) {
    return switch (safeText(status).toUpperCase(Locale.ROOT)) {
      case "PENDING_REVIEW" -> "待核验";
      case "REJECTED" -> "需补件";
      case "APPROVED" -> "低风险";
      default -> "未提交";
    };
  }

  private List<String> availableActions(String status) {
    return switch (safeText(status).toUpperCase(Locale.ROOT)) {
      case "PENDING_REVIEW" -> List.of("APPROVE", "REJECT");
      case "REJECTED" -> List.of("APPROVE");
      case "APPROVED" -> List.of("REJECT");
      default -> List.of();
    };
  }

  private String maskIdNo(String idNo) {
    String raw = safeText(idNo).toUpperCase();
    if (raw.length() <= 8) {
      return raw;
    }
    return raw.substring(0, 4) + "********" + raw.substring(raw.length() - 4);
  }

  private String maskBankAccount(String bankAccountNo) {
    String raw = safeText(bankAccountNo);
    if (raw.length() <= 8) {
      return raw;
    }
    return raw.substring(0, 4) + "********" + raw.substring(raw.length() - 4);
  }
}
