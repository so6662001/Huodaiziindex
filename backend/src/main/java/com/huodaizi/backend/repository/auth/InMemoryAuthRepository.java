package com.huodaizi.backend.repository.auth;

import com.huodaizi.backend.common.BaseException;
import com.huodaizi.backend.common.ErrorCode;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.UUID;
import java.util.Comparator;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import org.springframework.stereotype.Repository;

@Repository
public class InMemoryAuthRepository {
  private final ConcurrentMap<String, AuthUserEntity> userStore = new ConcurrentHashMap<>();
  private final ConcurrentMap<String, SessionEntity> sessionStore = new ConcurrentHashMap<>();
  private final ConcurrentMap<String, EnterpriseCertificationEntity> certificationStore =
      new ConcurrentHashMap<>();
  private final ConcurrentMap<String, N05NegotiationSessionEntity> negotiationStore =
      new ConcurrentHashMap<>();
  private final ConcurrentMap<String, N06OrderEntity> orderStore = new ConcurrentHashMap<>();
  private final ConcurrentMap<String, N07TradeTermsEntity> tradeTermsStore = new ConcurrentHashMap<>();
  private final ConcurrentMap<String, N08AfterSaleDisputeEntity> afterSaleStore = new ConcurrentHashMap<>();
  private final ConcurrentMap<String, N10CashierOrderEntity> cashierStore = new ConcurrentHashMap<>();
  private final ConcurrentMap<String, N12InvoiceTitleEntity> invoiceTitleStore = new ConcurrentHashMap<>();
  private final ConcurrentMap<String, N12InvoiceApplicationEntity> invoiceApplicationStore =
      new ConcurrentHashMap<>();
  private final ConcurrentMap<String, N13CreditScoreEntity> creditScoreStore = new ConcurrentHashMap<>();
  private final ConcurrentMap<String, N14DispatchAppealEntity> dispatchAppealStore = new ConcurrentHashMap<>();
  private final ConcurrentMap<String, H5LoginCodeEntity> h5LoginCodeStore = new ConcurrentHashMap<>();
  private final ConcurrentMap<String, H5N11MessageSettingsEntity> h5MessageSettingsStore =
      new ConcurrentHashMap<>();

  public InMemoryAuthRepository() {
    seed();
  }

  public AuthUserEntity register(AuthRegistration registration) {
    String normalizedMobile = normalizeMobile(registration.mobile());
    if (userStore.containsKey(normalizedMobile)) {
      throw new BaseException(ErrorCode.BAD_REQUEST.getCode(), "账号已存在");
    }
    String accountType = defaultText(registration.accountType(), "MOBILE").toUpperCase(Locale.ROOT);
    String companyName = defaultText(registration.companyName(), "-");
    String contactName = defaultText(registration.contactName(), "用户" + normalizedMobile.substring(7));
    AuthUserEntity entity =
        new AuthUserEntity(
            "U" + UUID.randomUUID().toString().replace("-", "").substring(0, 12),
            accountType,
            normalizedMobile,
            normalizedMobile,
            maskPhone(normalizedMobile),
            hashPassword(registration.password()),
            companyName,
            contactName,
            "BUYER",
            "ACTIVE",
            LocalDateTime.now(),
            LocalDateTime.now());
    userStore.put(normalizedMobile, entity);
    return entity;
  }

  public AuthUserEntity login(String mobile, String password) {
    AuthUserEntity user = requireUser(mobile);
    if (!"ACTIVE".equalsIgnoreCase(user.getStatus())) {
      throw new BaseException(ErrorCode.BAD_REQUEST.getCode(), "账号已禁用");
    }
    if (!user.getPasswordHash().equals(hashPassword(password))) {
      throw new BaseException(ErrorCode.BAD_REQUEST.getCode(), "账号或密码错误");
    }
    return user;
  }

  public H5LoginCodeEntity sendH5LoginCode(String mobile, String operator) {
    String normalizedMobile = normalizeMobile(mobile);
    AuthUserEntity user = userStore.get(normalizedMobile);
    if (user == null || !"ACTIVE".equalsIgnoreCase(user.getStatus())) {
      throw new BaseException(ErrorCode.NOT_FOUND.getCode(), "手机号未注册，请先注册账号");
    }
    LocalDateTime now = LocalDateTime.now();
    String code = "123456";
    H5LoginCodeEntity entity =
        new H5LoginCodeEntity(
            normalizedMobile,
            code,
            "H5CODE_" + UUID.randomUUID().toString().replace("-", "").substring(0, 12).toUpperCase(Locale.ROOT),
            60,
            now.plusMinutes(5),
            defaultText(operator, "h5-n01-send-code"),
            now);
    h5LoginCodeStore.put(normalizedMobile, entity);
    return entity;
  }

  public AuthUserEntity quickLoginByCode(String mobile, String smsCode) {
    String normalizedMobile = normalizeMobile(mobile);
    String normalizedCode = defaultText(smsCode, "");
    if (!normalizedCode.matches("^\\d{6}$")) {
      throw new BaseException(ErrorCode.BAD_REQUEST.getCode(), "smsCode 必须为6位数字");
    }
    H5LoginCodeEntity codeEntity = h5LoginCodeStore.get(normalizedMobile);
    if (codeEntity == null) {
      throw new BaseException(ErrorCode.BAD_REQUEST.getCode(), "请先获取验证码");
    }
    if (codeEntity.getExpireAt().isBefore(LocalDateTime.now())) {
      h5LoginCodeStore.remove(normalizedMobile);
      throw new BaseException(ErrorCode.BAD_REQUEST.getCode(), "验证码已过期，请重新获取");
    }
    if (!codeEntity.getSmsCode().equals(normalizedCode)
        && !codeEntity.getCodeToken().equalsIgnoreCase(normalizedCode)) {
      throw new BaseException(ErrorCode.BAD_REQUEST.getCode(), "验证码错误");
    }
    AuthUserEntity user = requireUser(normalizedMobile);
    h5LoginCodeStore.remove(normalizedMobile);
    return user;
  }

  public SessionEntity createSession(AuthUserEntity user, String channel) {
    LocalDateTime now = LocalDateTime.now();
    String token =
        "N01_"
            + Base64.getUrlEncoder()
                .withoutPadding()
                .encodeToString(
                    (user.getUserId() + ":" + now + ":" + UUID.randomUUID())
                        .getBytes(StandardCharsets.UTF_8));
    SessionEntity session =
        new SessionEntity(
            token,
            user.getUserId(),
            user.getAccount(),
            user.getRole(),
            user.getRole(),
            defaultText(channel, "PC"),
            now.plusDays(7),
            now,
            now);
    sessionStore.put(token, session);
    return session;
  }

  public Optional<AuthUserEntity> findByToken(String token) {
    if (token == null || token.isBlank()) {
      return Optional.empty();
    }
    SessionEntity session = sessionStore.get(token.trim());
    if (session == null) {
      return Optional.empty();
    }
    if (session.getExpireAt().isBefore(LocalDateTime.now())) {
      sessionStore.remove(token.trim());
      return Optional.empty();
    }
    AuthUserEntity user = userStore.get(session.getAccount());
    if (user == null || !"ACTIVE".equalsIgnoreCase(user.getStatus())) {
      return Optional.empty();
    }
    return Optional.of(user);
  }

  public SessionEntity requireSession(String token) {
    SessionEntity session = sessionStore.get(defaultText(token, ""));
    if (session == null) {
      throw new BaseException(ErrorCode.UNAUTHORIZED.getCode(), "登录态不存在");
    }
    if (session.getExpireAt().isBefore(LocalDateTime.now())) {
      sessionStore.remove(token.trim());
      throw new BaseException(ErrorCode.UNAUTHORIZED.getCode(), "登录态已过期");
    }
    AuthUserEntity user = userStore.get(session.getAccount());
    if (user == null || !"ACTIVE".equalsIgnoreCase(user.getStatus())) {
      sessionStore.remove(token.trim());
      throw new BaseException(ErrorCode.UNAUTHORIZED.getCode(), "登录态无效");
    }
    return session;
  }

  public SessionEntity switchIdentity(String token, String identityCode) {
    SessionEntity session = requireSession(token);
    String normalized = normalizeRole(identityCode);
    AuthUserEntity user = userStore.get(session.getAccount());
    if (user == null || !user.getIdentityCodes().contains(normalized)) {
      throw new BaseException(ErrorCode.BAD_REQUEST.getCode(), "当前账号不支持该身份");
    }
    SessionEntity updated =
        new SessionEntity(
            session.getToken(),
            session.getUserId(),
            session.getAccount(),
            session.getDefaultRoleCode(),
            normalized,
            session.getChannel(),
            session.getExpireAt(),
            session.getCreatedAt(),
            LocalDateTime.now());
    sessionStore.put(updated.getToken(), updated);
    return updated;
  }

  public List<String> identityCodes() {
    return List.of("BUYER", "SUPPLIER", "OPERATOR");
  }

  public EnterpriseCertificationEntity saveCertification(
      String token, EnterpriseCertificationDraft draft) {
    SessionEntity session = requireSession(token);
    AuthUserEntity user = userStore.get(session.getAccount());
    if (user == null) {
      throw new BaseException(ErrorCode.UNAUTHORIZED.getCode(), "登录态无效");
    }
    EnterpriseCertificationEntity current = certificationStore.get(user.getUserId());
    LocalDateTime now = LocalDateTime.now();
    String certificationId =
        current == null
            ? "EC" + UUID.randomUUID().toString().replace("-", "").substring(0, 10)
            : current.getCertificationId();
    EnterpriseCertificationEntity saved =
        new EnterpriseCertificationEntity(
            certificationId,
            user.getUserId(),
            user.getAccount(),
            "PENDING_REVIEW",
            defaultText(draft.companyName(), user.getCompanyName()),
            draft.unifiedSocialCreditCode(),
            draft.legalPersonName(),
            draft.legalPersonIdNo(),
            defaultText(draft.contactName(), user.getContactName()),
            draft.contactMobile(),
            maskPhone(draft.contactMobile()),
            draft.businessLicenseUrl(),
            draft.legalIdFrontUrl(),
            draft.legalIdBackUrl(),
            draft.bankAccountName(),
            draft.bankAccountNo(),
            draft.bankName(),
            draft.province(),
            draft.city(),
            draft.address(),
            draft.remark(),
            defaultText(draft.operator(), "n03-pc-submit"),
            current == null ? now : current.getCreatedAt(),
            now,
            now);
    certificationStore.put(user.getUserId(), saved);
    return saved;
  }

  public Optional<EnterpriseCertificationEntity> findCertificationByToken(String token) {
    SessionEntity session = requireSession(token);
    AuthUserEntity user = userStore.get(session.getAccount());
    if (user == null) {
      throw new BaseException(ErrorCode.UNAUTHORIZED.getCode(), "登录态无效");
    }
    return Optional.ofNullable(certificationStore.get(user.getUserId()));
  }

  public List<EnterpriseCertificationEntity> listAllCertifications(
      String status, String keyword, int page, int pageSize) {
    String statusFilter = defaultText(status, "").toUpperCase(Locale.ROOT);
    String keywordFilter = defaultText(keyword, "").toLowerCase(Locale.ROOT);
    return certificationStore.values().stream()
        .filter(
            item ->
                statusFilter.isBlank() || statusFilter.equals(item.getStatus().toUpperCase(Locale.ROOT)))
        .filter(
            item ->
                keywordFilter.isBlank()
                    || defaultText(item.getCertificationId(), "").toLowerCase(Locale.ROOT).contains(keywordFilter)
                    || defaultText(item.getCompanyName(), "").toLowerCase(Locale.ROOT).contains(keywordFilter)
                    || defaultText(item.getUnifiedSocialCreditCode(), "")
                        .toLowerCase(Locale.ROOT)
                        .contains(keywordFilter)
                    || defaultText(item.getUserId(), "").toLowerCase(Locale.ROOT).contains(keywordFilter)
                    || defaultText(item.getAccount(), "").toLowerCase(Locale.ROOT).contains(keywordFilter))
        .sorted(Comparator.comparing(EnterpriseCertificationEntity::getUpdatedAt).reversed())
        .toList();
  }

  public EnterpriseCertificationEntity getCertificationById(String certificationId) {
    String normalizedId = defaultText(certificationId, "");
    if (normalizedId.isBlank()) {
      throw new BaseException(ErrorCode.BAD_REQUEST.getCode(), "certificationId 不能为空");
    }
    EnterpriseCertificationEntity entity =
        certificationStore.values().stream()
            .filter(item -> normalizedId.equals(item.getCertificationId()))
            .findFirst()
            .orElse(null);
    if (entity == null) {
      throw new BaseException(ErrorCode.NOT_FOUND.getCode(), "认证单不存在");
    }
    return entity;
  }

  public EnterpriseCertificationEntity reviewCertification(
      String certificationId, String action, String remark, String operator) {
    EnterpriseCertificationEntity target = getCertificationById(certificationId);
    String normalizedAction = defaultText(action, "").toUpperCase(Locale.ROOT);
    String nextStatus =
        switch (normalizedAction) {
          case "APPROVE" -> "APPROVED";
          case "REJECT" -> "REJECTED";
          default -> throw new BaseException(ErrorCode.BAD_REQUEST.getCode(), "action 仅支持 APPROVE/REJECT");
        };
    LocalDateTime now = LocalDateTime.now();
    target.refreshFromSubmission(
        nextStatus,
        target.getCompanyName(),
        target.getUnifiedSocialCreditCode(),
        target.getLegalPersonName(),
        target.getLegalPersonIdNo(),
        target.getContactName(),
        target.getContactMobile(),
        target.getContactMobileMasked(),
        target.getBusinessLicenseUrl(),
        target.getLegalIdFrontUrl(),
        target.getLegalIdBackUrl(),
        target.getBankAccountName(),
        target.getBankAccountNo(),
        target.getBankName(),
        target.getProvince(),
        target.getCity(),
        target.getAddress(),
        defaultText(remark, target.getRemark()),
        defaultText(operator, "admn01-review"),
        now);
    certificationStore.put(target.getUserId(), target);
    return target;
  }

  public List<EnterpriseCertificationEntity> listCertificationsForAdmin(String status, String keyword) {
    return listAllCertifications(status, keyword, 1, 500);
  }

  public EnterpriseCertificationEntity getCertificationByIdForAdmin(String certificationId) {
    return getCertificationById(certificationId);
  }

  public EnterpriseCertificationEntity reviewCertificationForAdmin(
      String certificationId, String action, String reviewRemark, String reviewer) {
    return reviewCertification(certificationId, action, reviewRemark, reviewer);
  }

  public Optional<AuthUserEntity> findUserByToken(String token) {
    return findByToken(token);
  }

  public List<N05NegotiationSessionEntity> listNegotiations(String token, N05NegotiationQuery query) {
    SessionEntity session = requireSession(token);
    AuthUserEntity user = userStore.get(session.getAccount());
    if (user == null) {
      throw new BaseException(ErrorCode.UNAUTHORIZED.getCode(), "登录态无效");
    }
    String statusFilter = query == null ? "" : defaultText(query.status(), "");
    String keyword = query == null ? "" : defaultText(query.keyword(), "");
    return negotiationStore.values().stream()
        .filter(item -> user.getUserId().equals(item.getUserId()))
        .filter(item -> statusFilter.isBlank() || statusFilter.equalsIgnoreCase(item.getStatus()))
        .filter(
            item ->
                keyword.isBlank()
                    || item.getSessionNo().contains(keyword)
                    || item.getInquiryNo().contains(keyword)
                    || item.getInquiryTitle().contains(keyword)
                    || item.getProductName().contains(keyword)
                    || item.getCounterpartyName().contains(keyword))
        .sorted(Comparator.comparing(N05NegotiationSessionEntity::getUpdatedAt).reversed())
        .toList();
  }

  public N05NegotiationSessionEntity getNegotiationDetail(String token, String sessionId) {
    SessionEntity session = requireSession(token);
    AuthUserEntity user = userStore.get(session.getAccount());
    if (user == null) {
      throw new BaseException(ErrorCode.UNAUTHORIZED.getCode(), "登录态无效");
    }
    N05NegotiationSessionEntity entity = negotiationStore.get(defaultText(sessionId, ""));
    if (entity == null || !user.getUserId().equals(entity.getUserId())) {
      throw new BaseException(ErrorCode.NOT_FOUND.getCode(), "议价会话不存在");
    }
    return entity;
  }

  public N05NegotiationSessionEntity appendNegotiationMessage(
      String token, String sessionId, String senderRole, String content, String operator) {
    N05NegotiationSessionEntity entity = getNegotiationDetail(token, sessionId);
    String normalizedRole = defaultText(senderRole, "").trim().toUpperCase(Locale.ROOT);
    if (!normalizedRole.matches("BUYER|SUPPLIER")) {
      throw new BaseException(ErrorCode.BAD_REQUEST.getCode(), "senderRole 仅支持 BUYER/SUPPLIER");
    }
    LocalDateTime now = LocalDateTime.now();
    N05NegotiationMessageEntity message =
        new N05NegotiationMessageEntity(
            "MSG" + UUID.randomUUID().toString().replace("-", "").substring(0, 10),
            normalizedRole,
            "TEXT",
            defaultText(content, ""),
            "",
            "SEND",
            "",
            now);
    entity.appendMessage(message);
    return entity;
  }

  public N05NegotiationSessionEntity updateNegotiationStatus(
      String token, String sessionId, String status, String operator) {
    N05NegotiationSessionEntity entity = getNegotiationDetail(token, sessionId);
    String normalized = defaultText(status, "").trim().toUpperCase(Locale.ROOT);
    if (!normalized.matches("ONGOING|WAIT_CONFIRM|DEAL|CLOSED")) {
      throw new BaseException(ErrorCode.BAD_REQUEST.getCode(), "action 仅支持 ONGOING/WAIT_CONFIRM/DEAL/CLOSED");
    }
    entity.updateStatus(normalized, LocalDateTime.now());
    entity.appendMessage(
        new N05NegotiationMessageEntity(
            "MSG" + UUID.randomUUID().toString().replace("-", "").substring(0, 10),
            "SYSTEM",
            "SYSTEM",
            "会话状态已更新为：" + normalized + "（" + defaultText(operator, "n05-pc-update") + "）",
            "",
            "STATUS_UPDATE",
            "",
            LocalDateTime.now()));
    return entity;
  }

  public List<N06OrderEntity> listOrders(String token, N06OrderQuery query) {
    SessionEntity session = requireSession(token);
    AuthUserEntity user = userStore.get(session.getAccount());
    if (user == null) {
      throw new BaseException(ErrorCode.UNAUTHORIZED.getCode(), "登录态无效");
    }
    String statusFilter = query == null ? "" : defaultText(query.status(), "");
    String keyword = query == null ? "" : defaultText(query.keyword(), "");
    return orderStore.values().stream()
        .filter(item -> user.getUserId().equals(item.getUserId()))
        .filter(item -> statusFilter.isBlank() || statusFilter.equalsIgnoreCase(item.getOrderStatus()))
        .filter(
            item ->
                keyword.isBlank()
                    || item.getOrderNo().contains(keyword)
                    || item.getInquiryNo().contains(keyword)
                    || item.getSupplierName().contains(keyword)
                    || item.getGoodsName().contains(keyword))
        .sorted(Comparator.comparing(N06OrderEntity::getUpdatedAt).reversed())
        .toList();
  }

  public N06OrderEntity getOrderDetail(String token, String orderId) {
    SessionEntity session = requireSession(token);
    AuthUserEntity user = userStore.get(session.getAccount());
    if (user == null) {
      throw new BaseException(ErrorCode.UNAUTHORIZED.getCode(), "登录态无效");
    }
    N06OrderEntity entity = orderStore.get(defaultText(orderId, ""));
    if (entity == null || !user.getUserId().equals(entity.getUserId())) {
      throw new BaseException(ErrorCode.NOT_FOUND.getCode(), "订单不存在");
    }
    return entity;
  }

  public N06OrderEntity updateOrderStatus(
      String token, String orderId, String targetStatus, String operator, String remark) {
    N06OrderEntity entity = getOrderDetail(token, orderId);
    String normalized = defaultText(targetStatus, "").trim().toUpperCase(Locale.ROOT);
    if (!normalized.matches("PENDING_SIGN|SIGNED|PICKUP_IN_PROGRESS|RECONCILING|COMPLETED|CANCELLED")) {
      throw new BaseException(
          ErrorCode.BAD_REQUEST.getCode(),
          "targetStatus 仅支持 PENDING_SIGN/SIGNED/PICKUP_IN_PROGRESS/RECONCILING/COMPLETED/CANCELLED");
    }
    String normalizedOperator = defaultText(operator, "pc-n06-action");
    String normalizedRemark = defaultText(remark, "");
    LocalDateTime now = LocalDateTime.now();
    entity.updateStatus(normalized, normalizedOperator, now, normalizedRemark);
    return entity;
  }

  public N07TradeTermsEntity getTradeTerms(String token, String orderId) {
    SessionEntity session = requireSession(token);
    AuthUserEntity user = userStore.get(session.getAccount());
    if (user == null) {
      throw new BaseException(ErrorCode.UNAUTHORIZED.getCode(), "登录态无效");
    }
    N07TradeTermsEntity entity = tradeTermsStore.get(defaultText(orderId, ""));
    if (entity == null || !user.getUserId().equals(entity.getUserId())) {
      throw new BaseException(ErrorCode.NOT_FOUND.getCode(), "交易条款不存在");
    }
    return entity;
  }

  public N07TradeTermsEntity confirmTradeTerms(
      String token, String orderId, String operator, String remark) {
    N07TradeTermsEntity entity = getTradeTerms(token, orderId);
    String normalizedOperator = defaultText(operator, "pc-n07-confirm");
    String normalizedRemark = defaultText(remark, "");
    entity.confirm(normalizedOperator, normalizedRemark, LocalDateTime.now());
    return entity;
  }

  public List<N08AfterSaleDisputeEntity> listAfterSaleDisputes(String token, N08AfterSaleQuery query) {
    SessionEntity session = requireSession(token);
    AuthUserEntity user = userStore.get(session.getAccount());
    if (user == null) {
      throw new BaseException(ErrorCode.UNAUTHORIZED.getCode(), "登录态无效");
    }
    String statusFilter = query == null ? "" : defaultText(query.status(), "");
    String keyword = query == null ? "" : defaultText(query.keyword(), "");
    return afterSaleStore.values().stream()
        .filter(item -> user.getUserId().equals(item.getUserId()))
        .filter(item -> statusFilter.isBlank() || statusFilter.equalsIgnoreCase(item.getStatus()))
        .filter(
            item ->
                keyword.isBlank()
                    || item.getDisputeId().contains(keyword)
                    || item.getOrderNo().contains(keyword)
                    || item.getInquiryNo().contains(keyword)
                    || item.getIssueSummary().contains(keyword)
                    || item.getSupplierName().contains(keyword))
        .sorted(Comparator.comparing(N08AfterSaleDisputeEntity::getUpdatedAt).reversed())
        .toList();
  }

  public N08AfterSaleDisputeEntity getAfterSaleDisputeDetail(String token, String disputeId) {
    SessionEntity session = requireSession(token);
    AuthUserEntity user = userStore.get(session.getAccount());
    if (user == null) {
      throw new BaseException(ErrorCode.UNAUTHORIZED.getCode(), "登录态无效");
    }
    N08AfterSaleDisputeEntity entity = afterSaleStore.get(defaultText(disputeId, ""));
    if (entity == null || !user.getUserId().equals(entity.getUserId())) {
      throw new BaseException(ErrorCode.NOT_FOUND.getCode(), "售后争议单不存在");
    }
    return entity;
  }

  public N08AfterSaleDisputeEntity createAfterSaleDispute(
      String token,
      String orderId,
      String issueType,
      String issueSummary,
      String issueDescription,
      String expectedResolution,
      String contactName,
      String contactPhone,
      String evidenceFiles,
      String operator) {
    N06OrderEntity order = getOrderDetail(token, orderId);
    String normalizedIssueType = defaultText(issueType, "").trim().toUpperCase(Locale.ROOT);
    if (!normalizedIssueType.matches("QUALITY|DELIVERY_DELAY|INVOICE|PAYMENT|OTHER")) {
      throw new BaseException(
          ErrorCode.BAD_REQUEST.getCode(), "issueType 仅支持 QUALITY/DELIVERY_DELAY/INVOICE/PAYMENT/OTHER");
    }
    String normalizedPhone = defaultText(contactPhone, "").replaceAll("\\D", "");
    if (!normalizedPhone.matches("^1\\d{10}$")) {
      throw new BaseException(ErrorCode.BAD_REQUEST.getCode(), "contactPhone 必须为11位手机号");
    }
    LocalDateTime now = LocalDateTime.now();
    String disputeId = "AS" + UUID.randomUUID().toString().replace("-", "").substring(0, 10).toUpperCase(Locale.ROOT);
    N08AfterSaleDisputeEntity entity =
        new N08AfterSaleDisputeEntity(
            disputeId,
            order.getUserId(),
            order.getOrderId(),
            order.getOrderNo(),
            order.getInquiryNo(),
            order.getBuyerCompany(),
            order.getSupplierName(),
            normalizedIssueType,
            issueTypeText(normalizedIssueType),
            defaultText(issueSummary, ""),
            defaultText(issueDescription, ""),
            defaultText(expectedResolution, ""),
            defaultText(contactName, ""),
            maskPhone(normalizedPhone),
            defaultText(evidenceFiles, ""),
            "SUBMITTED",
            "已提交",
            "争议已提交，待平台处理",
            now,
            now);
    afterSaleStore.put(entity.getDisputeId(), entity);
    return entity;
  }

  public N08AfterSaleDisputeEntity updateAfterSaleDisputeStatus(
      String token, String disputeId, String status, String operator, String remark) {
    N08AfterSaleDisputeEntity entity = getAfterSaleDisputeDetail(token, disputeId);
    String normalized = defaultText(status, "").trim().toUpperCase(Locale.ROOT);
    if (!normalized.matches("SUBMITTED|PROCESSING|RESOLVED|CLOSED")) {
      throw new BaseException(
          ErrorCode.BAD_REQUEST.getCode(), "status 仅支持 SUBMITTED/PROCESSING/RESOLVED/CLOSED");
    }
    String normalizedRemark = defaultText(remark, "");
    entity.updateStatus(
        normalized,
        disputeStatusText(normalized),
        normalizedRemark,
        defaultText(operator, "n08-system"),
        LocalDateTime.now());
    return entity;
  }

  public List<N10CashierOrderEntity> listCashierOrders(String token, N10CashierQuery query) {
    SessionEntity session = requireSession(token);
    AuthUserEntity user = userStore.get(session.getAccount());
    if (user == null) {
      throw new BaseException(ErrorCode.UNAUTHORIZED.getCode(), "登录态无效");
    }
    String statusFilter = query == null ? "" : defaultText(query.status(), "");
    String keyword = query == null ? "" : defaultText(query.keyword(), "");
    return cashierStore.values().stream()
        .filter(item -> user.getUserId().equals(item.getUserId()))
        .filter(item -> statusFilter.isBlank() || statusFilter.equalsIgnoreCase(item.getPayStatus()))
        .filter(
            item ->
                keyword.isBlank()
                    || item.getCashierId().contains(keyword)
                    || item.getOrderNo().contains(keyword)
                    || item.getInquiryNo().contains(keyword)
                    || item.getSupplierName().contains(keyword)
                    || item.getGoodsName().contains(keyword))
        .sorted(Comparator.comparing(N10CashierOrderEntity::getUpdatedAt).reversed())
        .toList();
  }

  public N10CashierOrderEntity getCashierOrderDetail(String token, String cashierId) {
    SessionEntity session = requireSession(token);
    AuthUserEntity user = userStore.get(session.getAccount());
    if (user == null) {
      throw new BaseException(ErrorCode.UNAUTHORIZED.getCode(), "登录态无效");
    }
    N10CashierOrderEntity entity = cashierStore.get(defaultText(cashierId, ""));
    if (entity == null || !user.getUserId().equals(entity.getUserId())) {
      throw new BaseException(ErrorCode.NOT_FOUND.getCode(), "收银单不存在");
    }
    return entity;
  }

  public N10CashierOrderEntity payCashierOrder(
      String token, String cashierId, String payMethod, String payerName, String remark, String operator) {
    N10CashierOrderEntity entity = getCashierOrderDetail(token, cashierId);
    String normalizedMethod = defaultText(payMethod, "").trim().toUpperCase(Locale.ROOT);
    if (!normalizedMethod.matches("BANK_TRANSFER|ALIPAY|WECHAT|UNIONPAY")) {
      throw new BaseException(
          ErrorCode.BAD_REQUEST.getCode(), "payMethod 仅支持 BANK_TRANSFER/ALIPAY/WECHAT/UNIONPAY");
    }
    if ("PAID".equalsIgnoreCase(entity.getPayStatus())) {
      throw new BaseException(ErrorCode.BAD_REQUEST.getCode(), "当前收银单已支付");
    }
    String channelText = payChannelText(normalizedMethod);
    String payer = defaultText(payerName, "采购方财务");
    String normalizedRemark = defaultText(remark, "收银台支付完成");
    entity.markPaid(
        normalizedMethod,
        channelText,
        entity.getAmountPayable(),
        "0",
        normalizedRemark,
        LocalDateTime.now(),
        defaultText(operator, payer));
    N06OrderEntity order = orderStore.get(entity.getOrderId());
    if (order != null && !"COMPLETED".equalsIgnoreCase(order.getOrderStatus())) {
      order.updateStatus("COMPLETED", defaultText(operator, "n10-cashier"), LocalDateTime.now(), normalizedRemark);
    }
    return entity;
  }

  public List<N12InvoiceTitleEntity> listInvoiceTitles(String token, String status) {
    SessionEntity session = requireSession(token);
    AuthUserEntity user = userStore.get(session.getAccount());
    if (user == null) {
      throw new BaseException(ErrorCode.UNAUTHORIZED.getCode(), "登录态无效");
    }
    String statusFilter = defaultText(status, "");
    return invoiceTitleStore.values().stream()
        .filter(item -> user.getUserId().equals(item.getUserId()))
        .filter(item -> statusFilter.isBlank() || statusFilter.equalsIgnoreCase(item.getStatus()))
        .sorted(Comparator.comparing(N12InvoiceTitleEntity::getUpdatedAt).reversed())
        .toList();
  }

  public N12InvoiceTitleEntity getInvoiceTitle(String token, String titleId) {
    SessionEntity session = requireSession(token);
    AuthUserEntity user = userStore.get(session.getAccount());
    if (user == null) {
      throw new BaseException(ErrorCode.UNAUTHORIZED.getCode(), "登录态无效");
    }
    N12InvoiceTitleEntity entity = invoiceTitleStore.get(defaultText(titleId, ""));
    if (entity == null || !user.getUserId().equals(entity.getUserId())) {
      throw new BaseException(ErrorCode.NOT_FOUND.getCode(), "发票抬头不存在");
    }
    return entity;
  }

  public N12InvoiceTitleEntity saveInvoiceTitle(
      String token,
      String titleId,
      String titleName,
      String taxNo,
      String registerAddress,
      String registerPhone,
      String bankName,
      String bankAccountNo,
      boolean defaultTitle,
      String operator) {
    SessionEntity session = requireSession(token);
    AuthUserEntity user = userStore.get(session.getAccount());
    if (user == null) {
      throw new BaseException(ErrorCode.UNAUTHORIZED.getCode(), "登录态无效");
    }

    String normalizedName = defaultText(titleName, "");
    if (normalizedName.isBlank()) {
      throw new BaseException(ErrorCode.BAD_REQUEST.getCode(), "titleName 不能为空");
    }
    String normalizedTaxNo = defaultText(taxNo, "").trim().toUpperCase(Locale.ROOT);
    if (normalizedTaxNo.isBlank()) {
      throw new BaseException(ErrorCode.BAD_REQUEST.getCode(), "taxNo 不能为空");
    }
    String normalizedAddress = defaultText(registerAddress, "");
    if (normalizedAddress.isBlank()) {
      throw new BaseException(ErrorCode.BAD_REQUEST.getCode(), "address 不能为空");
    }
    String normalizedPhone = defaultText(registerPhone, "").replaceAll("\\D", "");
    if (!normalizedPhone.matches("^1\\d{10}$")) {
      throw new BaseException(ErrorCode.BAD_REQUEST.getCode(), "phone 必须为11位手机号");
    }

    N12InvoiceTitleEntity current =
        titleId == null || titleId.isBlank() ? null : invoiceTitleStore.get(titleId.trim());
    if (current != null && !user.getUserId().equals(current.getUserId())) {
      throw new BaseException(ErrorCode.NOT_FOUND.getCode(), "发票抬头不存在");
    }

    LocalDateTime now = LocalDateTime.now();
    String finalId =
        current == null
            ? "IT" + UUID.randomUUID().toString().replace("-", "").substring(0, 10).toUpperCase(Locale.ROOT)
            : current.getTitleId();
    boolean finalDefault = defaultTitle || (current != null && current.isDefaultTitle());
    String status = current == null ? "ACTIVE" : current.getStatus();

    N12InvoiceTitleEntity saved =
        new N12InvoiceTitleEntity(
            finalId,
            user.getUserId(),
            "COMPANY",
            "企业抬头",
            normalizedName,
            normalizedTaxNo,
            defaultText(bankName, ""),
            defaultText(bankAccountNo, ""),
            normalizedAddress,
            normalizedPhone,
            current == null ? "" : current.getEmail(),
            finalDefault,
            status,
            titleStatusText(status),
            defaultText(operator, "n12-save-title"),
            current == null ? now : current.getCreatedAt(),
            now);
    invoiceTitleStore.put(saved.getTitleId(), saved);

    if (finalDefault) {
      invoiceTitleStore.values().stream()
          .filter(item -> user.getUserId().equals(item.getUserId()))
          .filter(item -> !item.getTitleId().equals(saved.getTitleId()))
          .forEach(item -> item.setDefaultTitle(false, now));
    }

    boolean hasDefault =
        invoiceTitleStore.values().stream()
            .anyMatch(item -> user.getUserId().equals(item.getUserId()) && item.isDefaultTitle());
    if (!hasDefault) {
      saved.setDefaultTitle(true, now);
    }
    return saved;
  }

  public N12InvoiceTitleEntity setInvoiceTitleDefault(String token, String titleId, String operator) {
    N12InvoiceTitleEntity target = getInvoiceTitle(token, titleId);
    if (!"ACTIVE".equalsIgnoreCase(target.getStatus())) {
      throw new BaseException(ErrorCode.BAD_REQUEST.getCode(), "仅启用中的抬头可设为默认");
    }
    SessionEntity session = requireSession(token);
    AuthUserEntity user = userStore.get(session.getAccount());
    LocalDateTime now = LocalDateTime.now();
    invoiceTitleStore.values().stream()
        .filter(item -> user.getUserId().equals(item.getUserId()))
        .forEach(item -> item.setDefaultTitle(item.getTitleId().equals(target.getTitleId()), now));
    target.setStatus(target.getStatus(), target.getStatusText(), defaultText(operator, "n12-set-default"), now);
    return target;
  }

  public N12InvoiceTitleEntity updateInvoiceTitleStatus(
      String token, String titleId, String status, String operator) {
    N12InvoiceTitleEntity target = getInvoiceTitle(token, titleId);
    String normalizedStatus = defaultText(status, "").trim().toUpperCase(Locale.ROOT);
    if (!normalizedStatus.matches("ACTIVE|INACTIVE")) {
      throw new BaseException(ErrorCode.BAD_REQUEST.getCode(), "status 仅支持 ACTIVE/INACTIVE");
    }
    LocalDateTime now = LocalDateTime.now();
    target.setStatus(
        normalizedStatus, titleStatusText(normalizedStatus), defaultText(operator, "n12-update-status"), now);
    if ("INACTIVE".equals(normalizedStatus)) {
      target.setDefaultTitle(false, now);
      SessionEntity session = requireSession(token);
      AuthUserEntity user = userStore.get(session.getAccount());
      invoiceTitleStore.values().stream()
          .filter(item -> user.getUserId().equals(item.getUserId()))
          .filter(item -> "ACTIVE".equalsIgnoreCase(item.getStatus()))
          .findFirst()
          .ifPresent(item -> item.setDefaultTitle(true, now));
    }
    return target;
  }

  public List<N12InvoiceApplicationEntity> listInvoiceApplications(
      String token, N12InvoiceApplicationQuery query) {
    SessionEntity session = requireSession(token);
    AuthUserEntity user = userStore.get(session.getAccount());
    if (user == null) {
      throw new BaseException(ErrorCode.UNAUTHORIZED.getCode(), "登录态无效");
    }
    String statusFilter = query == null ? "" : defaultText(query.status(), "");
    String keyword = query == null ? "" : defaultText(query.keyword(), "");
    return invoiceApplicationStore.values().stream()
        .filter(item -> user.getUserId().equals(item.getUserId()))
        .filter(item -> statusFilter.isBlank() || statusFilter.equalsIgnoreCase(item.getStatus()))
        .filter(
            item ->
                keyword.isBlank()
                    || item.getApplicationId().contains(keyword)
                    || item.getOrderNo().contains(keyword)
                    || item.getInquiryNo().contains(keyword)
                    || item.getInvoiceTypeText().contains(keyword))
        .sorted(Comparator.comparing(N12InvoiceApplicationEntity::getUpdatedAt).reversed())
        .toList();
  }

  public N12InvoiceApplicationEntity getInvoiceApplicationDetail(String token, String applicationId) {
    SessionEntity session = requireSession(token);
    AuthUserEntity user = userStore.get(session.getAccount());
    if (user == null) {
      throw new BaseException(ErrorCode.UNAUTHORIZED.getCode(), "登录态无效");
    }
    N12InvoiceApplicationEntity entity = invoiceApplicationStore.get(defaultText(applicationId, ""));
    if (entity == null || !user.getUserId().equals(entity.getUserId())) {
      throw new BaseException(ErrorCode.NOT_FOUND.getCode(), "发票申请不存在");
    }
    return entity;
  }

  public N12InvoiceApplicationEntity createInvoiceApplication(
      String token, String orderId, String titleId, String invoiceContent, String remark, String operator) {
    N06OrderEntity order = getOrderDetail(token, orderId);
    N12InvoiceTitleEntity title = getInvoiceTitle(token, titleId);
    if (!"ACTIVE".equalsIgnoreCase(title.getStatus())) {
      throw new BaseException(ErrorCode.BAD_REQUEST.getCode(), "发票抬头未启用");
    }

    SessionEntity session = requireSession(token);
    AuthUserEntity user = userStore.get(session.getAccount());
    LocalDateTime now = LocalDateTime.now();
    String applicationId =
        "IA" + UUID.randomUUID().toString().replace("-", "").substring(0, 10).toUpperCase(Locale.ROOT);
    String normalizedRemark = defaultText(remark, "");
    String normalizedContent = defaultText(invoiceContent, "货款");
    String latestRemark =
        normalizedRemark.isBlank()
            ? "发票申请已提交，开票内容：" + normalizedContent
            : normalizedRemark;
    N12InvoiceApplicationEntity created =
        new N12InvoiceApplicationEntity(
            applicationId,
            user.getUserId(),
            order.getOrderId(),
            order.getOrderNo(),
            order.getInquiryNo(),
            order.getSupplierName(),
            order.getDealTotalAmount(),
            "13%",
            "VAT_SPECIAL",
            invoiceTypeText("VAT_SPECIAL"),
            title.getTitleId(),
            "SUBMITTED",
            invoiceApplyStatusText("SUBMITTED"),
            latestRemark,
            title.getEmail(),
            title.getRegisteredPhone(),
            now,
            now);
    invoiceApplicationStore.put(created.getApplicationId(), created);
    return created;
  }

  public List<N13CreditScoreEntity> listCreditScores(String token, N13CreditScoreQuery query) {
    SessionEntity session = requireSession(token);
    AuthUserEntity user = userStore.get(session.getAccount());
    if (user == null) {
      throw new BaseException(ErrorCode.UNAUTHORIZED.getCode(), "登录态无效");
    }
    String keyword = query == null ? "" : defaultText(query.keyword(), "");
    return creditScoreStore.values().stream()
        .filter(item -> user.getUserId().equals(item.getUserId()))
        .filter(
            item ->
                keyword.isBlank()
                    || item.getScoreId().contains(keyword)
                    || item.getMerchantName().contains(keyword)
                    || item.getMerchantId().contains(keyword)
                    || item.getScoreVersion().contains(keyword))
        .sorted(Comparator.comparing(N13CreditScoreEntity::getUpdatedAt).reversed())
        .toList();
  }

  public N13CreditScoreEntity getCreditScoreDetail(String token, String scoreId) {
    SessionEntity session = requireSession(token);
    AuthUserEntity user = userStore.get(session.getAccount());
    if (user == null) {
      throw new BaseException(ErrorCode.UNAUTHORIZED.getCode(), "登录态无效");
    }
    N13CreditScoreEntity entity = creditScoreStore.get(defaultText(scoreId, ""));
    if (entity == null || !user.getUserId().equals(entity.getUserId())) {
      throw new BaseException(ErrorCode.NOT_FOUND.getCode(), "信用评分记录不存在");
    }
    return entity;
  }

  public H5N11MessageSettingsEntity getH5MessageSettings(String token) {
    SessionEntity session = requireSession(token);
    AuthUserEntity user = userStore.get(session.getAccount());
    if (user == null) {
      throw new BaseException(ErrorCode.UNAUTHORIZED.getCode(), "登录态无效");
    }
    return h5MessageSettingsStore.computeIfAbsent(
        user.getUserId(),
        userId ->
            new H5N11MessageSettingsEntity(
                "MS-" + userId,
                userId,
                user.getPhoneMasked(),
                true,
                true,
                false,
                true,
                true,
                true,
                false,
                "22:00",
                "08:00",
                "H5-N11 默认消息设置",
                "H5",
                "system",
                LocalDateTime.now(),
                LocalDateTime.now()));
  }

  public H5N11MessageSettingsEntity updateH5MessageSettings(
      String token,
      Boolean systemNoticeEnabled,
      Boolean orderNoticeEnabled,
      Boolean financeNoticeEnabled,
      Boolean marketingNoticeEnabled,
      Boolean pushEnabled,
      Boolean smsEnabled,
      Boolean emailEnabled,
      Boolean doNotDisturbEnabled,
      String doNotDisturbStart,
      String doNotDisturbEnd,
      String latestRemark,
      String channel,
      String operator) {
    SessionEntity session = requireSession(token);
    AuthUserEntity user = userStore.get(session.getAccount());
    if (user == null) {
      throw new BaseException(ErrorCode.UNAUTHORIZED.getCode(), "登录态无效");
    }
    H5N11MessageSettingsEntity current = getH5MessageSettings(token);
    LocalDateTime now = LocalDateTime.now();
    H5N11MessageSettingsEntity updated =
        new H5N11MessageSettingsEntity(
            current.getSettingId(),
            user.getUserId(),
            current.getContactMobileMasked(),
            pushEnabled == null ? current.isGlobalPushEnabled() : pushEnabled,
            emailEnabled == null ? current.isAppPushEnabled() : emailEnabled,
            smsEnabled == null ? current.isSmsPushEnabled() : smsEnabled,
            marketingNoticeEnabled == null ? current.isMarketingEnabled() : marketingNoticeEnabled,
            orderNoticeEnabled == null ? current.isTransactionEnabled() : orderNoticeEnabled,
            financeNoticeEnabled == null ? current.isRiskEnabled() : financeNoticeEnabled,
            doNotDisturbEnabled == null ? current.isDoNotDisturbEnabled() : doNotDisturbEnabled,
            defaultText(doNotDisturbStart, current.getDoNotDisturbStart()),
            defaultText(doNotDisturbEnd, current.getDoNotDisturbEnd()),
            defaultText(latestRemark, current.getLatestRemark()),
            defaultText(channel, current.getChannel()),
            defaultText(operator, "h5-n11-message-settings"),
            current.getCreatedAt(),
            now);
    h5MessageSettingsStore.put(user.getUserId(), updated);
    return updated;
  }

  public List<N14DispatchAppealEntity> listDispatchAppeals(String token, N14DispatchAppealQuery query) {
    SessionEntity session = requireSession(token);
    AuthUserEntity user = userStore.get(session.getAccount());
    if (user == null) {
      throw new BaseException(ErrorCode.UNAUTHORIZED.getCode(), "登录态无效");
    }
    String statusFilter = query == null ? "" : defaultText(query.status(), "");
    String keyword = query == null ? "" : defaultText(query.keyword(), "");
    return dispatchAppealStore.values().stream()
        .filter(item -> user.getUserId().equals(item.getUserId()))
        .filter(item -> statusFilter.isBlank() || statusFilter.equalsIgnoreCase(item.getStatus()))
        .filter(
            item ->
                keyword.isBlank()
                    || item.getAppealId().contains(keyword)
                    || item.getSceneCode().contains(keyword)
                    || item.getMerchantName().contains(keyword)
                    || item.getDescription().contains(keyword)
                    || item.getTitle().contains(keyword))
        .sorted(Comparator.comparing(N14DispatchAppealEntity::getUpdatedAt).reversed())
        .toList();
  }

  public N14DispatchAppealEntity getDispatchAppealDetail(String token, String appealId) {
    SessionEntity session = requireSession(token);
    AuthUserEntity user = userStore.get(session.getAccount());
    if (user == null) {
      throw new BaseException(ErrorCode.UNAUTHORIZED.getCode(), "登录态无效");
    }
    N14DispatchAppealEntity entity = dispatchAppealStore.get(defaultText(appealId, ""));
    if (entity == null || !user.getUserId().equals(entity.getUserId())) {
      throw new BaseException(ErrorCode.NOT_FOUND.getCode(), "分发异议申诉单不存在");
    }
    return entity;
  }

  public N14DispatchAppealEntity createDispatchAppeal(
      String token,
      String sceneCode,
      String sceneName,
      String merchantId,
      String merchantName,
      String relatedRuleVersion,
      String appealReason,
      String appealDetail,
      String evidenceUrls,
      String operator) {
    SessionEntity session = requireSession(token);
    AuthUserEntity user = userStore.get(session.getAccount());
    if (user == null) {
      throw new BaseException(ErrorCode.UNAUTHORIZED.getCode(), "登录态无效");
    }
    String normalizedSceneCode = defaultText(sceneCode, "MERCHANT_LEAD").trim().toUpperCase(Locale.ROOT);
    if (!normalizedSceneCode.matches("MERCHANT_LEAD|QUOTE_DISTRIBUTION|PICKUP_DISTRIBUTION")) {
      throw new BaseException(
          ErrorCode.BAD_REQUEST.getCode(),
          "sceneCode 仅支持 MERCHANT_LEAD/QUOTE_DISTRIBUTION/PICKUP_DISTRIBUTION");
    }
    String normalizedReason = defaultText(appealReason, "").trim().toUpperCase(Locale.ROOT);
    if (!normalizedReason.matches("SCORE_MISMATCH|RULE_MISREAD|DATA_ERROR|UNFAIR_TRAFFIC|OTHER")) {
      throw new BaseException(
          ErrorCode.BAD_REQUEST.getCode(),
          "appealReason 仅支持 SCORE_MISMATCH/RULE_MISREAD/DATA_ERROR/UNFAIR_TRAFFIC/OTHER");
    }
    LocalDateTime now = LocalDateTime.now();
    String appealId = "DA" + UUID.randomUUID().toString().replace("-", "").substring(0, 10).toUpperCase(Locale.ROOT);
    List<N14DispatchAppealEntity.TimelineItem> timeline = new ArrayList<>();
    timeline.add(
        new N14DispatchAppealEntity.TimelineItem(
            "SUBMITTED", "已提交", defaultText(operator, "n14-create-appeal"), "申诉已提交，待平台审核", now.toString()));
    N14DispatchAppealEntity created =
        new N14DispatchAppealEntity(
            appealId,
            user.getUserId(),
            defaultText(merchantId, "S001"),
            defaultText(merchantName, user.getCompanyName()),
            normalizedSceneCode,
            defaultText(sceneName, sceneNameText(normalizedSceneCode)),
            defaultText(relatedRuleVersion, "v2026.04"),
            defaultText(merchantId, "S001"),
            "MERCHANT",
            normalizedReason,
            appealReasonText(normalizedReason),
            "分发评分争议申诉",
            defaultText(appealDetail, ""),
            defaultText(evidenceUrls, ""),
            "SUBMITTED",
            dispatchAppealStatusText("SUBMITTED"),
            "申诉已提交，待平台审核",
            timeline,
            now,
            now);
    dispatchAppealStore.put(created.getAppealId(), created);
    return created;
  }

  public N14DispatchAppealEntity updateDispatchAppealStatus(
      String token, String appealId, String status, String operator, String remark) {
    N14DispatchAppealEntity target = getDispatchAppealDetail(token, appealId);
    String normalizedStatus = defaultText(status, "").trim().toUpperCase(Locale.ROOT);
    if (!normalizedStatus.matches("SUBMITTED|PROCESSING|APPROVED|REJECTED|CLOSED")) {
      throw new BaseException(
          ErrorCode.BAD_REQUEST.getCode(),
          "status 仅支持 SUBMITTED/PROCESSING/APPROVED/REJECTED/CLOSED");
    }
    target.updateStatus(
        normalizedStatus,
        dispatchAppealStatusText(normalizedStatus),
        defaultText(operator, "n14-update-status"),
        defaultText(remark, ""),
        LocalDateTime.now());
    return target;
  }

  public void logout(String token) {
    sessionStore.remove(defaultText(token, ""));
  }

  private AuthUserEntity requireUser(String mobile) {
    AuthUserEntity entity = userStore.get(normalizeMobile(mobile));
    if (entity == null) {
      throw new BaseException(ErrorCode.BAD_REQUEST.getCode(), "账号或密码错误");
    }
    return entity;
  }

  private String normalizeMobile(String mobile) {
    String normalized = defaultText(mobile, "").replaceAll("\\D", "");
    if (!normalized.matches("^1\\d{10}$")) {
      throw new BaseException(ErrorCode.BAD_REQUEST.getCode(), "mobile 必须为11位手机号");
    }
    return normalized;
  }

  private String normalizeRole(String roleCode) {
    String normalized = defaultText(roleCode, "").trim().toUpperCase(Locale.ROOT);
    return switch (normalized) {
      case "BUYER", "SUPPLIER", "OPERATOR" -> normalized;
      default -> throw new BaseException(ErrorCode.BAD_REQUEST.getCode(), "roleCode 仅支持 BUYER/SUPPLIER/OPERATOR");
    };
  }

  private String hashPassword(String password) {
    String raw = defaultText(password, "");
    if (raw.isBlank()) {
      throw new BaseException(ErrorCode.BAD_REQUEST.getCode(), "password 不能为空");
    }
    try {
      MessageDigest digest = MessageDigest.getInstance("SHA-256");
      byte[] encoded = digest.digest(raw.getBytes(StandardCharsets.UTF_8));
      StringBuilder sb = new StringBuilder();
      for (byte b : encoded) {
        sb.append(String.format("%02x", b));
      }
      return sb.toString();
    } catch (NoSuchAlgorithmException ex) {
      throw new BaseException(ErrorCode.INTERNAL_ERROR.getCode(), "密码加密失败");
    }
  }

  public String maskPhone(String phone) {
    String digits = defaultText(phone, "").replaceAll("\\D", "");
    if (digits.length() < 7) {
      return "***";
    }
    return digits.substring(0, 3) + "****" + digits.substring(digits.length() - 4);
  }

  private String defaultText(String text, String fallback) {
    return text == null || text.isBlank() ? fallback : text.trim();
  }

  private void seed() {
    AuthUserEntity seed =
        new AuthUserEntity(
            "U000000000001",
            "MOBILE",
            "13800138000",
            "13800138000",
            "138****8000",
            hashPassword("Demo@123456"),
            "演示钢贸有限公司",
            "演示账号",
            "BUYER",
            "ACTIVE",
            LocalDateTime.now().minusDays(3),
            LocalDateTime.now().minusDays(1));
    userStore.put(seed.getAccount(), seed);
    seedNegotiation(seed);
    seedOrders(seed);
    seedTradeTerms(seed);
    seedAfterSaleDisputes(seed);
    seedCashierOrders(seed);
    seedInvoiceTitles(seed);
    seedInvoiceApplications(seed);
    seedCreditScores(seed);
    seedDispatchAppeals(seed);
  }

  private void seedNegotiation(AuthUserEntity user) {
    LocalDateTime now = LocalDateTime.now();
    List<N05NegotiationMessageEntity> firstMessages =
        List.of(
            new N05NegotiationMessageEntity(
                "MSG0001",
                "BUYER",
                "TEXT",
                "当前市场回落，目标 3500 元/吨可否支持？",
                "",
                "",
                "",
                now.minusHours(3)),
            new N05NegotiationMessageEntity(
                "MSG0002",
                "SUPPLIER",
                "TEXT",
                "最低可到 3520 元/吨，含税含运。",
                "3520",
                "",
                "",
                now.minusHours(2)),
            new N05NegotiationMessageEntity(
                "MSG0003",
                "BUYER",
                "TEXT",
                "若 3510 元/吨，我今天可锁单。",
                "3510",
                "",
                "",
                now.minusMinutes(80)),
            new N05NegotiationMessageEntity(
                "MSG0004",
                "SUPPLIER",
                "TEXT",
                "可申请特批，稍后回复。",
                "",
                "",
                "",
                now.minusMinutes(20)));
    N05NegotiationSessionEntity first =
        new N05NegotiationSessionEntity(
            "NS0001",
            "NEG-20260419-0001",
            user.getUserId(),
            user.getAccount(),
            "INQ-20260419-3301",
            "螺纹钢HRB400E Φ20 500吨",
            "螺纹钢HRB400E",
            "Φ20*12m",
            "500吨",
            "唐山",
            "唐山弘达钢贸",
            "BUYER",
            "演示钢贸有限公司",
            "唐山弘达钢贸",
            "ONGOING",
            "R04",
            "3520",
            "3560",
            "CNY",
            true,
            true,
            2,
            "因运费调整，建议每吨下调20元",
            now.minusMinutes(20),
            now.minusHours(3),
            now.minusMinutes(20),
            firstMessages);
    negotiationStore.put(first.getSessionId(), first);

    List<N05NegotiationMessageEntity> secondMessages =
        List.of(
            new N05NegotiationMessageEntity(
                "MSG0010", "BUYER", "TEXT", "希望在 3680 元/吨内达成。", "3680", "", "", now.minusDays(1)),
            new N05NegotiationMessageEntity(
                "MSG0011",
                "SUPPLIER",
                "TEXT",
                "确认 3680 元/吨，支持当日排货。",
                "3680",
                "",
                "",
                now.minusHours(14)));
    N05NegotiationSessionEntity second =
        new N05NegotiationSessionEntity(
            "NS0002",
            "NEG-20260418-0010",
            user.getUserId(),
            user.getAccount(),
            "INQ-20260418-2210",
            "热轧卷板Q235B 3.0*1500 300吨",
            "热轧卷板Q235B",
            "3.0*1500*C",
            "300吨",
            "无锡",
            "无锡铭泰供应链",
            "BUYER",
            "演示钢贸有限公司",
            "无锡铭泰供应链",
            "DEAL",
            "R02",
            "3680",
            "3695",
            "CNY",
            true,
            true,
            0,
            "双方确认 3680 元/吨，已转成交确认",
            now.minusHours(14),
            now.minusDays(1),
            now.minusHours(14),
            secondMessages);
    negotiationStore.put(second.getSessionId(), second);
  }

  private void seedOrders(AuthUserEntity user) {
    LocalDateTime now = LocalDateTime.now();
    List<N06OrderEntity.OrderTimelineNode> firstTimeline = new ArrayList<>();
    firstTimeline.add(
        new N06OrderEntity.OrderTimelineNode(
            "CREATED",
            "创建成交订单",
            "已完成",
            "DEAL_CONFIRMED",
            "成交确认已完成",
            "system",
            now.minusDays(2).toString()));
    firstTimeline.add(
        new N06OrderEntity.OrderTimelineNode(
            "SIGN",
            "合同签署",
            "进行中",
            "PENDING_SIGN",
            "待双方签署合同",
            "buyer",
            now.minusDays(1).toString()));
    firstTimeline.add(
        new N06OrderEntity.OrderTimelineNode(
            "PICKUP",
            "提货履约",
            "待开始",
            "PENDING",
            "签署后可发起提货计划",
            "logistics",
            ""));

    N06OrderEntity first =
        new N06OrderEntity(
            "OD0001",
            "OD-20260418-0001",
            user.getUserId(),
            user.getAccount(),
            "INQ-20260419-3301",
            "螺纹钢HRB400E Φ20 500吨",
            "500吨",
            "唐山",
            "唐山弘达钢贸",
            "演示钢贸有限公司",
            "3520",
            "1760000",
            "PENDING_SIGN",
            "待签署",
            "CONTRACT_PENDING",
            "待签署",
            "签署后48小时内安排提货",
            now.plusDays(2).toString(),
            "NO",
            "",
            "NONE",
            "",
            "",
            now.minusDays(2),
            now.minusHours(8),
            firstTimeline);
    orderStore.put(first.getOrderId(), first);

    List<N06OrderEntity.OrderTimelineNode> secondTimeline = new ArrayList<>();
    secondTimeline.add(
        new N06OrderEntity.OrderTimelineNode(
            "CREATED",
            "创建成交订单",
            "已完成",
            "DEAL_CONFIRMED",
            "成交确认已完成",
            "system",
            now.minusDays(4).toString()));
    secondTimeline.add(
        new N06OrderEntity.OrderTimelineNode(
            "SIGN",
            "合同签署",
            "已完成",
            "SIGNED",
            "合同已签署",
            "buyer",
            now.minusDays(3).toString()));
    secondTimeline.add(
        new N06OrderEntity.OrderTimelineNode(
            "PICKUP",
            "提货履约",
            "已完成",
            "COMPLETED",
            "提货完成并签收",
            "logistics",
            now.minusDays(2).toString()));
    secondTimeline.add(
        new N06OrderEntity.OrderTimelineNode(
            "RECONCILE",
            "对账回款",
            "已完成",
            "PAID",
            "账款已结清",
            "finance",
            now.minusDays(1).toString()));

    N06OrderEntity second =
        new N06OrderEntity(
            "OD0002",
            "OD-20260416-0008",
            user.getUserId(),
            user.getAccount(),
            "INQ-20260418-2210",
            "热轧卷板Q235B 3.0*1500 300吨",
            "300吨",
            "无锡",
            "无锡铭泰供应链",
            "演示钢贸有限公司",
            "3680",
            "1104000",
            "COMPLETED",
            "已完成",
            "SETTLED",
            "已结算",
            "可发起复购或复制订单",
            "",
            "YES",
            now.minusDays(1).toString(),
            "FULLY_PAID",
            now.minusDays(1).toString(),
            "订单履约与回款均已完成",
            now.minusDays(4),
            now.minusDays(1),
            secondTimeline);
    orderStore.put(second.getOrderId(), second);
  }

  private void seedTradeTerms(AuthUserEntity user) {
    LocalDateTime now = LocalDateTime.now();
    N07TradeTermsEntity first =
        new N07TradeTermsEntity(
            "OD0001",
            user.getUserId(),
            "OD-20260418-0001",
            "INQ-20260419-3301",
            "演示钢贸有限公司",
            "唐山弘达钢贸",
            "螺纹钢HRB400E Φ20 500吨",
            "Φ20*12m",
            "500吨",
            "3520",
            "1760000",
            "买方自提，供应方负责协调仓库窗口，签约后48小时内可提。",
            "月结30天，电子对账单确认后5个工作日内付款。",
            "增值税专票 13%，供应方在发货后3个工作日内开票。",
            "银行公对公转账，不支持现金与个人代付。",
            "执行 GB/T 1499.2-2024，按批次附材质单。",
            "理论重量误差 ±0.3%，超差部分按实结算。",
            "任何一方违约按未履约金额的3%承担违约责任。",
            "优先友好协商，协商不成提交买方所在地仲裁委员会。",
            "极端天气导致运输中断可顺延交付，不视为违约。",
            now.toLocalDate().toString(),
            now.plusDays(30).toLocalDate().toString(),
            List.of(
                new N07TradeTermsEntity.ClauseItem(
                    "DELIVERY", "交付条款", "提货通知后24小时内安排车辆入库。", true),
                new N07TradeTermsEntity.ClauseItem(
                    "PAYMENT", "结算条款", "对账确认后5个工作日完成货款支付。", true),
                new N07TradeTermsEntity.ClauseItem(
                    "QUALITY", "质量条款", "如质量异议需在收货后48小时内书面提出。", true),
                new N07TradeTermsEntity.ClauseItem(
                    "FORCE_MAJEURE", "不可抗力", "不可抗力发生后应在24小时内通知对方。", false)),
            List.of(
                new N07TradeTermsEntity.AttachmentItem(
                    "采购合同草案-V1.pdf", "PDF", "https://cdn.huodaizi.com/contract/OD0001-v1.pdf"),
                new N07TradeTermsEntity.AttachmentItem(
                    "材质标准附件.docx", "DOCX", "https://cdn.huodaizi.com/contract/OD0001-material.docx")),
            false,
            "",
            "",
            "",
            now.minusHours(6),
            now.minusDays(1));
    tradeTermsStore.put(first.getOrderId(), first);

    N07TradeTermsEntity second =
        new N07TradeTermsEntity(
            "OD0002",
            user.getUserId(),
            "OD-20260416-0008",
            "INQ-20260418-2210",
            "演示钢贸有限公司",
            "无锡铭泰供应链",
            "热轧卷板Q235B 3.0*1500 300吨",
            "3.0*1500*C",
            "300吨",
            "3680",
            "1104000",
            "供应方代办物流，含一次装卸费。",
            "30%预付款，余款见提单后2个工作日支付。",
            "增值税专票 13%，票到30天内可抵扣。",
            "银行承兑汇票+电汇组合结算。",
            "执行 Q/3202MT 2024 企业标准。",
            "重量误差 ±0.5%，超出部分按补差规则执行。",
            "违约方承担直接损失及滞纳金。",
            "争议提交无锡仲裁委员会。",
            "保密条款有效期 12 个月。",
            now.minusDays(4).toLocalDate().toString(),
            now.plusDays(20).toLocalDate().toString(),
            List.of(
                new N07TradeTermsEntity.ClauseItem(
                    "DELIVERY", "交付条款", "按周滚动提货计划执行。", true),
                new N07TradeTermsEntity.ClauseItem(
                    "PAYMENT", "结算条款", "提单回传后2个工作日内支付尾款。", true),
                new N07TradeTermsEntity.ClauseItem(
                    "QUALITY", "质量条款", "争议样品由第三方检测机构复检。", true)),
            List.of(
                new N07TradeTermsEntity.AttachmentItem(
                    "采购合同签署版.pdf", "PDF", "https://cdn.huodaizi.com/contract/OD0002-signed.pdf")),
            true,
            "采购经理",
            now.minusDays(3).toString(),
            "双方已确认并生效",
            now.minusDays(2),
            now.minusDays(4));
    tradeTermsStore.put(second.getOrderId(), second);
  }

  private void seedAfterSaleDisputes(AuthUserEntity user) {
    LocalDateTime now = LocalDateTime.now();
    N08AfterSaleDisputeEntity first =
        new N08AfterSaleDisputeEntity(
            "AS00000001",
            user.getUserId(),
            "OD0001",
            "OD-20260418-0001",
            "INQ-20260419-3301",
            "演示钢贸有限公司",
            "唐山弘达钢贸",
            "DELIVERY_DELAY",
            "交付延迟",
            "提货窗口延迟导致无法按计划发车",
            "原定今日提货，仓库反馈需顺延1天，影响下游工地排产。",
            "希望优先释放明日早班窗口并减免部分滞车费用。",
            "王经理",
            "138****8000",
            "https://cdn.huodaizi.com/dispute/as-0001-evidence.png",
            "PROCESSING",
            "处理中",
            "平台已联系仓库协调排期",
            now.minusHours(10),
            now.minusHours(2));
    afterSaleStore.put(first.getDisputeId(), first);

    N08AfterSaleDisputeEntity second =
        new N08AfterSaleDisputeEntity(
            "AS00000002",
            user.getUserId(),
            "OD0002",
            "OD-20260416-0008",
            "INQ-20260418-2210",
            "演示钢贸有限公司",
            "无锡铭泰供应链",
            "QUALITY",
            "质量异议",
            "部分卷板厚度偏差超出约定范围",
            "抽检发现2卷板厚度偏差超0.6%，超过合同约定±0.5%。",
            "申请补差并由第三方复检确认。",
            "李主管",
            "138****8000",
            "https://cdn.huodaizi.com/dispute/as-0002-report.pdf",
            "RESOLVED",
            "已解决",
            "双方确认补差方案并已执行",
            now.minusDays(2),
            now.minusDays(1));
    afterSaleStore.put(second.getDisputeId(), second);
  }

  private void seedCashierOrders(AuthUserEntity user) {
    LocalDateTime now = LocalDateTime.now();

    N10CashierOrderEntity first =
        new N10CashierOrderEntity(
            "CS00000001",
            user.getUserId(),
            "OD0001",
            "OD-20260418-0001",
            "INQ-20260419-3301",
            "演示钢贸有限公司",
            "唐山弘达钢贸",
            "螺纹钢HRB400E Φ20 500吨",
            "1760000",
            "0",
            "1760000",
            "CNY",
            "UNPAID",
            "待支付",
            "",
            "",
            "首付款待支付",
            "",
            now.minusHours(8),
            now.minusHours(2),
            List.of(
                new N10CashierOrderEntity.PayTimelineNode(
                    "CREATE", "创建收银单", "UNPAID", "待支付", "system", "系统已生成收银单", now.minusHours(8).toString()),
                new N10CashierOrderEntity.PayTimelineNode(
                    "NOTICE",
                    "支付提醒",
                    "UNPAID",
                    "待支付",
                    "system",
                    "请于24小时内完成支付",
                    now.minusHours(2).toString())));
    cashierStore.put(first.getCashierId(), first);

    N10CashierOrderEntity second =
        new N10CashierOrderEntity(
            "CS00000002",
            user.getUserId(),
            "OD0002",
            "OD-20260416-0008",
            "INQ-20260418-2210",
            "演示钢贸有限公司",
            "无锡铭泰供应链",
            "热轧卷板Q235B 3.0*1500 300吨",
            "1104000",
            "1104000",
            "0",
            "CNY",
            "PAID",
            "已支付",
            "BANK_TRANSFER",
            "对公转账",
            "历史订单已完成支付",
            now.minusDays(1).toString(),
            now.minusDays(3),
            now.minusDays(1),
            List.of(
                new N10CashierOrderEntity.PayTimelineNode(
                    "CREATE", "创建收银单", "UNPAID", "待支付", "system", "系统已生成收银单", now.minusDays(3).toString()),
                new N10CashierOrderEntity.PayTimelineNode(
                    "PAY_SUCCESS",
                    "支付成功",
                    "PAID",
                    "已支付",
                    "buyer-finance",
                    "对公转账到账",
                    now.minusDays(1).toString())));
    cashierStore.put(second.getCashierId(), second);
  }

  private void seedInvoiceTitles(AuthUserEntity user) {
    LocalDateTime now = LocalDateTime.now();
    N12InvoiceTitleEntity company =
        new N12InvoiceTitleEntity(
            "IT00000001",
            user.getUserId(),
            "COMPANY",
            "企业抬头",
            "演示钢贸有限公司",
            "91350211MA2Y9X8E7L",
            "招商银行厦门分行",
            "123456789012345678",
            "厦门市思明区软件园二期观日路58号",
            "13800138000",
            "finance@huodaizi.com",
            true,
            "ACTIVE",
            "启用",
            "n12-seed",
            now.minusDays(10),
            now.minusDays(1));
    invoiceTitleStore.put(company.getTitleId(), company);

    N12InvoiceTitleEntity personal =
        new N12InvoiceTitleEntity(
            "IT00000002",
            user.getUserId(),
            "COMPANY",
            "企业抬头",
            "演示钢贸有限公司分部",
            "91350211MA2Y9X8E8M",
            "中国银行厦门分行",
            "6225888888888888",
            "厦门市湖里区金山街道",
            "13900139000",
            "buyer@demo.com",
            false,
            "ACTIVE",
            "启用",
            "n12-seed",
            now.minusDays(8),
            now.minusDays(2));
    invoiceTitleStore.put(personal.getTitleId(), personal);
  }

  private void seedInvoiceApplications(AuthUserEntity user) {
    LocalDateTime now = LocalDateTime.now();
    N12InvoiceApplicationEntity first =
        new N12InvoiceApplicationEntity(
            "IA00000001",
            user.getUserId(),
            "OD0002",
            "OD-20260416-0008",
            "INQ-20260418-2210",
            "无锡铭泰供应链",
            "1104000",
            "13%",
            "VAT_SPECIAL",
            "增值税专票",
            "IT00000001",
            "DELIVERED",
            invoiceApplyStatusText("DELIVERED"),
            "发票已开具并寄出（顺丰SF12345678）",
            "finance@huodaizi.com",
            "13800138000",
            now.minusDays(3),
            now.minusDays(1));
    invoiceApplicationStore.put(first.getApplicationId(), first);
  }

  private void seedCreditScores(AuthUserEntity user) {
    LocalDateTime now = LocalDateTime.now();
    N13CreditScoreEntity first =
        new N13CreditScoreEntity(
            "CR00000001",
            user.getUserId(),
            "S001",
            "唐山弘达钢贸",
            "2026-04",
            "A+",
            "92",
            "TOP 18%",
            "LOW",
            "低风险",
            "v2026.04",
            "90",
            "+2",
            "UP",
            "98.1%",
            "1.2%",
            "7",
            "99.2%",
            List.of("低风险", "履约稳健"),
            List.of("保持回款登记时效在 T+1 内", "将争议工单平均关闭时长压缩到 24h", "提高电子回单上传完整率至 99%"),
            List.of(
                new N13CreditScoreEntity.FactorItem(
                    "FULFILLMENT", "履约稳定性", 98, 40, "39.2", "UP", "逾期率持续低位", "维持仓库排产提前量"),
                new N13CreditScoreEntity.FactorItem(
                    "RESPONSE", "响应效率", 95, 25, "23.8", "FLAT", "平均响应 7 分钟", "高峰期保持7分钟内响应"),
                new N13CreditScoreEntity.FactorItem(
                    "PAYMENT", "回款质量", 89, 25, "22.3", "UP", "回款周期稳定", "持续推进T+1回款登记"),
                new N13CreditScoreEntity.FactorItem(
                    "DISPUTE", "争议率", 90, 10, "9.0", "UP", "争议结案时长下降", "将争议处理SLA压缩至24小时内")),
            List.of(
                new N13CreditScoreEntity.TimelineItem(
                    "2026-01", "月度评分", "86", "96.2%", "1.8%", "10"),
                new N13CreditScoreEntity.TimelineItem(
                    "2026-02", "月度评分", "88", "97.1%", "1.6%", "9"),
                new N13CreditScoreEntity.TimelineItem(
                    "2026-03", "月度评分", "90", "97.8%", "1.4%", "8"),
                new N13CreditScoreEntity.TimelineItem(
                    "2026-04", "月度评分", "92", "98.1%", "1.2%", "7")),
            now.minusDays(3),
            now.minusHours(2));
    creditScoreStore.put(first.getScoreId(), first);

    N13CreditScoreEntity second =
        new N13CreditScoreEntity(
            "CR00000002",
            user.getUserId(),
            "S001",
            "唐山弘达钢贸",
            "2026-03",
            "A",
            "90",
            "TOP 21%",
            "LOW",
            "低风险",
            "v2026.03",
            "88",
            "+2",
            "UP",
            "97.8%",
            "1.4%",
            "8",
            "98.6%",
            List.of("低风险", "回款稳定"),
            List.of("保持稳定履约表现", "优化高峰时段报价响应"),
            List.of(
                new N13CreditScoreEntity.FactorItem(
                    "FULFILLMENT", "履约稳定性", 97, 40, "38.8", "UP", "履约率持续提升", "持续稳定仓储协同"),
                new N13CreditScoreEntity.FactorItem(
                    "RESPONSE", "响应效率", 92, 25, "23.0", "UP", "高峰期响应改善", "建立值班分时机制"),
                new N13CreditScoreEntity.FactorItem(
                    "PAYMENT", "回款质量", 87, 25, "21.8", "FLAT", "回款质量稳定", "加强逾期预警"),
                new N13CreditScoreEntity.FactorItem(
                    "DISPUTE", "争议率", 88, 10, "8.8", "UP", "争议率低位", "保持争议闭环复盘")),
            List.of(
                new N13CreditScoreEntity.TimelineItem(
                    "2025-12", "月度评分", "84", "95.9%", "2.1%", "11"),
                new N13CreditScoreEntity.TimelineItem(
                    "2026-01", "月度评分", "86", "96.4%", "1.9%", "10"),
                new N13CreditScoreEntity.TimelineItem(
                    "2026-02", "月度评分", "88", "97.1%", "1.7%", "9"),
                new N13CreditScoreEntity.TimelineItem(
                    "2026-03", "月度评分", "90", "97.8%", "1.4%", "8")),
            now.minusDays(33),
            now.minusDays(30));
    creditScoreStore.put(second.getScoreId(), second);
  }

  private void seedDispatchAppeals(AuthUserEntity user) {
    LocalDateTime now = LocalDateTime.now();
    N14DispatchAppealEntity first =
        new N14DispatchAppealEntity(
            "DA00000001",
            user.getUserId(),
            "S001",
            "唐山弘达钢贸",
            "MERCHANT_LEAD",
            "线索分发",
            "v2026.04",
            "S001",
            "MERCHANT",
            "SCORE_MISMATCH",
            appealReasonText("SCORE_MISMATCH"),
            "分发评分偏低复核申请",
            "系统显示响应时效评分偏低，与实际工单响应记录不一致。",
            "https://cdn.huodaizi.com/appeal/da0001-evidence.png",
            "PROCESSING",
            dispatchAppealStatusText("PROCESSING"),
            "平台已受理，正在核对响应日志",
            new ArrayList<>(
                List.of(
                    new N14DispatchAppealEntity.TimelineItem(
                        "SUBMITTED",
                        "已提交",
                        "n14-seed",
                        "申诉已提交，待平台审核",
                        now.minusDays(2).toString()),
                    new N14DispatchAppealEntity.TimelineItem(
                        "PROCESSING",
                        "处理中",
                        "n14-seed",
                        "平台已受理，正在核对响应日志",
                        now.minusHours(6).toString()))),
            now.minusDays(2),
            now.minusHours(6));
    dispatchAppealStore.put(first.getAppealId(), first);

    N14DispatchAppealEntity second =
        new N14DispatchAppealEntity(
            "DA00000002",
            user.getUserId(),
            "S001",
            "唐山弘达钢贸",
            "MERCHANT_LEAD",
            "线索分发",
            "v2026.03",
            "S001",
            "MERCHANT",
            "DATA_ERROR",
            appealReasonText("DATA_ERROR"),
            "履约数据漏计复核申请",
            "部分履约回单已上传但评分未计入，申请复核。",
            "https://cdn.huodaizi.com/appeal/da0002-evidence.pdf",
            "APPROVED",
            dispatchAppealStatusText("APPROVED"),
            "复核通过，评分将于下轮更新修正",
            new ArrayList<>(
                List.of(
                    new N14DispatchAppealEntity.TimelineItem(
                        "SUBMITTED",
                        "已提交",
                        "n14-seed",
                        "申诉已提交，待平台审核",
                        now.minusDays(8).toString()),
                    new N14DispatchAppealEntity.TimelineItem(
                        "PROCESSING",
                        "处理中",
                        "n14-seed",
                        "平台已受理，核查数据中",
                        now.minusDays(6).toString()),
                    new N14DispatchAppealEntity.TimelineItem(
                        "APPROVED",
                        "审核通过",
                        "n14-seed",
                        "复核通过，评分将于下轮更新修正",
                        now.minusDays(3).toString()))),
            now.minusDays(8),
            now.minusDays(3));
    dispatchAppealStore.put(second.getAppealId(), second);
  }

  private String issueTypeText(String type) {
    return switch (type) {
      case "QUALITY" -> "质量异议";
      case "DELIVERY_DELAY" -> "交付延迟";
      case "INVOICE" -> "发票问题";
      case "PAYMENT" -> "结算问题";
      default -> "其他问题";
    };
  }

  private String disputeStatusText(String status) {
    return switch (status) {
      case "SUBMITTED" -> "已提交";
      case "PROCESSING" -> "处理中";
      case "RESOLVED" -> "已解决";
      case "CLOSED" -> "已关闭";
      default -> "处理中";
    };
  }

  private String payChannelText(String channel) {
    return switch (channel) {
      case "BANK_TRANSFER" -> "对公转账";
      case "ALIPAY" -> "支付宝";
      case "WECHAT" -> "微信支付";
      case "UNIONPAY" -> "银联";
      default -> "其他";
    };
  }

  private String invoiceTypeText(String invoiceType) {
    return switch (invoiceType) {
      case "VAT_SPECIAL" -> "增值税专票";
      case "VAT_NORMAL" -> "增值税普票";
      default -> "其他";
    };
  }

  private String invoiceApplyStatusText(String status) {
    return switch (defaultText(status, "").toUpperCase(Locale.ROOT)) {
      case "SUBMITTED" -> "已提交";
      case "PROCESSING" -> "开票中";
      case "ISSUED" -> "已开票";
      case "DELIVERED" -> "已寄出";
      case "VOID" -> "已作废";
      default -> "处理中";
    };
  }

  private String titleStatusText(String status) {
    return switch (defaultText(status, "").toUpperCase(Locale.ROOT)) {
      case "ACTIVE" -> "启用";
      case "INACTIVE" -> "停用";
      default -> "未知";
    };
  }

  private String sceneNameText(String sceneCode) {
    return switch (defaultText(sceneCode, "").toUpperCase(Locale.ROOT)) {
      case "MERCHANT_LEAD" -> "线索分发";
      case "QUOTE_DISTRIBUTION" -> "报价分发";
      case "PICKUP_DISTRIBUTION" -> "提货协同分发";
      default -> "线索分发";
    };
  }

  private String appealReasonText(String reason) {
    return switch (defaultText(reason, "").toUpperCase(Locale.ROOT)) {
      case "SCORE_MISMATCH" -> "评分结果异议";
      case "RULE_MISREAD" -> "规则解读异议";
      case "DATA_ERROR" -> "数据错误申诉";
      case "UNFAIR_TRAFFIC" -> "流量分发不公";
      default -> "其他申诉";
    };
  }

  private String dispatchAppealStatusText(String status) {
    return switch (defaultText(status, "").toUpperCase(Locale.ROOT)) {
      case "SUBMITTED" -> "已提交";
      case "PROCESSING" -> "处理中";
      case "APPROVED" -> "申诉通过";
      case "REJECTED" -> "申诉驳回";
      case "CLOSED" -> "已关闭";
      default -> "处理中";
    };
  }

  public static final class SessionEntity {
    private final String token;
    private final String userId;
    private final String account;
    private final String defaultRoleCode;
    private final String activeIdentityCode;
    private final String channel;
    private final LocalDateTime expireAt;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;

    public SessionEntity(
        String token,
        String userId,
        String account,
        String defaultRoleCode,
        String activeIdentityCode,
        String channel,
        LocalDateTime expireAt,
        LocalDateTime createdAt,
        LocalDateTime updatedAt) {
      this.token = token;
      this.userId = userId;
      this.account = account;
      this.defaultRoleCode = defaultRoleCode;
      this.activeIdentityCode = activeIdentityCode;
      this.channel = channel;
      this.expireAt = expireAt;
      this.createdAt = createdAt;
      this.updatedAt = updatedAt;
    }

    public String getToken() {
      return token;
    }

    public String getUserId() {
      return userId;
    }

    public String getAccount() {
      return account;
    }

    public String getDefaultRoleCode() {
      return defaultRoleCode;
    }

    public String getActiveIdentityCode() {
      return activeIdentityCode;
    }

    public String getChannel() {
      return channel;
    }

    public LocalDateTime getExpireAt() {
      return expireAt;
    }

    public LocalDateTime getCreatedAt() {
      return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
      return updatedAt;
    }
  }

  public record AuthRegistration(
      String accountType,
      String mobile,
      String password,
      String companyName,
      String contactName,
      String operator) {}

  public static final class H5LoginCodeEntity {
    private final String mobile;
    private final String codeToken;
    private final String smsCode;
    private final int expireInSeconds;
    private final LocalDateTime expireAt;
    private final String operator;
    private final LocalDateTime sentAt;

    public H5LoginCodeEntity(
        String mobile,
        String codeToken,
        String smsCode,
        int expireInSeconds,
        LocalDateTime expireAt,
        String operator,
        LocalDateTime sentAt) {
      this.mobile = mobile;
      this.codeToken = codeToken;
      this.smsCode = smsCode;
      this.expireInSeconds = expireInSeconds;
      this.expireAt = expireAt;
      this.operator = operator;
      this.sentAt = sentAt;
    }

    public String getMobile() {
      return mobile;
    }

    public String getCodeToken() {
      return codeToken;
    }

    public String getSmsCode() {
      return smsCode;
    }

    public int getExpireInSeconds() {
      return expireInSeconds;
    }

    public LocalDateTime getExpireAt() {
      return expireAt;
    }

    public String getOperator() {
      return operator;
    }

    public LocalDateTime getSentAt() {
      return sentAt;
    }
  }
}
