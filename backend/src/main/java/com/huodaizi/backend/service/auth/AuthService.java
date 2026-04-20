package com.huodaizi.backend.service.auth;

import com.huodaizi.backend.common.BaseException;
import com.huodaizi.backend.common.ErrorCode;
import com.huodaizi.backend.dto.auth.AuthLoginRequest;
import com.huodaizi.backend.dto.auth.AuthLoginResponse;
import com.huodaizi.backend.dto.auth.AuthRegisterRequest;
import com.huodaizi.backend.dto.auth.AuthRegisterResponse;
import com.huodaizi.backend.dto.auth.AuthSessionResponse;
import com.huodaizi.backend.dto.auth.H5N01QuickLoginRequest;
import com.huodaizi.backend.dto.auth.H5N01QuickLoginResponse;
import com.huodaizi.backend.dto.auth.H5N01SendLoginCodeRequest;
import com.huodaizi.backend.dto.auth.H5N01SendLoginCodeResponse;
import com.huodaizi.backend.dto.auth.H5N03EnterpriseCertificationDetailResponse;
import com.huodaizi.backend.dto.auth.H5N03EnterpriseCertificationSubmitRequest;
import com.huodaizi.backend.dto.auth.H5N04NegotiationDetailResponse;
import com.huodaizi.backend.dto.auth.H5N04NegotiationMessageDTO;
import com.huodaizi.backend.dto.auth.H5N04NegotiationSendMessageRequest;
import com.huodaizi.backend.dto.auth.H5N04NegotiationSessionItemDTO;
import com.huodaizi.backend.dto.auth.H5N04NegotiationSessionListResponse;
import com.huodaizi.backend.dto.auth.H5N04NegotiationStatusUpdateRequest;
import com.huodaizi.backend.dto.auth.H5N05OrderActionDTO;
import com.huodaizi.backend.dto.auth.H5N05OrderDetailResponse;
import com.huodaizi.backend.dto.auth.H5N05OrderListItemDTO;
import com.huodaizi.backend.dto.auth.H5N05OrderListResponse;
import com.huodaizi.backend.dto.auth.H5N05OrderStatusUpdateRequest;
import com.huodaizi.backend.dto.auth.H5N05OrderTimelineNodeDTO;
import com.huodaizi.backend.dto.auth.H5N06PickupOrderDetailResponse;
import com.huodaizi.backend.dto.auth.H5N06PickupOrderListItemDTO;
import com.huodaizi.backend.dto.auth.H5N06PickupOrderListResponse;
import com.huodaizi.backend.dto.auth.H5N06PickupOrderScanRequest;
import com.huodaizi.backend.dto.auth.H5N06PickupOrderStatusUpdateRequest;
import com.huodaizi.backend.dto.auth.H5N07ReconcileDetailResponse;
import com.huodaizi.backend.dto.auth.H5N07ReconcileListItemDTO;
import com.huodaizi.backend.dto.auth.H5N07ReconcileListResponse;
import com.huodaizi.backend.dto.auth.H5N07ReconcileStatusUpdateRequest;
import com.huodaizi.backend.dto.auth.H5N08AfterSaleCreateRequest;
import com.huodaizi.backend.dto.auth.H5N08AfterSaleDetailResponse;
import com.huodaizi.backend.dto.auth.H5N08AfterSaleListItemDTO;
import com.huodaizi.backend.dto.auth.H5N08AfterSaleListResponse;
import com.huodaizi.backend.dto.auth.H5N08AfterSaleStatusUpdateRequest;
import com.huodaizi.backend.dto.auth.H5N09LitePayDetailResponse;
import com.huodaizi.backend.dto.auth.H5N09LitePayListItemDTO;
import com.huodaizi.backend.dto.auth.H5N09LitePayListResponse;
import com.huodaizi.backend.dto.auth.H5N09LitePaySubmitRequest;
import com.huodaizi.backend.dto.auth.H5N10CreditBriefDetailResponse;
import com.huodaizi.backend.dto.auth.H5N10CreditBriefFactorDTO;
import com.huodaizi.backend.dto.auth.H5N10CreditBriefListItemDTO;
import com.huodaizi.backend.dto.auth.H5N10CreditBriefListResponse;
import com.huodaizi.backend.dto.auth.H5N10CreditBriefTrendPointDTO;
import com.huodaizi.backend.dto.auth.N03EnterpriseCertificationDetailResponse;
import com.huodaizi.backend.dto.auth.N03EnterpriseCertificationSubmitRequest;
import com.huodaizi.backend.dto.auth.N04OnboardingProgressNodeDTO;
import com.huodaizi.backend.dto.auth.N04OnboardingProgressResponse;
import com.huodaizi.backend.dto.auth.N05NegotiationDetailResponse;
import com.huodaizi.backend.dto.auth.N05NegotiationMessageDTO;
import com.huodaizi.backend.dto.auth.N05NegotiationSendMessageRequest;
import com.huodaizi.backend.dto.auth.N05NegotiationSessionItemDTO;
import com.huodaizi.backend.dto.auth.N05NegotiationSessionListResponse;
import com.huodaizi.backend.dto.auth.N05NegotiationStatusUpdateRequest;
import com.huodaizi.backend.dto.auth.N06OrderActionDTO;
import com.huodaizi.backend.dto.auth.N06OrderActionRequest;
import com.huodaizi.backend.dto.auth.N06OrderDetailResponse;
import com.huodaizi.backend.dto.auth.N06OrderListItemDTO;
import com.huodaizi.backend.dto.auth.N06OrderListResponse;
import com.huodaizi.backend.dto.auth.N06OrderTimelineNodeDTO;
import com.huodaizi.backend.dto.auth.N07TradeTermAttachmentDTO;
import com.huodaizi.backend.dto.auth.N07TradeTermClauseDTO;
import com.huodaizi.backend.dto.auth.N07TradeTermsConfirmRequest;
import com.huodaizi.backend.dto.auth.N07TradeTermsDetailResponse;
import com.huodaizi.backend.dto.auth.N08AfterSaleDisputeCreateRequest;
import com.huodaizi.backend.dto.auth.N08AfterSaleDisputeDetailResponse;
import com.huodaizi.backend.dto.auth.N08AfterSaleDisputeListItemDTO;
import com.huodaizi.backend.dto.auth.N08AfterSaleDisputeListResponse;
import com.huodaizi.backend.dto.auth.N08AfterSaleDisputeStatusUpdateRequest;
import com.huodaizi.backend.dto.auth.N09AfterSaleProgressDetailResponse;
import com.huodaizi.backend.dto.auth.N09AfterSaleProgressListItemDTO;
import com.huodaizi.backend.dto.auth.N09AfterSaleProgressListResponse;
import com.huodaizi.backend.dto.auth.N09AfterSaleProgressNodeDTO;
import com.huodaizi.backend.dto.auth.N10CashierOrderDetailResponse;
import com.huodaizi.backend.dto.auth.N10CashierOrderListItemDTO;
import com.huodaizi.backend.dto.auth.N10CashierOrderListResponse;
import com.huodaizi.backend.dto.auth.N10CashierPayRequest;
import com.huodaizi.backend.dto.auth.N10CashierTimelineNodeDTO;
import com.huodaizi.backend.dto.auth.N11PaymentResultNodeDTO;
import com.huodaizi.backend.dto.auth.N11PaymentResultResponse;
import com.huodaizi.backend.dto.auth.N12InvoiceApplicationCreateRequest;
import com.huodaizi.backend.dto.auth.N12InvoiceApplicationDetailResponse;
import com.huodaizi.backend.dto.auth.N12InvoiceApplicationListItemDTO;
import com.huodaizi.backend.dto.auth.N12InvoiceApplicationListResponse;
import com.huodaizi.backend.dto.auth.N12InvoiceTitleDTO;
import com.huodaizi.backend.dto.auth.N12InvoiceTitleListResponse;
import com.huodaizi.backend.dto.auth.N12InvoiceTitleSetDefaultRequest;
import com.huodaizi.backend.dto.auth.N12InvoiceTitleSetStatusRequest;
import com.huodaizi.backend.dto.auth.N12InvoiceTitleUpsertRequest;
import com.huodaizi.backend.dto.auth.N13CreditScoreDetailResponse;
import com.huodaizi.backend.dto.auth.N13CreditScoreFactorDTO;
import com.huodaizi.backend.dto.auth.N13CreditScoreListItemDTO;
import com.huodaizi.backend.dto.auth.N13CreditScoreListResponse;
import com.huodaizi.backend.dto.auth.N13CreditScoreTrendPointDTO;
import com.huodaizi.backend.dto.auth.N14DispatchAppealCreateRequest;
import com.huodaizi.backend.dto.auth.N14DispatchAppealDetailResponse;
import com.huodaizi.backend.dto.auth.N14DispatchAppealListItemDTO;
import com.huodaizi.backend.dto.auth.N14DispatchAppealListResponse;
import com.huodaizi.backend.dto.auth.N14DispatchAppealStatusUpdateRequest;
import com.huodaizi.backend.repository.auth.AuthUserEntity;
import com.huodaizi.backend.repository.auth.EnterpriseCertificationDraft;
import com.huodaizi.backend.repository.auth.EnterpriseCertificationEntity;
import com.huodaizi.backend.repository.auth.InMemoryAuthRepository;
import com.huodaizi.backend.repository.auth.InMemoryAuthRepository.AuthRegistration;
import com.huodaizi.backend.repository.auth.InMemoryAuthRepository.SessionEntity;
import com.huodaizi.backend.repository.auth.N05NegotiationMessageEntity;
import com.huodaizi.backend.repository.auth.N05NegotiationQuery;
import com.huodaizi.backend.repository.auth.N05NegotiationSessionEntity;
import com.huodaizi.backend.repository.auth.N06OrderEntity;
import com.huodaizi.backend.repository.auth.N06OrderQuery;
import com.huodaizi.backend.repository.auth.N07TradeTermsEntity;
import com.huodaizi.backend.repository.auth.N08AfterSaleDisputeEntity;
import com.huodaizi.backend.repository.auth.N08AfterSaleQuery;
import com.huodaizi.backend.repository.auth.N10CashierOrderEntity;
import com.huodaizi.backend.repository.auth.N10CashierQuery;
import com.huodaizi.backend.repository.auth.N12InvoiceApplicationEntity;
import com.huodaizi.backend.repository.auth.N12InvoiceApplicationQuery;
import com.huodaizi.backend.repository.auth.N12InvoiceTitleEntity;
import com.huodaizi.backend.repository.auth.N13CreditScoreEntity;
import com.huodaizi.backend.repository.auth.N13CreditScoreQuery;
import com.huodaizi.backend.repository.auth.N14DispatchAppealEntity;
import com.huodaizi.backend.repository.auth.N14DispatchAppealQuery;
import com.huodaizi.backend.dto.inquiry.InquiryPickupOrderStatusUpdateRequest;
import com.huodaizi.backend.repository.inquiry.InMemoryInquiryRepository;
import com.huodaizi.backend.repository.inquiry.InquiryPickupOrderEntity;
import com.huodaizi.backend.repository.inquiry.InquiryReconcileOrderEntity;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
  private final InMemoryAuthRepository repository;
  private final InMemoryInquiryRepository inquiryRepository;

  public AuthService(InMemoryAuthRepository repository, InMemoryInquiryRepository inquiryRepository) {
    this.repository = repository;
    this.inquiryRepository = inquiryRepository;
  }

  public AuthRegisterResponse register(AuthRegisterRequest request) {
    if (!request.password().equals(request.confirmPassword())) {
      throw new BaseException(ErrorCode.BAD_REQUEST.getCode(), "两次输入密码不一致");
    }
    AuthUserEntity user =
        repository.register(
            new AuthRegistration(
                request.accountType(),
                request.mobile(),
                request.password(),
                request.companyName(),
                request.contactName(),
                request.operator()));
    SessionEntity session = repository.createSession(user, "PC");
    return new AuthRegisterResponse(
        user.getUserId(),
        user.getAccount(),
        user.getPhoneMasked(),
        "M-" + user.getUserId(),
        user.getRole(),
        user.getStatus(),
        session.getToken(),
        session.getExpireAt().toString(),
        "注册成功");
  }

  public AuthLoginResponse login(AuthLoginRequest request) {
    AuthUserEntity user = repository.login(request.account(), request.password());
    SessionEntity session = repository.createSession(user, "PC");
    return new AuthLoginResponse(
        user.getUserId(),
        user.getAccount(),
        user.getContactName(),
        user.getRole(),
        session.getToken(),
        session.getExpireAt().toString(),
        session.getCreatedAt().toString());
  }

  public H5N01SendLoginCodeResponse sendH5LoginCode(H5N01SendLoginCodeRequest request) {
    String mobile = request.mobile() == null ? "" : request.mobile().trim();
    if (!mobile.matches("^1\\d{10}$")) {
      throw new BaseException(ErrorCode.BAD_REQUEST.getCode(), "mobile 必须为11位手机号");
    }
    String operator = safeText(request.operator());
    InMemoryAuthRepository.H5LoginCodeEntity code = repository.sendH5LoginCode(mobile, operator);
    return new H5N01SendLoginCodeResponse(
        mobile,
        repository.maskPhone(mobile),
        code.getCodeToken(),
        code.getExpireAt().toString(),
        "验证码已发送");
  }

  public H5N01QuickLoginResponse h5QuickLogin(H5N01QuickLoginRequest request) {
    AuthUserEntity user = repository.quickLoginByCode(safeText(request.mobile()), safeText(request.smsCode()));
    String channel = safeText(request.channel()).isBlank() ? "H5" : safeText(request.channel()).toUpperCase();
    SessionEntity session = repository.createSession(user, channel);
    return new H5N01QuickLoginResponse(
        user.getUserId(),
        user.getAccount(),
        user.getPhoneMasked(),
        user.getContactName(),
        user.getRole(),
        session.getToken(),
        session.getExpireAt().toString(),
        session.getCreatedAt().toString(),
        channel);
  }

  public AuthSessionResponse session(String token) {
    SessionEntity session = repository.requireSession(token);
    AuthUserEntity user =
        repository
            .findByToken(token)
            .orElseThrow(
                () -> new BaseException(ErrorCode.UNAUTHORIZED.getCode(), "登录态无效"));
    return new AuthSessionResponse(
        user.getUserId(),
        user.getAccount(),
        user.getCompanyName(),
        user.getRole(),
        user.getContactName(),
        user.getPhoneMasked(),
        session.getToken(),
        session.getExpireAt().toString());
  }

  public void logout(String token) {
    repository.logout(token);
  }

  public N03EnterpriseCertificationDetailResponse submitEnterpriseCertification(
      String token, N03EnterpriseCertificationSubmitRequest request) {
    validateCertificationRequest(request);
    EnterpriseCertificationEntity saved =
        repository.saveCertification(
            token,
            new EnterpriseCertificationDraft(
                request.companyName().trim(),
                request.unifiedSocialCreditCode().trim().toUpperCase(),
                request.legalPersonName().trim(),
                request.legalPersonIdNo().trim().toUpperCase(),
                request.contactName().trim(),
                request.contactMobile().trim(),
                request.businessLicenseUrl().trim(),
                request.legalIdFrontUrl().trim(),
                request.legalIdBackUrl().trim(),
                safeText(request.bankAccountName()),
                safeText(request.bankAccountNo()),
                safeText(request.bankName()),
                request.province().trim(),
                request.city().trim(),
                request.address().trim(),
                safeText(request.remark()),
                safeText(request.operator())));
    return toCertificationDetail(saved);
  }

  public N03EnterpriseCertificationDetailResponse enterpriseCertificationDetail(String token) {
    AuthUserEntity user =
        repository
            .findUserByToken(token)
            .orElseThrow(() -> new BaseException(ErrorCode.UNAUTHORIZED.getCode(), "登录态无效"));
    EnterpriseCertificationEntity entity = repository.findCertificationByToken(token).orElse(null);
    if (entity == null) {
      return new N03EnterpriseCertificationDetailResponse(
          "",
          user.getUserId(),
          user.getAccount(),
          "UNSUBMITTED",
          user.getCompanyName(),
          "",
          "",
          "",
          user.getContactName(),
          user.getPhoneMasked(),
          "",
          "",
          "",
          "",
          "",
          "",
          "",
          "",
          "",
          "",
          "",
          null,
          null,
          null);
    }
    return toCertificationDetail(entity);
  }

  public H5N03EnterpriseCertificationDetailResponse h5EnterpriseCertificationDetail(String token) {
    AuthUserEntity user =
        repository
            .findUserByToken(token)
            .orElseThrow(() -> new BaseException(ErrorCode.UNAUTHORIZED.getCode(), "登录态无效"));
    EnterpriseCertificationEntity entity = repository.findCertificationByToken(token).orElse(null);
    if (entity == null) {
      return new H5N03EnterpriseCertificationDetailResponse(
          "",
          user.getUserId(),
          user.getAccount(),
          "UNSUBMITTED",
          certificationStatusText("UNSUBMITTED"),
          user.getCompanyName(),
          "",
          "",
          "",
          user.getContactName(),
          user.getPhoneMasked(),
          "",
          "",
          "",
          "",
          "",
          "",
          "",
          "",
          "",
          "",
          "",
          "H5",
          true,
          null,
          null,
          null);
    }
    return toH5CertificationDetail(entity);
  }

  public H5N03EnterpriseCertificationDetailResponse submitH5EnterpriseCertification(
      String token, H5N03EnterpriseCertificationSubmitRequest request) {
    validateH5CertificationRequest(request);
    String operator = safeText(request.operator());
    EnterpriseCertificationEntity saved =
        repository.saveCertification(
            token,
            new EnterpriseCertificationDraft(
                request.companyName().trim(),
                request.unifiedSocialCreditCode().trim().toUpperCase(),
                request.legalPersonName().trim(),
                request.legalPersonIdNo().trim().toUpperCase(),
                request.contactName().trim(),
                request.contactMobile().trim(),
                request.businessLicenseUrl().trim(),
                request.legalIdFrontUrl().trim(),
                request.legalIdBackUrl().trim(),
                safeText(request.bankAccountName()),
                safeText(request.bankAccountNo()),
                safeText(request.bankName()),
                request.province().trim(),
                request.city().trim(),
                request.address().trim(),
                safeText(request.remark()),
                operator.isBlank() ? "h5-n03-submit" : operator));
    return toH5CertificationDetail(saved);
  }

  public N04OnboardingProgressResponse onboardingProgress(String token) {
    AuthUserEntity user =
        repository
            .findUserByToken(token)
            .orElseThrow(() -> new BaseException(ErrorCode.UNAUTHORIZED.getCode(), "登录态无效"));
    EnterpriseCertificationEntity cert = repository.findCertificationByToken(token).orElse(null);

    String certificationStatus = cert == null ? "UNSUBMITTED" : cert.getStatus();
    String currentStepCode = currentStepCodeByStatus(certificationStatus);
    int progressPercent = progressPercentByStatus(certificationStatus);

    String submittedAt = cert == null ? null : toText(cert.getSubmittedAt());
    String reviewFinishedAt = ("APPROVED".equalsIgnoreCase(certificationStatus)
            || "REJECTED".equalsIgnoreCase(certificationStatus))
        ? toText(cert == null ? null : cert.getUpdatedAt())
        : null;

    List<N04OnboardingProgressNodeDTO> nodes =
        List.of(
            new N04OnboardingProgressNodeDTO(
                "CERT_SUBMIT",
                "提交企业认证",
                "请填写企业资质与对公账户信息后提交",
                "UNSUBMITTED".equalsIgnoreCase(certificationStatus) ? "PENDING" : "COMPLETED",
                "UNSUBMITTED".equalsIgnoreCase(certificationStatus) ? "待提交" : "已提交",
                "商家",
                submittedAt,
                submittedAt,
                "UNSUBMITTED".equalsIgnoreCase(certificationStatus) ? "等待提交资料" : "资料已提交"),
            new N04OnboardingProgressNodeDTO(
                "REVIEW",
                "平台审核中",
                "平台将在1-2个工作日内完成审核",
                reviewStatusByCertificationStatus(certificationStatus),
                reviewStatusTextByCertificationStatus(certificationStatus),
                "审核专员",
                submittedAt,
                reviewFinishedAt,
                "REJECTED".equalsIgnoreCase(certificationStatus)
                    ? "资料需补充后重提"
                    : "审核流程正常"),
            new N04OnboardingProgressNodeDTO(
                "ONBOARDING",
                "入驻完成",
                "审核通过后即可开通完整入驻能力",
                "APPROVED".equalsIgnoreCase(certificationStatus) ? "COMPLETED" : "PENDING",
                "APPROVED".equalsIgnoreCase(certificationStatus) ? "已完成" : "待完成",
                "系统",
                "APPROVED".equalsIgnoreCase(certificationStatus) ? reviewFinishedAt : null,
                "APPROVED".equalsIgnoreCase(certificationStatus) ? reviewFinishedAt : null,
                "APPROVED".equalsIgnoreCase(certificationStatus) ? "已开通" : "等待审核结果"));

    String statusText =
        switch (certificationStatus) {
          case "UNSUBMITTED" -> "待提交";
          case "PENDING_REVIEW" -> "审核中";
          case "REJECTED" -> "已驳回";
          case "APPROVED" -> "已通过";
          default -> "处理中";
        };

    String currentStepName = stepNameByCode(currentStepCode);

    String rejectReason =
        "REJECTED".equalsIgnoreCase(certificationStatus)
            ? "证照信息不清晰或关键字段缺失，请补充后重新提交"
            : null;

    String expectedFinishAt = estimateFinishAt(certificationStatus, cert);
    return new N04OnboardingProgressResponse(
        cert == null ? null : cert.getCertificationId(),
        user.getUserId(),
        user.getAccount(),
        user.getCompanyName(),
        certificationStatus,
        statusText,
        progressPercent,
        currentStepCode,
        currentStepName,
        expectedFinishAt,
        nodes,
        submittedAt,
        cert == null ? null : toText(cert.getUpdatedAt()),
        rejectReason);
  }

  public N05NegotiationSessionListResponse negotiationSessions(
      String token, String status, String keyword, int pageNo, int pageSize) {
    int safePageNo = Math.max(1, pageNo);
    int safePageSize = Math.min(Math.max(1, pageSize), 50);
    N05NegotiationQuery query = new N05NegotiationQuery(status, keyword, safePageNo, safePageSize);
    List<N05NegotiationSessionEntity> all = repository.listNegotiations(token, query);
    int from = Math.min((safePageNo - 1) * safePageSize, all.size());
    int to = Math.min(from + safePageSize, all.size());
    List<N05NegotiationSessionEntity> sessions = all.subList(from, to);

    List<N05NegotiationSessionItemDTO> items =
        sessions.stream()
            .map(
                session ->
                    new N05NegotiationSessionItemDTO(
                        session.getSessionId(),
                        session.getInquiryNo(),
                        session.getProductName(),
                        session.getSpecification(),
                        session.getQuantityText(),
                        session.getCity(),
                        session.getCounterpartyName(),
                        session.getRoleInSession(),
                        session.getStatus(),
                        sessionStatusText(session.getStatus()),
                        session.getLatestOfferPrice(),
                        session.getLatestMessage(),
                        toText(session.getLatestMessageAt()),
                        session.getUnreadCount()))
            .toList();

    return new N05NegotiationSessionListResponse(safePageNo, safePageSize, all.size(), items);
  }

  public N05NegotiationDetailResponse negotiationDetail(String token, String sessionId) {
    N05NegotiationSessionEntity session = repository.getNegotiationDetail(token, sessionId);
    List<N05NegotiationMessageDTO> messages = session.getMessages().stream().map(this::toMessageDTO).toList();
    return new N05NegotiationDetailResponse(
        session.getSessionId(),
        session.getInquiryNo(),
        session.getProductName(),
        session.getSpecification(),
        session.getBuyerName(),
        session.getSupplierName(),
        session.getStatus(),
        sessionStatusText(session.getStatus()),
        session.getLatestOfferPrice(),
        session.getTargetPrice(),
        session.getRoundNo(),
        String.valueOf(session.getUnreadCount()),
        toText(session.getLatestMessageAt()),
        toText(session.getUpdatedAt()),
        messages);
  }

  public N05NegotiationDetailResponse sendNegotiationMessage(
      String token, String sessionId, N05NegotiationSendMessageRequest request) {
    repository.appendNegotiationMessage(
        token,
        sessionId,
        safeText(request.senderRole()),
        safeText(request.content()),
        safeText(request.operator()));
    return negotiationDetail(token, sessionId);
  }

  public N05NegotiationDetailResponse updateNegotiationStatus(
      String token, String sessionId, N05NegotiationStatusUpdateRequest request) {
    String normalized = normalizeActionToStatus(request.action());
    repository.updateNegotiationStatus(token, sessionId, normalized, safeText(request.remark()));
    return negotiationDetail(token, sessionId);
  }

  public H5N04NegotiationSessionListResponse h5NegotiationSessions(
      String token, String status, String keyword, int pageNo, int pageSize) {
    int safePageNo = Math.max(1, pageNo);
    int safePageSize = Math.min(Math.max(1, pageSize), 50);
    N05NegotiationQuery query = new N05NegotiationQuery(status, keyword, safePageNo, safePageSize);
    List<N05NegotiationSessionEntity> all = repository.listNegotiations(token, query);
    int from = Math.min((safePageNo - 1) * safePageSize, all.size());
    int to = Math.min(from + safePageSize, all.size());
    List<N05NegotiationSessionEntity> sessions = all.subList(from, to);

    List<H5N04NegotiationSessionItemDTO> records =
        sessions.stream()
            .map(
                session ->
                    new H5N04NegotiationSessionItemDTO(
                        session.getSessionId(),
                        session.getInquiryNo(),
                        session.getProductName(),
                        session.getSpecification(),
                        session.getCounterpartyName(),
                        session.getRoleInSession(),
                        session.getStatus(),
                        sessionStatusText(session.getStatus()),
                        session.getLatestOfferPrice(),
                        session.getLatestMessage(),
                        toText(session.getLatestMessageAt()),
                        session.getUnreadCount(),
                        h5StatusActionText(session.getStatus())))
            .toList();
    String activeSessionId = records.isEmpty() ? "" : records.get(0).sessionId();
    return new H5N04NegotiationSessionListResponse(
        safePageNo, safePageSize, all.size(), "H5", activeSessionId, records);
  }

  public H5N04NegotiationDetailResponse h5NegotiationDetail(String token, String sessionId) {
    N05NegotiationSessionEntity session = repository.getNegotiationDetail(token, sessionId);
    List<H5N04NegotiationMessageDTO> messages =
        session.getMessages().stream().map(this::toH5NegotiationMessage).toList();
    return toH5NegotiationDetail(session, messages);
  }

  public H5N04NegotiationDetailResponse h5SendNegotiationMessage(
      String token, String sessionId, H5N04NegotiationSendMessageRequest request) {
    repository.appendNegotiationMessage(
        token,
        sessionId,
        safeText(request.senderRole()),
        safeText(request.content()),
        safeText(request.operator()));
    return h5NegotiationDetail(token, sessionId);
  }

  public H5N04NegotiationDetailResponse h5UpdateNegotiationStatus(
      String token, String sessionId, H5N04NegotiationStatusUpdateRequest request) {
    String normalized = normalizeActionToStatus(request.action());
    String remark = safeText(request.remark());
    String operator = safeText(request.operator());
    String finalOperator = operator.isBlank() ? "h5-n04-status" : operator;
    String fullRemark = remark.isBlank() ? finalOperator : remark + "（" + finalOperator + "）";
    repository.updateNegotiationStatus(token, sessionId, normalized, fullRemark);
    return h5NegotiationDetail(token, sessionId);
  }

  public H5N05OrderListResponse h5OrderList(
      String token, String status, String keyword, int pageNo, int pageSize) {
    int safePageNo = Math.max(1, pageNo);
    int safePageSize = Math.min(Math.max(1, pageSize), 50);
    N06OrderQuery query = new N06OrderQuery(status, keyword, safePageNo, safePageSize);
    List<N06OrderEntity> all = repository.listOrders(token, query);
    int from = Math.min((safePageNo - 1) * safePageSize, all.size());
    int to = Math.min(from + safePageSize, all.size());
    List<N06OrderEntity> paged = all.subList(from, to);

    List<H5N05OrderListItemDTO> records =
        paged.stream()
            .map(
                item -> {
                  List<N06OrderActionDTO> actions = orderActionsForStatus(item.getOrderStatus());
                  String quickActionText = actions.isEmpty() ? "查看详情" : actions.get(0).actionName();
                  return new H5N05OrderListItemDTO(
                      item.getOrderId(),
                      item.getOrderNo(),
                      item.getGoodsName(),
                      item.getQuantityTon(),
                      item.getSupplierName(),
                      item.getOrderStatus(),
                      item.getOrderStatusText(),
                      item.getDealTotalAmount(),
                      toText(item.getUpdatedAt()),
                      quickActionText);
                })
            .toList();
    String activeOrderId = records.isEmpty() ? "" : records.get(0).orderId();
    return new H5N05OrderListResponse(
        safePageNo, safePageSize, all.size(), "H5", activeOrderId, records);
  }

  public H5N05OrderDetailResponse h5OrderDetail(String token, String orderId) {
    N06OrderEntity order = repository.getOrderDetail(token, orderId);
    List<H5N05OrderTimelineNodeDTO> timeline =
        order.getTimeline().stream()
            .map(
                node ->
                    new H5N05OrderTimelineNodeDTO(
                        node.getNodeCode(),
                        node.getNodeName(),
                        node.getStatus(),
                        node.getStatusText(),
                        node.getOwner(),
                        node.getHappenedAt(),
                        node.getRemark()))
            .toList();
    List<H5N05OrderActionDTO> actions =
        orderActionsForStatus(order.getOrderStatus()).stream()
            .map(
                item ->
                    new H5N05OrderActionDTO(
                        item.actionCode(), item.actionName(), item.enabled(), item.reason()))
            .toList();
    List<String> availableActions =
        actions.stream().filter(H5N05OrderActionDTO::enabled).map(H5N05OrderActionDTO::actionCode).toList();

    String receivable = order.getDealTotalAmount();
    String paidAmount = isPaidLike(order.getPaymentStatus()) ? receivable : "0";
    String outstanding = isPaidLike(order.getPaymentStatus()) ? "0" : receivable;
    return new H5N05OrderDetailResponse(
        order.getOrderId(),
        order.getOrderNo(),
        order.getInquiryNo(),
        order.getGoodsName(),
        order.getGoodsName(),
        order.getQuantityTon(),
        order.getSupplierName(),
        order.getBuyerCompany(),
        order.getOrderStatus(),
        order.getOrderStatusText(),
        order.getPaymentStatus(),
        h5PaymentStatusText(order.getPaymentStatus()),
        order.getContractStatus(),
        order.getContractStatusText(),
        order.getExpectedDeliveryAt(),
        order.getLatestRemark(),
        receivable,
        paidAmount,
        outstanding,
        "H5",
        availableActions,
        toText(order.getCreatedAt()),
        toText(order.getUpdatedAt()),
        timeline,
        actions);
  }

  public H5N05OrderDetailResponse h5UpdateOrderStatus(
      String token, String orderId, H5N05OrderStatusUpdateRequest request) {
    String targetStatus = mapOrderActionToStatus(request.action());
    String remark = safeText(request.remark());
    String operator = safeText(request.operator());
    String finalOperator = operator.isBlank() ? "h5-n05-order-status" : operator;
    String fullRemark = remark.isBlank() ? finalOperator : remark + "（" + finalOperator + "）";
    repository.updateOrderStatus(token, orderId, targetStatus, finalOperator, fullRemark);
    return h5OrderDetail(token, orderId);
  }

  public H5N06PickupOrderListResponse h5PickupOrderList(
      String token, String status, String keyword, int pageNo, int pageSize) {
    AuthUserEntity user =
        repository
            .findUserByToken(token)
            .orElseThrow(() -> new BaseException(ErrorCode.UNAUTHORIZED.getCode(), "登录态无效"));
    int safePageNo = Math.max(1, pageNo);
    int safePageSize = Math.min(Math.max(1, pageSize), 50);
    String contactMobile = user.getAccount();

    com.huodaizi.backend.dto.inquiry.InquiryPickupOrderListRequest request =
        new com.huodaizi.backend.dto.inquiry.InquiryPickupOrderListRequest(
            contactMobile, safeText(status), safeText(keyword), safePageNo, safePageSize);
    List<InquiryPickupOrderEntity> all = inquiryRepository.listPickupOrders(request);
    int from = Math.min((safePageNo - 1) * safePageSize, all.size());
    int to = Math.min(from + safePageSize, all.size());
    List<InquiryPickupOrderEntity> paged = all.subList(from, to);

    List<H5N06PickupOrderListItemDTO> records =
        paged.stream()
            .map(
                item ->
                    new H5N06PickupOrderListItemDTO(
                        item.getPickupId(),
                        item.getPickupNo(),
                        item.getInquiryNo(),
                        item.getQuoteId(),
                        item.getSupplierName(),
                        item.getSpecText() + " / " + item.getQuantityTon() + "吨",
                        item.getPickupAddress(),
                        item.getPickupDate(),
                        item.getTruckNo(),
                        item.getStatus().name(),
                        h5PickupStatusText(item.getStatus().name()),
                        h5PickupQuickActionText(item.getStatus().name()),
                        toText(item.getUpdatedAt())))
            .toList();
    String activePickupId = records.isEmpty() ? "" : records.get(0).pickupId();
    return new H5N06PickupOrderListResponse(
        safePageNo,
        safePageSize,
        all.size(),
        "H5",
        repository.maskPhone(contactMobile),
        activePickupId,
        records);
  }

  public H5N06PickupOrderDetailResponse h5PickupOrderDetail(String token, String pickupId) {
    AuthUserEntity user =
        repository
            .findUserByToken(token)
            .orElseThrow(() -> new BaseException(ErrorCode.UNAUTHORIZED.getCode(), "登录态无效"));
    String contactMobile = user.getAccount();
    InquiryPickupOrderEntity item = inquiryRepository.getPickupOrderById(safeText(pickupId), contactMobile);
    return toH5PickupOrderDetail(item, contactMobile, "扫码已通过，核验司机与车牌后可推进状态");
  }

  public H5N06PickupOrderDetailResponse h5ScanPickupOrder(String token, H5N06PickupOrderScanRequest request) {
    AuthUserEntity user =
        repository
            .findUserByToken(token)
            .orElseThrow(() -> new BaseException(ErrorCode.UNAUTHORIZED.getCode(), "登录态无效"));
    String scanCode = safeText(request.scanCode());
    if (scanCode.isBlank()) {
      throw new BaseException(ErrorCode.BAD_REQUEST.getCode(), "scanCode 不能为空");
    }
    String pickupId = scanCode;
    if (scanCode.contains("-PU")) {
      pickupId = scanCode.substring(scanCode.lastIndexOf("-PU") + 1);
    } else if (scanCode.startsWith("PU-")) {
      String suffix = scanCode.substring(3);
      if (suffix.matches("\\d+")) {
        pickupId = "PU" + suffix;
      }
    }
    InquiryPickupOrderEntity item = inquiryRepository.getPickupOrderById(pickupId, user.getAccount());
    return toH5PickupOrderDetail(
        item,
        user.getAccount(),
        "扫码核验成功，可执行「确认提货/运输中/已签收/已完成」状态流转");
  }

  public H5N06PickupOrderDetailResponse h5PickupOrderStatusUpdate(
      String token, String pickupId, H5N06PickupOrderStatusUpdateRequest request) {
    AuthUserEntity user =
        repository
            .findUserByToken(token)
            .orElseThrow(() -> new BaseException(ErrorCode.UNAUTHORIZED.getCode(), "登录态无效"));
    String normalizedStatus = safeText(request.status()).toUpperCase();
    if (!normalizedStatus.matches("CONFIRMED|IN_TRANSIT|SIGNED|COMPLETED|CANCELLED")) {
      throw new BaseException(
          ErrorCode.BAD_REQUEST.getCode(), "status 仅支持 CONFIRMED/IN_TRANSIT/SIGNED/COMPLETED/CANCELLED");
    }
    String operator = safeText(request.operator());
    String remark = safeText(request.remark());
    String finalOperator = operator.isBlank() ? "h5-n06-scan" : operator;
    String fullRemark = remark.isBlank() ? finalOperator : remark + "（" + finalOperator + "）";
    InquiryPickupOrderEntity updated =
        inquiryRepository.updatePickupOrderStatus(
            safeText(pickupId),
            new InquiryPickupOrderStatusUpdateRequest(
                user.getAccount(), normalizedStatus, "h5-n06-scan", fullRemark));
    return toH5PickupOrderDetail(updated, user.getAccount(), "提货状态已更新");
  }

  public H5N07ReconcileListResponse h5ReconcileList(
      String token, String status, String keyword, int pageNo, int pageSize) {
    AuthUserEntity user =
        repository
            .findUserByToken(token)
            .orElseThrow(() -> new BaseException(ErrorCode.UNAUTHORIZED.getCode(), "登录态无效"));
    int safePageNo = Math.max(1, pageNo);
    int safePageSize = Math.min(Math.max(1, pageSize), 50);
    String contactMobile = user.getAccount();

    com.huodaizi.backend.dto.inquiry.InquiryReconcileOrderListRequest request =
        new com.huodaizi.backend.dto.inquiry.InquiryReconcileOrderListRequest(
            contactMobile, safeText(status), safeText(keyword), safePageNo, safePageSize);
    List<InquiryReconcileOrderEntity> all = inquiryRepository.listReconcileOrders(request);
    int from = Math.min((safePageNo - 1) * safePageSize, all.size());
    int to = Math.min(from + safePageSize, all.size());
    List<InquiryReconcileOrderEntity> paged = all.subList(from, to);

    List<H5N07ReconcileListItemDTO> records =
        paged.stream()
            .map(
                item ->
                    new H5N07ReconcileListItemDTO(
                        item.getReconcileId(),
                        item.getReconcileNo(),
                        item.getPickupOrderNo(),
                        item.getSupplierName(),
                        item.getGoodsSummary(),
                        item.getReceivableAmount(),
                        item.getPaidAmount(),
                        item.getOutstandingAmount(),
                        item.getStatus().name(),
                        h5ReconcileStatusText(item.getStatus().name()),
                        h5ReconcileQuickActionText(item.getStatus().name()),
                        toText(item.getUpdatedAt())))
            .toList();
    String activeReconcileId = records.isEmpty() ? "" : records.get(0).reconcileId();
    return new H5N07ReconcileListResponse(
        safePageNo,
        safePageSize,
        all.size(),
        "H5",
        repository.maskPhone(contactMobile),
        activeReconcileId,
        records);
  }

  public H5N07ReconcileDetailResponse h5ReconcileDetail(String token, String reconcileId) {
    AuthUserEntity user =
        repository
            .findUserByToken(token)
            .orElseThrow(() -> new BaseException(ErrorCode.UNAUTHORIZED.getCode(), "登录态无效"));
    InquiryReconcileOrderEntity item =
        inquiryRepository.getReconcileOrderById(safeText(reconcileId), user.getAccount());
    return toH5ReconcileDetail(item, "已定位对账单，可执行回款状态更新");
  }

  public H5N07ReconcileDetailResponse h5ReconcileStatusUpdate(
      String token, String reconcileId, H5N07ReconcileStatusUpdateRequest request) {
    AuthUserEntity user =
        repository
            .findUserByToken(token)
            .orElseThrow(() -> new BaseException(ErrorCode.UNAUTHORIZED.getCode(), "登录态无效"));
    String normalizedStatus = safeText(request.status()).toUpperCase();
    if (!normalizedStatus.matches("CONFIRMED|PARTIAL_PAID|PAID|CLOSED|DISPUTED")) {
      throw new BaseException(
          ErrorCode.BAD_REQUEST.getCode(), "status 仅支持 CONFIRMED/PARTIAL_PAID/PAID/CLOSED/DISPUTED");
    }
    String operator = safeText(request.operator());
    String remark = safeText(request.remark());
    String finalOperator = operator.isBlank() ? "h5-n07-reconcile" : operator;
    String fullRemark = remark.isBlank() ? finalOperator : remark + "（" + finalOperator + "）";
    InquiryReconcileOrderEntity updated =
        inquiryRepository.updateReconcileOrderStatus(
            safeText(reconcileId),
            new com.huodaizi.backend.dto.inquiry.InquiryReconcileOrderStatusUpdateRequest(
                user.getAccount(),
                normalizedStatus,
                safeText(request.paidAmount()),
                finalOperator,
                fullRemark));
    return toH5ReconcileDetail(updated, "对账状态已更新");
  }

  public H5N08AfterSaleListResponse h5AfterSaleList(
      String token, String status, String keyword, int pageNo, int pageSize) {
    int safePageNo = Math.max(1, pageNo);
    int safePageSize = Math.min(Math.max(1, pageSize), 50);
    N08AfterSaleQuery query = new N08AfterSaleQuery(status, keyword, safePageNo, safePageSize);
    List<N08AfterSaleDisputeEntity> all = repository.listAfterSaleDisputes(token, query);
    int from = Math.min((safePageNo - 1) * safePageSize, all.size());
    int to = Math.min(from + safePageSize, all.size());
    List<N08AfterSaleDisputeEntity> paged = all.subList(from, to);
    List<H5N08AfterSaleListItemDTO> records =
        paged.stream()
            .map(
                item ->
                    new H5N08AfterSaleListItemDTO(
                        item.getDisputeId(),
                        item.getOrderId(),
                        item.getOrderNo(),
                        item.getInquiryNo(),
                        item.getSupplierName(),
                        item.getIssueType(),
                        item.getIssueTypeText(),
                        item.getIssueSummary(),
                        item.getStatus(),
                        item.getStatusText(),
                        h5AfterSaleQuickActionText(item.getStatus()),
                        toText(item.getUpdatedAt())))
            .toList();
    String activeDisputeId = records.isEmpty() ? "" : records.get(0).disputeId();
    return new H5N08AfterSaleListResponse(
        safePageNo, safePageSize, all.size(), "H5", activeDisputeId, records);
  }

  public H5N08AfterSaleDetailResponse h5AfterSaleDetail(String token, String disputeId) {
    N08AfterSaleDisputeEntity item = repository.getAfterSaleDisputeDetail(token, disputeId);
    return toH5AfterSaleDetail(item, "可继续补充证据并推进处理状态");
  }

  public H5N08AfterSaleDetailResponse h5CreateAfterSale(
      String token, H5N08AfterSaleCreateRequest request) {
    N08AfterSaleDisputeEntity created =
        repository.createAfterSaleDispute(
            token,
            safeText(request.orderId()),
            safeText(request.issueType()),
            safeText(request.issueSummary()),
            safeText(request.issueDescription()),
            safeText(request.expectedResolution()),
            safeText(request.contactName()),
            safeText(request.contactPhone()),
            safeText(request.evidenceFiles()),
            safeText(request.operator()));
    return toH5AfterSaleDetail(created, "售后争议已发起，等待平台处理");
  }

  public H5N08AfterSaleDetailResponse h5UpdateAfterSaleStatus(
      String token, String disputeId, H5N08AfterSaleStatusUpdateRequest request) {
    String targetStatus = mapAfterSaleActionToStatus(request.action());
    String operator = safeText(request.operator());
    String remark = safeText(request.remark());
    String finalOperator = operator.isBlank() ? "h5-n08-after-sale" : operator;
    String fullRemark = remark.isBlank() ? finalOperator : remark + "（" + finalOperator + "）";
    repository.updateAfterSaleDisputeStatus(token, disputeId, targetStatus, finalOperator, fullRemark);
    return h5AfterSaleDetail(token, disputeId);
  }

  public H5N09LitePayListResponse h5LitePayOrderList(
      String token, String status, String keyword, int pageNo, int pageSize) {
    AuthUserEntity user =
        repository
            .findUserByToken(token)
            .orElseThrow(() -> new BaseException(ErrorCode.UNAUTHORIZED.getCode(), "登录态无效"));
    int safePageNo = Math.max(1, pageNo);
    int safePageSize = Math.min(Math.max(1, pageSize), 50);
    N10CashierQuery query = new N10CashierQuery(status, keyword, safePageNo, safePageSize);
    List<N10CashierOrderEntity> all = repository.listCashierOrders(token, query);
    int from = Math.min((safePageNo - 1) * safePageSize, all.size());
    int to = Math.min(from + safePageSize, all.size());
    List<N10CashierOrderEntity> paged = all.subList(from, to);
    List<H5N09LitePayListItemDTO> records =
        paged.stream()
            .map(
                item ->
                    new H5N09LitePayListItemDTO(
                        item.getCashierId(),
                        item.getOrderId(),
                        item.getOrderNo(),
                        item.getInquiryNo(),
                        item.getGoodsName(),
                        item.getSupplierName(),
                        item.getAmountPayable(),
                        item.getAmountPaid(),
                        item.getAmountOutstanding(),
                        item.getPayStatus(),
                        h5LitePayStatusText(item.getPayStatus()),
                        h5LitePayQuickActionText(item.getPayStatus()),
                        toText(item.getUpdatedAt())))
            .toList();
    String activeCashierOrderId = records.isEmpty() ? "" : records.get(0).cashierOrderId();
    return new H5N09LitePayListResponse(
        safePageNo,
        safePageSize,
        all.size(),
        "H5",
        repository.maskPhone(user.getAccount()),
        activeCashierOrderId,
        records);
  }

  public H5N09LitePayDetailResponse h5LitePayOrderDetail(String token, String cashierOrderId) {
    N10CashierOrderEntity item = repository.getCashierOrderDetail(token, cashierOrderId);
    return toH5LitePayDetail(item, "可通过轻支付快速完成收款并同步订单状态");
  }

  public H5N09LitePayDetailResponse h5LitePaySubmit(
      String token, String cashierOrderId, H5N09LitePaySubmitRequest request) {
    String method = safeText(request.payMethod()).toUpperCase();
    if (!method.matches("BANK_TRANSFER|ALIPAY|WECHAT|UNIONPAY")) {
      throw new BaseException(
          ErrorCode.BAD_REQUEST.getCode(), "payMethod 仅支持 BANK_TRANSFER/ALIPAY/WECHAT/UNIONPAY");
    }
    String operator = safeText(request.operator());
    String finalOperator = operator.isBlank() ? "h5-n09-lite-pay" : operator;
    String remark = safeText(request.remark());
    String finalRemark = remark.isBlank() ? "H5轻支付完成" : remark;
    N10CashierOrderEntity updated =
        repository.payCashierOrder(
            token,
            cashierOrderId,
            method,
            safeText(request.payerName()),
            finalRemark,
            finalOperator);
    return toH5LitePayDetail(updated, "支付已提交成功，可前往支付结果页查看流水");
  }

  public H5N10CreditBriefListResponse h5CreditBriefList(
      String token, String grade, String keyword, int pageNo, int pageSize) {
    AuthUserEntity user =
        repository
            .findUserByToken(token)
            .orElseThrow(() -> new BaseException(ErrorCode.UNAUTHORIZED.getCode(), "登录态无效"));
    int safePageNo = Math.max(1, pageNo);
    int safePageSize = Math.min(Math.max(1, pageSize), 50);
    N13CreditScoreQuery query = new N13CreditScoreQuery(safeText(grade), keyword, safePageNo, safePageSize);
    List<N13CreditScoreEntity> all = repository.listCreditScores(token, query);
    String normalizedGrade = safeText(grade).toUpperCase();
    if (!normalizedGrade.isBlank()) {
      all =
          all.stream()
              .filter(item -> h5CreditGrade(item).equalsIgnoreCase(normalizedGrade))
              .toList();
    }
    int from = Math.min((safePageNo - 1) * safePageSize, all.size());
    int to = Math.min(from + safePageSize, all.size());
    List<N13CreditScoreEntity> paged = all.subList(from, to);
    List<H5N10CreditBriefListItemDTO> records =
        paged.stream()
            .map(
                item ->
                    new H5N10CreditBriefListItemDTO(
                        item.getScoreId(),
                        item.getMerchantId(),
                        item.getMerchantName(),
                        item.getScoreMonth(),
                        h5CreditTotalScore(item),
                        h5CreditGrade(item),
                        item.getRankPercent(),
                        item.getRiskLevel(),
                        h5CreditRiskLevelText(item.getRiskLevel()),
                        h5CreditQuickActionText(item.getRiskLevel(), h5CreditGrade(item)),
                        toText(item.getUpdatedAt())))
            .toList();
    String activeScoreId = records.isEmpty() ? "" : records.get(0).scoreId();
    return new H5N10CreditBriefListResponse(
        safePageNo,
        safePageSize,
        all.size(),
        "H5",
        repository.maskPhone(user.getAccount()),
        activeScoreId,
        records);
  }

  public H5N10CreditBriefDetailResponse h5CreditBriefDetail(String token, String scoreId) {
    repository
        .findUserByToken(token)
        .orElseThrow(() -> new BaseException(ErrorCode.UNAUTHORIZED.getCode(), "登录态无效"));
    N13CreditScoreEntity item = repository.getCreditScoreDetail(token, scoreId);
    return toH5CreditBriefDetail(item, h5CreditTipText(item.getRiskLevel()));
  }

  public N06OrderListResponse orderList(
      String token, String status, String keyword, int pageNo, int pageSize) {
    int safePageNo = Math.max(1, pageNo);
    int safePageSize = Math.min(Math.max(1, pageSize), 50);
    N06OrderQuery query = new N06OrderQuery(status, keyword, safePageNo, safePageSize);
    List<N06OrderEntity> all = repository.listOrders(token, query);
    int from = Math.min((safePageNo - 1) * safePageSize, all.size());
    int to = Math.min(from + safePageSize, all.size());
    List<N06OrderEntity> paged = all.subList(from, to);

    List<N06OrderListItemDTO> records =
        paged.stream()
            .map(
                item ->
                    new N06OrderListItemDTO(
                        item.getOrderId(),
                        item.getOrderNo(),
                        item.getInquiryNo(),
                        item.getGoodsName(),
                        item.getGoodsName(),
                        item.getQuantityTon(),
                        item.getSupplierName(),
                        item.getBuyerCompany(),
                        item.getOrderStatus(),
                        item.getOrderStatusText(),
                        item.getDealTotalAmount(),
                        toText(item.getCreatedAt()),
                        toText(item.getUpdatedAt())))
            .toList();
    return new N06OrderListResponse(safePageNo, safePageSize, all.size(), records);
  }

  public N06OrderDetailResponse orderDetail(String token, String orderId) {
    N06OrderEntity order = repository.getOrderDetail(token, orderId);
    List<N06OrderTimelineNodeDTO> timeline =
        order.getTimeline().stream()
            .map(
                node ->
                    new N06OrderTimelineNodeDTO(
                        node.getNodeCode(),
                        node.getNodeName(),
                        node.getStatus(),
                        node.getStatusText(),
                        node.getOwner(),
                        node.getHappenedAt(),
                        node.getRemark()))
            .toList();
    List<N06OrderActionDTO> actions = orderActionsForStatus(order.getOrderStatus());
    String reconcileStatus = mapOrderToReconcile(order.getOrderStatus());
    String pickupStatus = mapOrderToPickup(order.getOrderStatus());
    String receivable = order.getDealTotalAmount();
    String paidAmount = "PAID".equalsIgnoreCase(order.getPaymentStatus()) ? receivable : "0";
    String outstanding = "PAID".equalsIgnoreCase(order.getPaymentStatus()) ? "0" : receivable;
    return new N06OrderDetailResponse(
        order.getOrderId(),
        order.getOrderNo(),
        order.getInquiryNo(),
        order.getInquiryNo(),
        "Q-" + order.getOrderNo(),
        "NS-" + order.getOrderId(),
        order.getOrderStatus(),
        order.getOrderStatusText(),
        order.getBuyerCompany(),
        "采购经理",
        repository.maskPhone("13800138000"),
        order.getSupplierName(),
        order.getGoodsName(),
        order.getGoodsName(),
        order.getQuantityTon(),
        order.getDealUnitPrice(),
        order.getDealTotalAmount(),
        "YES",
        "月结30天",
        order.getExpectedDeliveryAt(),
        order.getLatestRemark(),
        pickupStatus,
        pickupStatusText(pickupStatus),
        "PU-" + order.getOrderId(),
        order.getDeliveryCity() + "一号仓",
        reconcileStatus,
        reconcileStatusText(reconcileStatus),
        "RC-" + order.getOrderId(),
        receivable,
        paidAmount,
        outstanding,
        toText(order.getCreatedAt()),
        toText(order.getUpdatedAt()),
        timeline,
        actions);
  }

  public N06OrderDetailResponse orderAction(String token, String orderId, N06OrderActionRequest request) {
    String targetStatus = mapOrderActionToStatus(request.action());
    repository.updateOrderStatus(
        token, orderId, targetStatus, safeText(request.operator()), safeText(request.remark()));
    return orderDetail(token, orderId);
  }

  public N07TradeTermsDetailResponse tradeTermsDetail(String token, String orderId) {
    N07TradeTermsEntity entity = repository.getTradeTerms(token, orderId);
    return toTradeTermsDetail(entity);
  }

  public N07TradeTermsDetailResponse confirmTradeTerms(
      String token, String orderId, N07TradeTermsConfirmRequest request) {
    String action = safeText(request.action()).toUpperCase();
    if (!"CONFIRM".equals(action)) {
      throw new BaseException(ErrorCode.BAD_REQUEST.getCode(), "action 仅支持 CONFIRM");
    }
    N07TradeTermsEntity updated =
        repository.confirmTradeTerms(token, orderId, safeText(request.operator()), safeText(request.remark()));
    return toTradeTermsDetail(updated);
  }

  public N08AfterSaleDisputeListResponse afterSaleDisputeList(
      String token, String status, String keyword, int pageNo, int pageSize) {
    int safePageNo = Math.max(1, pageNo);
    int safePageSize = Math.min(Math.max(1, pageSize), 50);
    N08AfterSaleQuery query = new N08AfterSaleQuery(status, keyword, safePageNo, safePageSize);
    List<N08AfterSaleDisputeEntity> all = repository.listAfterSaleDisputes(token, query);
    int from = Math.min((safePageNo - 1) * safePageSize, all.size());
    int to = Math.min(from + safePageSize, all.size());
    List<N08AfterSaleDisputeEntity> paged = all.subList(from, to);
    List<N08AfterSaleDisputeListItemDTO> records =
        paged.stream()
            .map(
                item ->
                    new N08AfterSaleDisputeListItemDTO(
                        item.getDisputeId(),
                        item.getOrderId(),
                        item.getOrderNo(),
                        item.getInquiryNo(),
                        item.getSupplierName(),
                        item.getIssueType(),
                        item.getIssueTypeText(),
                        item.getIssueSummary(),
                        item.getStatus(),
                        item.getStatusText(),
                        toText(item.getCreatedAt()),
                        toText(item.getUpdatedAt())))
            .toList();
    return new N08AfterSaleDisputeListResponse(safePageNo, safePageSize, all.size(), records);
  }

  public N08AfterSaleDisputeDetailResponse afterSaleDisputeDetail(String token, String disputeId) {
    N08AfterSaleDisputeEntity item = repository.getAfterSaleDisputeDetail(token, disputeId);
    return new N08AfterSaleDisputeDetailResponse(
        item.getDisputeId(),
        item.getOrderId(),
        item.getOrderNo(),
        item.getInquiryNo(),
        item.getBuyerCompany(),
        item.getSupplierName(),
        item.getIssueType(),
        item.getIssueTypeText(),
        item.getIssueSummary(),
        item.getIssueDescription(),
        item.getExpectedResolution(),
        item.getContactName(),
        item.getContactPhoneMasked(),
        item.getEvidenceFiles(),
        item.getStatus(),
        item.getStatusText(),
        item.getLatestRemark(),
        toText(item.getCreatedAt()),
        toText(item.getUpdatedAt()));
  }

  public N08AfterSaleDisputeDetailResponse createAfterSaleDispute(
      String token, N08AfterSaleDisputeCreateRequest request) {
    N08AfterSaleDisputeEntity created =
        repository.createAfterSaleDispute(
            token,
            safeText(request.orderId()),
            safeText(request.issueType()),
            safeText(request.issueSummary()),
            safeText(request.issueDescription()),
            safeText(request.expectedResolution()),
            safeText(request.contactName()),
            safeText(request.contactPhone()),
            safeText(request.evidenceFiles()),
            safeText(request.operator()));
    return afterSaleDisputeDetail(token, created.getDisputeId());
  }

  public N08AfterSaleDisputeDetailResponse updateAfterSaleDisputeStatus(
      String token, String disputeId, N08AfterSaleDisputeStatusUpdateRequest request) {
    String targetStatus = mapAfterSaleActionToStatus(request.action());
    repository.updateAfterSaleDisputeStatus(
        token, disputeId, targetStatus, safeText(request.operator()), safeText(request.remark()));
    return afterSaleDisputeDetail(token, disputeId);
  }

  public N09AfterSaleProgressListResponse afterSaleProgressList(
      String token, String status, String keyword, int pageNo, int pageSize) {
    int safePageNo = Math.max(1, pageNo);
    int safePageSize = Math.min(Math.max(1, pageSize), 50);
    N08AfterSaleQuery query = new N08AfterSaleQuery(status, keyword, safePageNo, safePageSize);
    List<N08AfterSaleDisputeEntity> all = repository.listAfterSaleDisputes(token, query);
    int from = Math.min((safePageNo - 1) * safePageSize, all.size());
    int to = Math.min(from + safePageSize, all.size());
    List<N08AfterSaleDisputeEntity> paged = all.subList(from, to);
    List<N09AfterSaleProgressListItemDTO> records =
        paged.stream()
            .map(
                item ->
                    new N09AfterSaleProgressListItemDTO(
                        item.getDisputeId(),
                        item.getOrderNo(),
                        item.getSupplierName(),
                        item.getIssueTypeText(),
                        item.getIssueSummary(),
                        currentStageByStatus(item.getStatus()),
                        item.getStatus(),
                        item.getStatusText(),
                        item.getLatestRemark(),
                        toText(item.getUpdatedAt())))
            .toList();
    return new N09AfterSaleProgressListResponse(safePageNo, safePageSize, all.size(), records);
  }

  public N09AfterSaleProgressDetailResponse afterSaleProgressDetail(String token, String disputeId) {
    N08AfterSaleDisputeEntity item = repository.getAfterSaleDisputeDetail(token, disputeId);
    List<N09AfterSaleProgressNodeDTO> nodes =
        item.getProgressNodes().stream()
            .map(
                node ->
                    new N09AfterSaleProgressNodeDTO(
                        node.getNodeCode(),
                        node.getNodeName(),
                        node.getStatus(),
                        node.getStatusText(),
                        node.getHandler(),
                        node.getRemark(),
                        node.getHappenedAt()))
            .toList();
    return new N09AfterSaleProgressDetailResponse(
        item.getDisputeId(),
        item.getOrderId(),
        item.getOrderNo(),
        item.getInquiryNo(),
        item.getBuyerCompany(),
        item.getSupplierName(),
        item.getIssueType(),
        item.getIssueTypeText(),
        item.getIssueSummary(),
        item.getIssueDescription(),
        item.getStatus(),
        item.getStatusText(),
        item.getLatestRemark(),
        toText(item.getCreatedAt()),
        toText(item.getUpdatedAt()),
        progressPercentByAfterSaleStatus(item.getStatus()),
        currentStageByStatus(item.getStatus()),
        nodes);
  }

  public N10CashierOrderListResponse cashierOrderList(
      String token, String status, String keyword, int pageNo, int pageSize) {
    int safePageNo = Math.max(1, pageNo);
    int safePageSize = Math.min(Math.max(1, pageSize), 50);
    N10CashierQuery query = new N10CashierQuery(status, keyword, safePageNo, safePageSize);
    List<N10CashierOrderEntity> all = repository.listCashierOrders(token, query);
    int from = Math.min((safePageNo - 1) * safePageSize, all.size());
    int to = Math.min(from + safePageSize, all.size());
    List<N10CashierOrderEntity> paged = all.subList(from, to);
    List<N10CashierOrderListItemDTO> records =
        paged.stream()
            .map(
                item ->
                    new N10CashierOrderListItemDTO(
                        item.getCashierId(),
                        item.getOrderId(),
                        item.getOrderNo(),
                        item.getSupplierName(),
                        item.getAmountPayable(),
                        item.getAmountPaid(),
                        item.getAmountOutstanding(),
                        item.getPayStatus(),
                        item.getPayStatusText(),
                        toText(item.getCreatedAt()),
                        toText(item.getUpdatedAt())))
            .toList();
    return new N10CashierOrderListResponse(safePageNo, safePageSize, all.size(), records);
  }

  public N10CashierOrderDetailResponse cashierOrderDetail(String token, String cashierId) {
    N10CashierOrderEntity item = repository.getCashierOrderDetail(token, cashierId);
    List<N10CashierTimelineNodeDTO> timeline =
        item.getTimeline().stream()
            .map(
                node ->
                    new N10CashierTimelineNodeDTO(
                        node.getNodeCode(),
                        node.getNodeName(),
                        node.getStatus(),
                        node.getStatusText(),
                        node.getHandler(),
                        node.getRemark(),
                        node.getHappenedAt()))
            .toList();
    String finalPayAmount = item.getAmountPayable();
    return new N10CashierOrderDetailResponse(
        item.getCashierId(),
        item.getOrderId(),
        item.getOrderNo(),
        item.getInquiryNo(),
        item.getBuyerCompany(),
        item.getSupplierName(),
        item.getGoodsName(),
        "-",
        "FULL",
        "全额支付",
        item.getAmountPayable(),
        item.getAmountPaid(),
        "0",
        "0",
        finalPayAmount,
        item.getPayStatus(),
        item.getPayStatusText(),
        item.getLatestRemark(),
        toText(item.getUpdatedAt()),
        item.getPaidAt(),
        toText(item.getCreatedAt()),
        toText(item.getUpdatedAt()),
        timeline);
  }

  public N10CashierOrderDetailResponse payCashierOrder(
      String token, String cashierId, N10CashierPayRequest request) {
    N10CashierOrderEntity updated =
        repository.payCashierOrder(
            token,
            cashierId,
            safeText(request.payMethod()),
            safeText(request.payerName()),
            safeText(request.remark()),
            safeText(request.operator()));
    return cashierOrderDetail(token, updated.getCashierId());
  }

  public N11PaymentResultResponse paymentResult(String token, String cashierId) {
    N10CashierOrderEntity item = repository.getCashierOrderDetail(token, cashierId);
    List<N11PaymentResultNodeDTO> nodes =
        item.getTimeline().stream()
            .map(
                node ->
                    new N11PaymentResultNodeDTO(
                        node.getNodeCode(),
                        node.getNodeName(),
                        node.getStatus(),
                        node.getStatusText(),
                        node.getHandler(),
                        node.getHappenedAt(),
                        node.getRemark()))
            .toList();

    String payStatus = item.getPayStatus();
    String payStatusText = item.getPayStatusText();
    String resultStatus = "PAID".equalsIgnoreCase(payStatus) ? "SUCCESS" : "PENDING";
    String resultStatusText = "PAID".equalsIgnoreCase(payStatus) ? "支付成功" : "待支付";
    String nextActionHint = "PAID".equalsIgnoreCase(payStatus) ? "查看订单详情" : "返回收银台继续支付";

    String orderStatus = "UNKNOWN";
    String orderStatusText = "待同步";
    String orderSyncRemark = "订单状态同步处理中";
    try {
      N06OrderEntity order = repository.getOrderDetail(token, item.getOrderId());
      orderStatus = order.getOrderStatus();
      orderStatusText = order.getOrderStatusText();
      orderSyncRemark = "订单状态已同步";
    } catch (BaseException ignore) {
      // 支付结果页优先展示支付结果，不因订单详情缺失中断。
    }

    return new N11PaymentResultResponse(
        item.getCashierId(),
        item.getOrderId(),
        item.getOrderNo(),
        item.getInquiryNo(),
        item.getBuyerCompany(),
        item.getSupplierName(),
        item.getGoodsName(),
        payStatus,
        payStatusText,
        resultStatus,
        resultStatusText,
        item.getPayChannel(),
        item.getPayChannelText(),
        item.getAmountPayable(),
        item.getAmountPaid(),
        item.getAmountOutstanding(),
        item.getPaidAt(),
        item.getLatestRemark(),
        orderStatus,
        orderStatusText,
        orderSyncRemark,
        nextActionHint,
        toText(item.getCreatedAt()),
        toText(item.getUpdatedAt()),
        nodes);
  }

  public N12InvoiceTitleListResponse invoiceTitleList(String token) {
    List<N12InvoiceTitleDTO> records =
        repository.listInvoiceTitles(token, "").stream().map(this::toInvoiceTitleDTO).toList();
    return new N12InvoiceTitleListResponse(records);
  }

  public N12InvoiceTitleDTO upsertInvoiceTitle(String token, N12InvoiceTitleUpsertRequest request) {
    N12InvoiceTitleEntity saved =
        repository.saveInvoiceTitle(
            token,
            safeText(request.titleId()),
            safeText(request.titleName()),
            safeText(request.taxNo()),
            safeText(request.address()),
            safeText(request.phone()),
            safeText(request.bankName()),
            safeText(request.bankAccountNo()),
            parseDefaultFlag(request.defaultTitle()),
            safeText(request.operator()));
    return toInvoiceTitleDTO(saved);
  }

  public N12InvoiceTitleDTO setDefaultInvoiceTitle(
      String token, String titleId, N12InvoiceTitleSetDefaultRequest request) {
    N12InvoiceTitleEntity updated =
        repository.setInvoiceTitleDefault(token, titleId, safeText(request.operator()));
    return toInvoiceTitleDTO(updated);
  }

  public N12InvoiceTitleDTO setInvoiceTitleStatus(
      String token, String titleId, N12InvoiceTitleSetStatusRequest request) {
    N12InvoiceTitleEntity updated =
        repository.updateInvoiceTitleStatus(
            token, titleId, safeText(request.status()), safeText(request.operator()));
    return toInvoiceTitleDTO(updated);
  }

  public N12InvoiceApplicationListResponse invoiceApplicationList(
      String token, String status, String keyword, int pageNo, int pageSize) {
    int safePageNo = Math.max(1, pageNo);
    int safePageSize = Math.min(Math.max(1, pageSize), 50);
    N12InvoiceApplicationQuery query = new N12InvoiceApplicationQuery(status, keyword, safePageNo, safePageSize);
    List<N12InvoiceApplicationEntity> all = repository.listInvoiceApplications(token, query);
    int from = Math.min((safePageNo - 1) * safePageSize, all.size());
    int to = Math.min(from + safePageSize, all.size());
    List<N12InvoiceApplicationEntity> paged = all.subList(from, to);
    List<N12InvoiceApplicationListItemDTO> records =
        paged.stream()
            .map(
                item ->
                    new N12InvoiceApplicationListItemDTO(
                        item.getApplicationId(),
                        item.getOrderId(),
                        item.getOrderNo(),
                        item.getInvoiceType(),
                        item.getInvoiceTypeText(),
                        item.getStatus(),
                        item.getStatusText(),
                        item.getAmount(),
                        invoiceTitleName(token, item.getTitleId()),
                        toText(item.getCreatedAt()),
                        toText(item.getUpdatedAt())))
            .toList();
    return new N12InvoiceApplicationListResponse(safePageNo, safePageSize, all.size(), records);
  }

  public N12InvoiceApplicationDetailResponse invoiceApplicationDetail(String token, String applicationId) {
    N12InvoiceApplicationEntity item = repository.getInvoiceApplicationDetail(token, applicationId);
    N06OrderEntity order = repository.getOrderDetail(token, item.getOrderId());
    N12InvoiceTitleEntity title = repository.getInvoiceTitle(token, item.getTitleId());
    return new N12InvoiceApplicationDetailResponse(
        item.getApplicationId(),
        item.getOrderId(),
        item.getOrderNo(),
        order.getInquiryNo(),
        order.getBuyerCompany(),
        order.getSupplierName(),
        item.getAmount(),
        item.getInvoiceType(),
        item.getInvoiceTypeText(),
        item.getStatus(),
        item.getStatusText(),
        title.getTitleId(),
        title.getTitleName(),
        title.getTaxNo(),
        title.getRegisteredAddress(),
        maskPhoneOptional(title.getRegisteredPhone()),
        title.getBankName(),
        maskBankNo(title.getBankAccountNo()),
        "收票联系人",
        maskPhoneOptional(item.getRecipientMobile()),
        title.getRegisteredAddress(),
        item.getRecipientEmail(),
        "",
        "",
        item.getLatestRemark(),
        toText(item.getCreatedAt()),
        toText(item.getUpdatedAt()));
  }

  public N12InvoiceApplicationDetailResponse createInvoiceApplication(
      String token, N12InvoiceApplicationCreateRequest request) {
    N12InvoiceApplicationEntity created =
        repository.createInvoiceApplication(
            token,
            safeText(request.orderId()),
            safeText(request.titleId()),
            safeText(request.invoiceContent()),
            safeText(request.remark()),
            safeText(request.operator()));
    return invoiceApplicationDetail(token, created.getApplicationId());
  }

  public N13CreditScoreListResponse creditScoreList(
      String token, String grade, String keyword, int pageNo, int pageSize) {
    int safePageNo = Math.max(1, pageNo);
    int safePageSize = Math.min(Math.max(1, pageSize), 50);
    N13CreditScoreQuery query = new N13CreditScoreQuery(safeText(grade), keyword, safePageNo, safePageSize);
    List<N13CreditScoreEntity> all = repository.listCreditScores(token, query);
    int from = Math.min((safePageNo - 1) * safePageSize, all.size());
    int to = Math.min(from + safePageSize, all.size());
    List<N13CreditScoreEntity> paged = all.subList(from, to);
    List<N13CreditScoreListItemDTO> records =
        paged.stream()
            .map(
                item ->
                    new N13CreditScoreListItemDTO(
                        item.getScoreId(),
                        item.getMerchantId(),
                        item.getMerchantName(),
                        item.getScoreVersion(),
                        item.getTotalScore(),
                        item.getGrade(),
                        item.getRankPercent(),
                        item.getRiskLevel(),
                        toText(item.getUpdatedAt())))
            .toList();
    return new N13CreditScoreListResponse(safePageNo, safePageSize, all.size(), records);
  }

  public N13CreditScoreDetailResponse creditScoreDetail(String token, String scoreId) {
    N13CreditScoreEntity item = repository.getCreditScoreDetail(token, scoreId);
    List<N13CreditScoreFactorDTO> factors =
        item.getFactors().stream()
            .map(
                factor ->
                    new N13CreditScoreFactorDTO(
                        factor.getFactorCode(),
                        factor.getFactorName(),
                        factor.getScore(),
                        factor.getWeight(),
                        factor.getTrend(),
                        factor.getSummary()))
            .toList();
    List<N13CreditScoreTrendPointDTO> trend =
        item.getTimeline().stream()
            .map(
                point ->
                    new N13CreditScoreTrendPointDTO(
                        point.getNodeCode(),
                        point.getImpactScore(),
                        point.getImpactDirection(),
                        point.getDescription(),
                        point.getHappenedAt()))
            .toList();
    return new N13CreditScoreDetailResponse(
        item.getScoreId(),
        item.getMerchantId(),
        item.getMerchantName(),
        item.getScoreMonth(),
        item.getTotalScore(),
        item.getGrade(),
        item.getRankPercent(),
        item.getRiskLevel(),
        String.join("、", item.getTags()),
        item.getScoreVersion(),
        item.getRiskSummary(),
        toText(item.getUpdatedAt()),
        factors,
        trend);
  }

  public N14DispatchAppealListResponse dispatchAppealList(
      String token, String status, String keyword, int pageNo, int pageSize) {
    int safePageNo = Math.max(1, pageNo);
    int safePageSize = Math.min(Math.max(1, pageSize), 50);
    N14DispatchAppealQuery query = new N14DispatchAppealQuery(status, keyword, safePageNo, safePageSize);
    List<N14DispatchAppealEntity> all = repository.listDispatchAppeals(token, query);
    int from = Math.min((safePageNo - 1) * safePageSize, all.size());
    int to = Math.min(from + safePageSize, all.size());
    List<N14DispatchAppealEntity> paged = all.subList(from, to);
    List<N14DispatchAppealListItemDTO> records =
        paged.stream()
            .map(
                item ->
                    new N14DispatchAppealListItemDTO(
                        item.getAppealId(),
                        item.getMerchantId(),
                        item.getMerchantName(),
                        item.getSceneCode(),
                        item.getSceneName(),
                        item.getAppealType(),
                        item.getAppealTypeText(),
                        item.getStatus(),
                        item.getStatusText(),
                        item.getLatestRemark(),
                        toText(item.getCreatedAt()),
                        toText(item.getUpdatedAt())))
            .toList();
    return new N14DispatchAppealListResponse(safePageNo, safePageSize, all.size(), records);
  }

  public N14DispatchAppealDetailResponse dispatchAppealDetail(String token, String appealId) {
    N14DispatchAppealEntity item = repository.getDispatchAppealDetail(token, appealId);
    List<String> processLogs =
        item.getTimeline().stream()
            .map(
                node ->
                    "%s｜%s｜%s｜%s"
                        .formatted(
                            node.getHappenedAt(),
                            node.getNodeName(),
                            node.getOperator(),
                            node.getRemark()))
            .toList();
    return new N14DispatchAppealDetailResponse(
        item.getAppealId(),
        item.getMerchantId(),
        item.getMerchantName(),
        item.getSceneCode(),
        item.getSceneName(),
        "-",
        item.getTargetObjectId(),
        item.getScoreVersion(),
        "-",
        item.getAppealType(),
        item.getAppealTypeText(),
        item.getDescription(),
        item.getEvidenceFiles(),
        item.getStatus(),
        item.getStatusText(),
        item.getLatestRemark(),
        processLogs,
        toText(item.getCreatedAt()),
        toText(item.getUpdatedAt()));
  }

  public N14DispatchAppealDetailResponse createDispatchAppeal(
      String token, N14DispatchAppealCreateRequest request) {
    N14DispatchAppealEntity created =
        repository.createDispatchAppeal(
            token,
            safeText(request.sceneCode()),
            "",
            safeText(request.targetId()),
            "",
            "",
            safeText(request.appealType()),
            safeText(request.appealReason()),
            safeText(request.evidenceFiles()),
            safeText(request.operator()));
    return dispatchAppealDetail(token, created.getAppealId());
  }

  public N14DispatchAppealDetailResponse updateDispatchAppealStatus(
      String token, String appealId, N14DispatchAppealStatusUpdateRequest request) {
    N14DispatchAppealEntity updated =
        repository.updateDispatchAppealStatus(
            token,
            appealId,
            toAppealStatus(request.status()),
            safeText(request.operator()),
            safeText(request.remark()));
    return dispatchAppealDetail(token, updated.getAppealId());
  }

  private String toAppealStatus(String status) {
    String normalized = safeText(status).toUpperCase();
    return switch (normalized) {
      case "MARK_PROCESSING", "PROCESSING" -> "PROCESSING";
      case "MARK_APPROVED", "APPROVED", "RESOLVED" -> "APPROVED";
      case "MARK_REJECTED", "REJECTED" -> "REJECTED";
      case "MARK_CLOSED", "CLOSED" -> "CLOSED";
      case "SUBMITTED", "REOPEN" -> "SUBMITTED";
      default -> throw new BaseException(ErrorCode.BAD_REQUEST.getCode(), "status 不支持");
    };
  }

  private N07TradeTermsDetailResponse toTradeTermsDetail(N07TradeTermsEntity entity) {
    List<N07TradeTermClauseDTO> clauses =
        entity.getClauses().stream()
            .map(
                item ->
                    new N07TradeTermClauseDTO(
                        item.getClauseCode(), item.getClauseName(), item.getClauseContent(), item.isRequired()))
            .toList();
    List<N07TradeTermAttachmentDTO> attachments =
        entity.getAttachments().stream()
            .map(item -> new N07TradeTermAttachmentDTO(item.getFileName(), item.getFileType(), item.getFileUrl()))
            .toList();
    return new N07TradeTermsDetailResponse(
        entity.getOrderId(),
        entity.getOrderNo(),
        entity.getInquiryNo(),
        entity.getBuyerCompany(),
        entity.getSupplierName(),
        entity.getGoodsName(),
        entity.getSpecText(),
        entity.getQuantityTon(),
        entity.getUnitPrice(),
        entity.getTotalAmount(),
        entity.getDeliveryTerm(),
        entity.getPaymentTerm(),
        entity.getInvoiceTerm(),
        entity.getSettlementMethod(),
        entity.getQualityStandard(),
        entity.getToleranceRange(),
        entity.getBreachLiability(),
        entity.getDisputeResolution(),
        entity.getOtherClause(),
        entity.getEffectiveDate(),
        entity.getExpireDate(),
        entity.isConfirmed(),
        entity.getConfirmedBy(),
        entity.getConfirmedAt(),
        entity.getConfirmRemark(),
        toText(entity.getCreatedAt()),
        toText(entity.getUpdatedAt()),
        clauses,
        attachments);
  }

  private N03EnterpriseCertificationDetailResponse toCertificationDetail(
      EnterpriseCertificationEntity entity) {
    return new N03EnterpriseCertificationDetailResponse(
        entity.getCertificationId(),
        entity.getUserId(),
        entity.getAccount(),
        entity.getStatus(),
        entity.getCompanyName(),
        entity.getUnifiedSocialCreditCode(),
        entity.getLegalPersonName(),
        maskIdentityNo(entity.getLegalPersonIdNo()),
        entity.getContactName(),
        entity.getContactMobileMasked(),
        entity.getBusinessLicenseUrl(),
        entity.getLegalIdFrontUrl(),
        entity.getLegalIdBackUrl(),
        entity.getBankAccountName(),
        maskBankNo(entity.getBankAccountNo()),
        entity.getBankName(),
        entity.getProvince(),
        entity.getCity(),
        entity.getAddress(),
        entity.getRemark(),
        entity.getOperator(),
        toText(entity.getCreatedAt()),
        toText(entity.getSubmittedAt()),
        toText(entity.getUpdatedAt()));
  }

  private H5N03EnterpriseCertificationDetailResponse toH5CertificationDetail(
      EnterpriseCertificationEntity entity) {
    String status = safeText(entity.getStatus());
    return new H5N03EnterpriseCertificationDetailResponse(
        entity.getCertificationId(),
        entity.getUserId(),
        entity.getAccount(),
        status,
        certificationStatusText(status),
        entity.getCompanyName(),
        entity.getUnifiedSocialCreditCode(),
        entity.getLegalPersonName(),
        maskIdentityNo(entity.getLegalPersonIdNo()),
        entity.getContactName(),
        entity.getContactMobileMasked(),
        entity.getBusinessLicenseUrl(),
        entity.getLegalIdFrontUrl(),
        entity.getLegalIdBackUrl(),
        entity.getBankAccountName(),
        maskBankNo(entity.getBankAccountNo()),
        entity.getBankName(),
        entity.getProvince(),
        entity.getCity(),
        entity.getAddress(),
        entity.getRemark(),
        entity.getOperator(),
        "H5",
        canResubmitByStatus(status),
        toText(entity.getCreatedAt()),
        toText(entity.getSubmittedAt()),
        toText(entity.getUpdatedAt()));
  }

  private void validateCertificationRequest(N03EnterpriseCertificationSubmitRequest request) {
    String socialCode = request.unifiedSocialCreditCode().trim().toUpperCase();
    if (!socialCode.matches("^[0-9A-Z]{18}$")) {
      throw new BaseException(ErrorCode.BAD_REQUEST.getCode(), "统一社会信用代码需为18位数字或大写字母");
    }
    String legalId = request.legalPersonIdNo().trim().toUpperCase();
    if (!legalId.matches("^[0-9X]{15,18}$")) {
      throw new BaseException(ErrorCode.BAD_REQUEST.getCode(), "法人证件号格式不正确");
    }
    String mobile = request.contactMobile().trim();
    if (!mobile.matches("^1\\d{10}$")) {
      throw new BaseException(ErrorCode.BAD_REQUEST.getCode(), "联系人手机号必须为11位");
    }
  }

  private void validateH5CertificationRequest(H5N03EnterpriseCertificationSubmitRequest request) {
    String socialCode = request.unifiedSocialCreditCode().trim().toUpperCase();
    if (!socialCode.matches("^[0-9A-Z]{18}$")) {
      throw new BaseException(ErrorCode.BAD_REQUEST.getCode(), "统一社会信用代码需为18位数字或大写字母");
    }
    String legalId = request.legalPersonIdNo().trim().toUpperCase();
    if (!legalId.matches("^[0-9X]{15,18}$")) {
      throw new BaseException(ErrorCode.BAD_REQUEST.getCode(), "法人证件号格式不正确");
    }
    String mobile = request.contactMobile().trim();
    if (!mobile.matches("^1\\d{10}$")) {
      throw new BaseException(ErrorCode.BAD_REQUEST.getCode(), "联系人手机号必须为11位");
    }
  }

  private String safeText(String text) {
    return text == null ? "" : text.trim();
  }

  private N12InvoiceTitleDTO toInvoiceTitleDTO(N12InvoiceTitleEntity item) {
    return new N12InvoiceTitleDTO(
        item.getTitleId(),
        item.getTitleType(),
        item.getTitleTypeText(),
        item.getTitleName(),
        item.getTaxNo(),
        item.getBankName(),
        maskBankNo(item.getBankAccountNo()),
        item.getRegisteredAddress(),
        maskPhoneOptional(item.getRegisteredPhone()),
        item.getEmail(),
        item.isDefaultTitle(),
        item.getStatus(),
        item.getStatusText(),
        toText(item.getUpdatedAt()));
  }

  private String invoiceTitleName(String token, String titleId) {
    try {
      return repository.getInvoiceTitle(token, titleId).getTitleName();
    } catch (BaseException ignore) {
      return "";
    }
  }

  private boolean parseDefaultFlag(String value) {
    String normalized = safeText(value).toUpperCase();
    return "Y".equals(normalized) || "YES".equals(normalized) || "TRUE".equals(normalized);
  }

  private String maskPhoneOptional(String phone) {
    String value = safeText(phone);
    if (value.isBlank()) {
      return "";
    }
    return repository.maskPhone(value);
  }

  private String maskIdentityNo(String idNo) {
    String value = safeText(idNo).toUpperCase();
    if (value.isEmpty()) {
      return "";
    }
    if (value.length() <= 8) {
      return "****";
    }
    return value.substring(0, 4) + "********" + value.substring(value.length() - 4);
  }

  private String maskBankNo(String bankNo) {
    String value = safeText(bankNo);
    if (value.isEmpty()) {
      return "";
    }
    if (value.length() <= 8) {
      return "****";
    }
    return value.substring(0, 4) + "********" + value.substring(value.length() - 4);
  }

  private String toText(LocalDateTime time) {
    return time == null || time.equals(LocalDateTime.MIN) ? null : time.toString();
  }

  private String certificationStatusText(String status) {
    return switch (safeText(status).toUpperCase()) {
      case "UNSUBMITTED" -> "待提交";
      case "PENDING_REVIEW" -> "审核中";
      case "REJECTED" -> "已驳回";
      case "APPROVED" -> "已通过";
      default -> "处理中";
    };
  }

  private boolean canResubmitByStatus(String status) {
    String normalized = safeText(status).toUpperCase();
    return "UNSUBMITTED".equals(normalized) || "REJECTED".equals(normalized);
  }

  private String currentStepCodeByStatus(String certificationStatus) {
    return switch (certificationStatus) {
      case "UNSUBMITTED" -> "CERT_SUBMIT";
      case "PENDING_REVIEW", "REJECTED" -> "REVIEW";
      case "APPROVED" -> "ONBOARDING";
      default -> "CERT_SUBMIT";
    };
  }

  private int progressPercentByStatus(String certificationStatus) {
    return switch (certificationStatus) {
      case "UNSUBMITTED" -> 10;
      case "PENDING_REVIEW" -> 55;
      case "REJECTED" -> 40;
      case "APPROVED" -> 100;
      default -> 0;
    };
  }

  private String reviewStatusByCertificationStatus(String certificationStatus) {
    return switch (certificationStatus) {
      case "UNSUBMITTED" -> "PENDING";
      case "PENDING_REVIEW" -> "IN_PROGRESS";
      case "REJECTED", "APPROVED" -> "COMPLETED";
      default -> "PENDING";
    };
  }

  private String reviewStatusTextByCertificationStatus(String certificationStatus) {
    return switch (certificationStatus) {
      case "UNSUBMITTED" -> "待开始";
      case "PENDING_REVIEW" -> "进行中";
      case "REJECTED" -> "已驳回";
      case "APPROVED" -> "已通过";
      default -> "待开始";
    };
  }

  private String stepNameByCode(String stepCode) {
    return switch (stepCode) {
      case "CERT_SUBMIT" -> "提交企业认证";
      case "REVIEW" -> "平台审核中";
      case "ONBOARDING" -> "入驻完成";
      default -> "提交企业认证";
    };
  }

  private String estimateFinishAt(String certificationStatus, EnterpriseCertificationEntity cert) {
    if ("UNSUBMITTED".equalsIgnoreCase(certificationStatus)) {
      return null;
    }
    if ("PENDING_REVIEW".equalsIgnoreCase(certificationStatus)) {
      LocalDateTime base = cert == null ? LocalDateTime.now() : cert.getSubmittedAt();
      if (base == null) {
        base = LocalDateTime.now();
      }
      return toText(base.plusDays(2));
    }
    if ("APPROVED".equalsIgnoreCase(certificationStatus) || "REJECTED".equalsIgnoreCase(certificationStatus)) {
      return cert == null ? null : toText(cert.getUpdatedAt());
    }
    return null;
  }

  private N05NegotiationMessageDTO toMessageDTO(N05NegotiationMessageEntity entity) {
    return new N05NegotiationMessageDTO(
        entity.getMessageId(),
        entity.getSenderRole(),
        senderRoleText(entity.getSenderRole()),
        entity.getMessageType(),
        entity.getContent(),
        entity.getOfferPrice(),
        entity.getActionLabel(),
        toText(entity.getCreatedAt()));
  }

  private H5N04NegotiationDetailResponse toH5NegotiationDetail(
      N05NegotiationSessionEntity session, List<H5N04NegotiationMessageDTO> messages) {
    return new H5N04NegotiationDetailResponse(
        session.getSessionId(),
        session.getInquiryNo(),
        session.getProductName(),
        session.getSpecification(),
        session.getBuyerName(),
        session.getSupplierName(),
        session.getStatus(),
        sessionStatusText(session.getStatus()),
        session.getLatestOfferPrice(),
        session.getTargetPrice(),
        session.getRoundNo(),
        String.valueOf(session.getUnreadCount()),
        toText(session.getLatestMessageAt()),
        toText(session.getUpdatedAt()),
        session.getRoleInSession(),
        "H5",
        h5AvailableActions(session.getStatus()),
        messages);
  }

  private H5N04NegotiationMessageDTO toH5NegotiationMessage(N05NegotiationMessageEntity entity) {
    return new H5N04NegotiationMessageDTO(
        entity.getMessageId(),
        entity.getSenderRole(),
        senderRoleText(entity.getSenderRole()),
        entity.getMessageType(),
        entity.getContent(),
        entity.getOfferPrice(),
        entity.getActionLabel(),
        toText(entity.getCreatedAt()));
  }

  private String h5StatusActionText(String status) {
    return switch (safeText(status).toUpperCase()) {
      case "ONGOING" -> "转待确认";
      case "WAIT_CONFIRM" -> "标记达成";
      case "DEAL" -> "转成交";
      case "CLOSED" -> "恢复会话";
      default -> "查看详情";
    };
  }

  private H5N06PickupOrderDetailResponse toH5PickupOrderDetail(
      InquiryPickupOrderEntity item, String contactMobile, String scanResultText) {
    List<String> availableActions = h5PickupAvailableActions(item.getStatus().name());
    return new H5N06PickupOrderDetailResponse(
        item.getPickupId(),
        item.getPickupNo(),
        item.getInquiryId(),
        item.getInquiryNo(),
        item.getQuoteId(),
        item.getSupplierName(),
        item.getBuyerCompany(),
        item.getSpecText() + " / " + item.getQuantityTon() + "吨",
        item.getPickupAddress(),
        item.getPickupDate(),
        item.getTruckNo(),
        item.getDriverName(),
        item.getDriverPhoneMasked(),
        item.getStatus().name(),
        h5PickupStatusText(item.getStatus().name()),
        repository.maskPhone(contactMobile),
        "H5",
        scanResultText,
        availableActions,
        item.getRemark(),
        toText(item.getCreatedAt()),
        toText(item.getUpdatedAt()));
  }

  private H5N07ReconcileDetailResponse toH5ReconcileDetail(
      InquiryReconcileOrderEntity item, String tipText) {
    List<String> availableActions = h5ReconcileAvailableActions(item.getStatus().name());
    String invoiceStatus = h5ReconcileInvoiceStatus(item.getStatus().name());
    return new H5N07ReconcileDetailResponse(
        item.getReconcileId(),
        item.getReconcileNo(),
        item.getPickupOrderId(),
        item.getPickupOrderNo(),
        item.getInquiryId(),
        item.getInquiryNo(),
        item.getQuoteId(),
        item.getSupplierName(),
        item.getBuyerCompany(),
        item.getGoodsSummary(),
        item.getStatementMonth(),
        item.getDueDate(),
        item.getStatus().name(),
        h5ReconcileStatusText(item.getStatus().name()),
        invoiceStatus,
        h5ReconcileInvoiceStatusText(invoiceStatus),
        item.getReceivableAmount(),
        item.getPaidAmount(),
        item.getOutstandingAmount(),
        repository.maskPhone(item.getContactMobile()),
        "H5",
        item.getLatestRemark(),
        availableActions,
        toText(item.getCreatedAt()),
        toText(item.getUpdatedAt()));
  }

  private String h5PickupStatusText(String status) {
    return switch (safeText(status).toUpperCase()) {
      case "CREATED" -> "待确认";
      case "CONFIRMED" -> "已确认";
      case "IN_TRANSIT" -> "运输中";
      case "SIGNED" -> "已签收";
      case "COMPLETED" -> "已完成";
      case "CANCELLED" -> "已取消";
      default -> "处理中";
    };
  }

  private String h5PickupQuickActionText(String status) {
    return switch (safeText(status).toUpperCase()) {
      case "CREATED" -> "确认提货";
      case "CONFIRMED" -> "标记运输中";
      case "IN_TRANSIT" -> "标记已签收";
      case "SIGNED" -> "标记已完成";
      case "COMPLETED", "CANCELLED" -> "查看详情";
      default -> "查看详情";
    };
  }

  private List<String> h5PickupAvailableActions(String status) {
    return switch (safeText(status).toUpperCase()) {
      case "CREATED" -> List.of("CONFIRMED", "CANCELLED");
      case "CONFIRMED" -> List.of("IN_TRANSIT", "CANCELLED");
      case "IN_TRANSIT" -> List.of("SIGNED", "CANCELLED");
      case "SIGNED" -> List.of("COMPLETED");
      case "COMPLETED", "CANCELLED" -> List.of();
      default -> List.of();
    };
  }

  private String h5ReconcileStatusText(String status) {
    return switch (safeText(status).toUpperCase()) {
      case "CREATED" -> "已创建";
      case "INVOICE_PENDING" -> "待开票";
      case "INVOICED" -> "已开票";
      case "CONFIRMED" -> "已确认";
      case "PARTIAL_PAID" -> "部分回款";
      case "PAID" -> "已回款";
      case "CLOSED" -> "已关闭";
      case "DISPUTED" -> "争议中";
      default -> "处理中";
    };
  }

  private String h5ReconcileQuickActionText(String status) {
    return switch (safeText(status).toUpperCase()) {
      case "CREATED", "INVOICE_PENDING", "INVOICED" -> "标记已确认";
      case "CONFIRMED" -> "登记部分回款";
      case "PARTIAL_PAID" -> "标记已回款";
      case "PAID" -> "关闭对账单";
      case "DISPUTED" -> "恢复确认";
      case "CLOSED" -> "查看详情";
      default -> "查看详情";
    };
  }

  private List<String> h5ReconcileAvailableActions(String status) {
    return switch (safeText(status).toUpperCase()) {
      case "CREATED", "INVOICE_PENDING", "INVOICED" -> List.of("CONFIRMED", "DISPUTED");
      case "CONFIRMED" -> List.of("PARTIAL_PAID", "PAID", "DISPUTED");
      case "PARTIAL_PAID" -> List.of("PAID", "DISPUTED");
      case "PAID" -> List.of("CLOSED");
      case "DISPUTED" -> List.of("CONFIRMED", "CLOSED");
      case "CLOSED" -> List.of();
      default -> List.of();
    };
  }

  private H5N08AfterSaleDetailResponse toH5AfterSaleDetail(
      N08AfterSaleDisputeEntity item, String tipText) {
    List<String> availableActions = h5AfterSaleAvailableActions(item.getStatus());
    return new H5N08AfterSaleDetailResponse(
        item.getDisputeId(),
        item.getOrderId(),
        item.getOrderNo(),
        item.getInquiryNo(),
        item.getBuyerCompany(),
        item.getSupplierName(),
        item.getIssueType(),
        item.getIssueTypeText(),
        item.getIssueSummary(),
        item.getIssueDescription(),
        item.getExpectedResolution(),
        item.getContactName(),
        item.getContactPhoneMasked(),
        item.getEvidenceFiles(),
        item.getStatus(),
        item.getStatusText(),
        "H5",
        availableActions,
        item.getLatestRemark(),
        tipText,
        toText(item.getCreatedAt()),
        toText(item.getUpdatedAt()));
  }

  private H5N09LitePayDetailResponse toH5LitePayDetail(N10CashierOrderEntity item, String tipText) {
    String resultStatus = "PAID".equalsIgnoreCase(item.getPayStatus()) ? "SUCCESS" : "PENDING";
    String resultStatusText = "PAID".equalsIgnoreCase(item.getPayStatus()) ? "支付成功" : "待支付";
    List<String> availableActions = h5LitePayAvailableActions(item.getPayStatus());
    List<N10CashierTimelineNodeDTO> timeline =
        item.getTimeline().stream()
            .map(
                node ->
                    new N10CashierTimelineNodeDTO(
                        node.getNodeCode(),
                        node.getNodeName(),
                        node.getStatus(),
                        node.getStatusText(),
                        node.getHandler(),
                        node.getRemark(),
                        node.getHappenedAt()))
            .toList();
    return new H5N09LitePayDetailResponse(
        item.getCashierId(),
        item.getOrderId(),
        item.getOrderNo(),
        item.getInquiryNo(),
        item.getBuyerCompany(),
        item.getSupplierName(),
        item.getGoodsName(),
        item.getAmountPayable(),
        item.getAmountPaid(),
        item.getAmountOutstanding(),
        item.getPayStatus(),
        h5LitePayStatusText(item.getPayStatus()),
        item.getPayChannel(),
        item.getPayChannelText(),
        item.getPayStatus().equalsIgnoreCase("PAID") ? "" : toText(item.getUpdatedAt()),
        item.getPaidAt(),
        item.getLatestRemark(),
        "H5",
        resultStatus,
        resultStatusText,
        availableActions,
        tipText,
        toText(item.getCreatedAt()),
        toText(item.getUpdatedAt()),
        timeline);
  }

  private H5N10CreditBriefDetailResponse toH5CreditBriefDetail(
      N13CreditScoreEntity item, String tipText) {
    List<H5N10CreditBriefFactorDTO> factors =
        item.getFactors().stream()
            .map(
                factor ->
                    new H5N10CreditBriefFactorDTO(
                        factor.getFactorCode(),
                        factor.getFactorName(),
                        factor.getScore(),
                        factor.getWeight(),
                        factor.getTrend(),
                        factor.getSummary()))
            .toList();
    List<H5N10CreditBriefTrendPointDTO> trend =
        item.getTimeline().stream()
            .map(
                point ->
                    new H5N10CreditBriefTrendPointDTO(
                        point.getNodeCode(),
                        point.getImpactScore(),
                        point.getImpactDirection(),
                        point.getDescription(),
                        point.getHappenedAt()))
            .toList();
    return new H5N10CreditBriefDetailResponse(
        item.getScoreId(),
        item.getMerchantId(),
        item.getMerchantName(),
        item.getScoreMonth(),
        h5CreditTotalScore(item),
        h5CreditGrade(item),
        item.getRankPercent(),
        item.getRiskLevel(),
        h5CreditRiskLevelText(item.getRiskLevel()),
        String.join("、", item.getTags()),
        item.getScoreVersion(),
        item.getRiskSummary(),
        "H5",
        h5CreditAvailableActions(item.getRiskLevel()),
        item.getSuggestions(),
        factors,
        trend,
        tipText,
        toText(item.getUpdatedAt()));
  }

  private List<String> h5AfterSaleAvailableActions(String status) {
    return switch (safeText(status).toUpperCase()) {
      case "SUBMITTED" -> List.of("MARK_PROCESSING", "MARK_CLOSED");
      case "PROCESSING" -> List.of("MARK_RESOLVED", "MARK_CLOSED");
      case "RESOLVED" -> List.of("MARK_CLOSED", "REOPEN");
      case "CLOSED" -> List.of("REOPEN");
      default -> List.of("MARK_PROCESSING");
    };
  }

  private String h5AfterSaleQuickActionText(String status) {
    return switch (safeText(status).toUpperCase()) {
      case "SUBMITTED" -> "转处理中";
      case "PROCESSING" -> "标记已解决";
      case "RESOLVED" -> "关闭争议";
      case "CLOSED" -> "重新打开";
      default -> "查看详情";
    };
  }

  private String h5LitePayStatusText(String status) {
    return switch (safeText(status).toUpperCase()) {
      case "UNPAID" -> "待支付";
      case "PAID" -> "已支付";
      default -> "处理中";
    };
  }

  private String h5LitePayQuickActionText(String status) {
    return switch (safeText(status).toUpperCase()) {
      case "UNPAID" -> "立即支付";
      case "PAID" -> "查看结果";
      default -> "查看详情";
    };
  }

  private List<String> h5LitePayAvailableActions(String status) {
    return switch (safeText(status).toUpperCase()) {
      case "UNPAID" -> List.of("SUBMIT_PAY");
      case "PAID" -> List.of("VIEW_RESULT");
      default -> List.of("VIEW_RESULT");
    };
  }

  private String h5CreditTotalScore(N13CreditScoreEntity item) {
    String totalScore = safeText(item.getTotalScore());
    String grade = safeText(item.getGrade());
    if (totalScore.matches("^\\d{1,3}$")) {
      return totalScore;
    }
    if (grade.matches("^\\d{1,3}$")) {
      return grade;
    }
    return "0";
  }

  private String h5CreditGrade(N13CreditScoreEntity item) {
    String grade = safeText(item.getGrade()).toUpperCase();
    String totalScore = safeText(item.getTotalScore()).toUpperCase();
    if (grade.matches("A\\+|A-|A|B\\+|B|C")) {
      return grade;
    }
    if (totalScore.matches("A\\+|A-|A|B\\+|B|C")) {
      return totalScore;
    }
    return "B";
  }

  private String h5CreditRiskLevelText(String riskLevel) {
    return switch (safeText(riskLevel).toUpperCase()) {
      case "LOW" -> "低风险";
      case "MEDIUM" -> "中风险";
      case "HIGH" -> "高风险";
      default -> "风险待评估";
    };
  }

  private String h5CreditQuickActionText(String riskLevel, String grade) {
    String normalizedRisk = safeText(riskLevel).toUpperCase();
    String normalizedGrade = safeText(grade).toUpperCase();
    if (!normalizedGrade.matches("A\\+|A-|A|B\\+|B|C")) {
      normalizedGrade = "B";
    }
    if ("HIGH".equals(normalizedRisk)) {
      return "立即风控复核";
    }
    if ("MEDIUM".equals(normalizedRisk)) {
      return "跟进整改建议";
    }
    if (normalizedGrade.matches("A\\+|A")) {
      return "查看提升路径";
    }
    return "查看信用详情";
  }

  private List<String> h5CreditAvailableActions(String riskLevel) {
    return switch (safeText(riskLevel).toUpperCase()) {
      case "HIGH" -> List.of("RISK_RECHECK", "VIEW_FACTORS");
      case "MEDIUM" -> List.of("FOLLOW_SUGGESTIONS", "VIEW_FACTORS");
      case "LOW" -> List.of("VIEW_FACTORS", "TRACK_TREND");
      default -> List.of("VIEW_FACTORS");
    };
  }

  private String h5CreditTipText(String riskLevel) {
    return switch (safeText(riskLevel).toUpperCase()) {
      case "HIGH" -> "当前风险偏高，建议优先处理争议率与回款时效。";
      case "MEDIUM" -> "当前风险中等，可按建议清单持续优化履约表现。";
      case "LOW" -> "当前信用表现稳定，建议保持高质量履约与回款记录。";
      default -> "可查看信用因子与趋势，持续优化经营评分。";
    };
  }

  private String h5ReconcileInvoiceStatus(String status) {
    return switch (safeText(status).toUpperCase()) {
      case "CREATED", "INVOICE_PENDING" -> "UNISSUED";
      case "INVOICED", "CONFIRMED", "PARTIAL_PAID", "PAID", "CLOSED" -> "ISSUED";
      case "DISPUTED" -> "DISPUTED";
      default -> "UNISSUED";
    };
  }

  private String h5ReconcileInvoiceStatusText(String status) {
    return switch (safeText(status).toUpperCase()) {
      case "UNISSUED" -> "待开票";
      case "ISSUED" -> "已开票";
      case "DISPUTED" -> "争议中";
      default -> "待开票";
    };
  }

  private List<String> h5AvailableActions(String status) {
    return switch (safeText(status).toUpperCase()) {
      case "ONGOING" -> List.of("WAIT_CONFIRM", "CLOSED");
      case "WAIT_CONFIRM" -> List.of("DEAL", "ONGOING");
      case "DEAL" -> List.of("ONGOING", "CLOSED");
      case "CLOSED" -> List.of("ONGOING");
      default -> List.of("ONGOING");
    };
  }

  private String h5PaymentStatusText(String paymentStatus) {
    return switch (safeText(paymentStatus).toUpperCase()) {
      case "PAID", "FULLY_PAID" -> "已支付";
      case "PARTIAL_PAID" -> "部分支付";
      case "NONE", "UNPAID" -> "待支付";
      default -> "待支付";
    };
  }

  private boolean isPaidLike(String paymentStatus) {
    String normalized = safeText(paymentStatus).toUpperCase();
    return "PAID".equals(normalized) || "FULLY_PAID".equals(normalized);
  }

  private String sessionStatusText(String status) {
    return switch (status) {
      case "ONGOING" -> "议价中";
      case "WAIT_CONFIRM" -> "待确认";
      case "DEAL" -> "已达成";
      case "CLOSED" -> "已关闭";
      default -> "未知状态";
    };
  }

  private String senderRoleText(String senderRole) {
    return switch (senderRole) {
      case "BUYER" -> "采购方";
      case "SUPPLIER" -> "供应方";
      case "SYSTEM" -> "系统";
      default -> "未知";
    };
  }

  private String normalizeActionToStatus(String action) {
    String normalized = safeText(action).toUpperCase();
    return switch (normalized) {
      case "MARK_DEAL", "DEAL" -> "DEAL";
      case "MARK_WAIT_CONFIRM", "WAIT_CONFIRM" -> "WAIT_CONFIRM";
      case "MARK_CLOSED", "CLOSED", "CANCEL" -> "CLOSED";
      case "RESUME", "ONGOING" -> "ONGOING";
      default -> throw new BaseException(ErrorCode.BAD_REQUEST.getCode(), "action 不支持");
    };
  }

  private List<N06OrderActionDTO> orderActionsForStatus(String status) {
    return switch (safeText(status).toUpperCase()) {
      case "PENDING_SIGN" -> List.of(
          new N06OrderActionDTO("CONFIRM_SIGNED", "确认签署", true, ""),
          new N06OrderActionDTO("CLOSE_ORDER", "关闭订单", true, ""));
      case "SIGNED" -> List.of(
          new N06OrderActionDTO("MARK_PICKUP_IN_PROGRESS", "标记提货中", true, ""),
          new N06OrderActionDTO("CLOSE_ORDER", "关闭订单", true, ""));
      case "PICKUP_IN_PROGRESS" -> List.of(
          new N06OrderActionDTO("MARK_RECONCILING", "进入对账", true, ""),
          new N06OrderActionDTO("CLOSE_ORDER", "关闭订单", true, ""));
      case "RECONCILING" -> List.of(
          new N06OrderActionDTO("MARK_COMPLETED", "确认完结", true, ""),
          new N06OrderActionDTO("CLOSE_ORDER", "关闭订单", true, ""));
      case "CANCELLED" -> List.of(new N06OrderActionDTO("RESUME_ORDER", "恢复订单", true, ""));
      default -> List.of();
    };
  }

  private String mapOrderActionToStatus(String action) {
    return switch (safeText(action).toUpperCase()) {
      case "CONFIRM_SIGNED", "MARK_SIGNED" -> "SIGNED";
      case "MARK_PICKUP_IN_PROGRESS" -> "PICKUP_IN_PROGRESS";
      case "MARK_RECONCILING" -> "RECONCILING";
      case "MARK_COMPLETED", "CONFIRM_COMPLETED" -> "COMPLETED";
      case "CLOSE_ORDER" -> "CANCELLED";
      case "RESUME_ORDER" -> "SIGNED";
      default -> throw new BaseException(ErrorCode.BAD_REQUEST.getCode(), "action 不支持");
    };
  }

  private String mapOrderToPickup(String orderStatus) {
    return switch (safeText(orderStatus).toUpperCase()) {
      case "PENDING_SIGN" -> "CREATED";
      case "SIGNED", "PICKUP_IN_PROGRESS" -> "IN_TRANSIT";
      case "RECONCILING", "COMPLETED" -> "COMPLETED";
      case "CANCELLED" -> "CANCELLED";
      default -> "CREATED";
    };
  }

  private String mapOrderToReconcile(String orderStatus) {
    return switch (safeText(orderStatus).toUpperCase()) {
      case "RECONCILING" -> "PARTIAL_PAID";
      case "COMPLETED" -> "PAID";
      case "CANCELLED" -> "DISPUTED";
      default -> "CREATED";
    };
  }

  private String pickupStatusText(String status) {
    return switch (safeText(status).toUpperCase()) {
      case "CREATED" -> "待确认";
      case "IN_TRANSIT" -> "提货中";
      case "COMPLETED" -> "已完成";
      case "CANCELLED" -> "已取消";
      default -> "处理中";
    };
  }

  private String reconcileStatusText(String status) {
    return switch (safeText(status).toUpperCase()) {
      case "CREATED" -> "待对账";
      case "PARTIAL_PAID" -> "部分回款";
      case "PAID" -> "已回款";
      case "DISPUTED" -> "异常";
      default -> "处理中";
    };
  }

  private String mapAfterSaleActionToStatus(String action) {
    return switch (safeText(action).toUpperCase()) {
      case "MARK_PROCESSING", "PROCESSING" -> "PROCESSING";
      case "MARK_RESOLVED", "RESOLVED" -> "RESOLVED";
      case "MARK_CLOSED", "CLOSED" -> "CLOSED";
      case "REOPEN", "SUBMITTED" -> "SUBMITTED";
      default -> throw new BaseException(ErrorCode.BAD_REQUEST.getCode(), "action 不支持");
    };
  }

  private int progressPercentByAfterSaleStatus(String status) {
    return switch (safeText(status).toUpperCase()) {
      case "SUBMITTED" -> 25;
      case "PROCESSING" -> 60;
      case "RESOLVED" -> 90;
      case "CLOSED" -> 100;
      default -> 0;
    };
  }

  private String currentStageByStatus(String status) {
    return switch (safeText(status).toUpperCase()) {
      case "SUBMITTED" -> "争议提交";
      case "PROCESSING" -> "平台处理中";
      case "RESOLVED" -> "方案达成";
      case "CLOSED" -> "争议关闭";
      default -> "处理中";
    };
  }
}
