package com.huodaizi.backend.service.admn11;

import com.huodaizi.backend.dto.admn11.Admn11PaymentRefundDetailResponse;
import com.huodaizi.backend.dto.admn11.Admn11PaymentRefundListItemDTO;
import com.huodaizi.backend.dto.admn11.Admn11PaymentRefundListRequest;
import com.huodaizi.backend.dto.admn11.Admn11PaymentRefundListResponse;
import com.huodaizi.backend.dto.admn11.Admn11PaymentRefundProgressNodeDTO;
import com.huodaizi.backend.dto.admn11.Admn11PaymentRefundReviewRequest;
import com.huodaizi.backend.repository.auth.Admn11PaymentRefundEntity;
import com.huodaizi.backend.repository.auth.InMemoryAuthRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;
import org.springframework.stereotype.Service;

@Service
public class Admn11PaymentRefundAdminService {
  private final InMemoryAuthRepository repository;

  public Admn11PaymentRefundAdminService(InMemoryAuthRepository repository) {
    this.repository = repository;
  }

  public Admn11PaymentRefundListResponse list(Admn11PaymentRefundListRequest request) {
    int page = request == null ? 1 : request.safePage();
    int pageSize = request == null ? 10 : request.safePageSize();
    List<Admn11PaymentRefundEntity> all =
        repository.listPaymentRefundsForAdmin(
            request == null ? null : request.refundStatus(),
            request == null ? null : request.refundReasonCode(),
            request == null ? null : request.payChannel(),
            request == null ? null : request.keyword());
    int from = Math.min((page - 1) * pageSize, all.size());
    int to = Math.min(from + pageSize, all.size());
    List<Admn11PaymentRefundListItemDTO> records = all.subList(from, to).stream().map(this::toListItem).toList();
    int pendingCount =
        (int)
            all.stream()
                .filter(item -> "PENDING_REVIEW".equalsIgnoreCase(safeText(item.getRefundStatus())))
                .count();
    int approvedCount =
        (int)
            all.stream()
                .filter(item -> "APPROVED".equalsIgnoreCase(safeText(item.getRefundStatus())))
                .count();
    int rejectedCount =
        (int)
            all.stream()
                .filter(item -> "REJECTED".equalsIgnoreCase(safeText(item.getRefundStatus())))
                .count();
    int refundedCount =
        (int)
            all.stream()
                .filter(item -> "REFUNDED".equalsIgnoreCase(safeText(item.getRefundStatus())))
                .count();
    return new Admn11PaymentRefundListResponse(
        all.size(),
        page,
        pageSize,
        request == null ? "" : safeText(request.refundStatus()),
        request == null ? "" : safeText(request.refundReasonCode()),
        request == null ? "" : safeText(request.payChannel()),
        request == null ? "" : safeText(request.keyword()),
        pendingCount,
        approvedCount,
        rejectedCount,
        refundedCount,
        records);
  }

  public Admn11PaymentRefundDetailResponse detail(String refundId) {
    return toDetail(repository.getPaymentRefundForAdmin(refundId));
  }

  public Admn11PaymentRefundDetailResponse review(String refundId, Admn11PaymentRefundReviewRequest request) {
    Admn11PaymentRefundEntity entity =
        repository.reviewPaymentRefundForAdmin(
            refundId,
            request.action(),
            request.reviewRemark(),
            request.rejectReason(),
            request.operator());
    repository.appendAuditLogForAdmin(
        "ADMN11",
        "PAYMENT_REFUND_REVIEW",
        "PAYMENT_REFUND",
        entity.getRefundId(),
        safeText(request.operator()),
        "ADMIN",
        "TRACE_ADMN11_REVIEW_" + entity.getRefundId(),
        "SUCCESS",
        repository.admn11AuditRiskLevel(entity.getRefundStatus(), entity.getRefundAmountYuan()),
        "退款审核处理：" + entity.getRefundNo() + " -> " + entity.getRefundStatus(),
        "",
        "action=" + safeText(request.action()) + ", refundAmount=" + entity.getRefundAmountYuan(),
        "127.0.0.1",
        "admn11-service");
    return toDetail(entity);
  }

  public void appendAuditQueryLogForAdmin(String operator, String summary, String traceId, String targetId) {
    repository.appendAuditLogForAdmin(
        "ADMN11",
        "PAYMENT_REFUND_QUERY",
        "PAYMENT_REFUND",
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
        "admn11-service");
  }

  private Admn11PaymentRefundListItemDTO toListItem(Admn11PaymentRefundEntity entity) {
    return new Admn11PaymentRefundListItemDTO(
        entity.getRefundId(),
        entity.getRefundNo(),
        entity.getRefundType(),
        entity.getCashierOrderId(),
        entity.getOrderNo(),
        entity.getBuyerCompany(),
        entity.getSupplierName(),
        entity.getPayChannel(),
        entity.getPayChannelText(),
        entity.getRefundReasonCode(),
        repository.admn11RefundReasonText(entity.getRefundReasonCode()),
        entity.getRefundStatus(),
        repository.admn11RefundStatusText(entity.getRefundStatus()),
        entity.getPayableAmountYuan(),
        entity.getPaidAmountYuan(),
        entity.getRefundAmountYuan(),
        entity.getLatestRemark(),
        toText(entity.getCreatedAt()),
        toText(entity.getUpdatedAt()));
  }

  private Admn11PaymentRefundDetailResponse toDetail(Admn11PaymentRefundEntity entity) {
    List<Admn11PaymentRefundProgressNodeDTO> progressNodes =
        entity.getProgressNodes().stream()
            .map(
                node ->
                    new Admn11PaymentRefundProgressNodeDTO(
                        node.getNodeCode(),
                        node.getNodeName(),
                        node.getStatus(),
                        node.getStatusText(),
                        node.getHandler(),
                        node.getRemark(),
                        node.getHappenedAt()))
            .toList();
    return new Admn11PaymentRefundDetailResponse(
        entity.getRefundId(),
        entity.getRefundNo(),
        entity.getCashierOrderId(),
        entity.getOrderId(),
        entity.getOrderNo(),
        entity.getInquiryNo(),
        entity.getBuyerCompany(),
        entity.getSupplierName(),
        entity.getGoodsName(),
        entity.getRefundType(),
        refundTypeText(entity.getRefundType()),
        entity.getRefundReasonCode(),
        repository.admn11RefundReasonText(entity.getRefundReasonCode()),
        entity.getRefundStatus(),
        repository.admn11RefundStatusText(entity.getRefundStatus()),
        entity.getPayChannel(),
        entity.getPayChannelText(),
        entity.getPayableAmountYuan(),
        entity.getPaidAmountYuan(),
        entity.getRefundAmountYuan(),
        entity.getApprovedAmountYuan(),
        entity.getRefundedAmountYuan(),
        entity.getRemainingAmountYuan(),
        entity.getApplicant(),
        entity.getReviewer(),
        entity.getFinanceOperator(),
        entity.getLatestRemark(),
        entity.getRequestedAt(),
        entity.getReviewedAt(),
        toText(entity.getCreatedAt()),
        toText(entity.getUpdatedAt()),
        entity.getRefundedAt(),
        safeText(entity.getRejectReason()),
        progressNodes,
        availableActions(entity.getRefundStatus()));
  }

  private List<String> availableActions(String refundStatus) {
    return switch (safeText(refundStatus).toUpperCase(Locale.ROOT)) {
      case "PENDING_REVIEW" -> List.of("VIEW", "APPROVE", "REJECT");
      case "APPROVED" -> List.of("VIEW", "CONFIRM_REFUND");
      case "REFUNDED" -> List.of("VIEW", "ARCHIVE");
      case "REJECTED" -> List.of("VIEW", "REOPEN");
      default -> List.of("VIEW");
    };
  }

  private String refundTypeText(String refundType) {
    return switch (safeText(refundType).toUpperCase(Locale.ROOT)) {
      case "FULL" -> "全额退款";
      case "PARTIAL" -> "部分退款";
      default -> "其他退款";
    };
  }

  private String safeText(String text) {
    return text == null ? "" : text.trim();
  }

  private String toText(LocalDateTime value) {
    return value == null ? "" : value.toString();
  }
}
