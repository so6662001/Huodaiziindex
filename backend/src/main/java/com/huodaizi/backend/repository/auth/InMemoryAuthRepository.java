package com.huodaizi.backend.repository.auth;

import com.huodaizi.backend.common.BaseException;
import com.huodaizi.backend.common.ErrorCode;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDate;
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
  private final ConcurrentMap<String, Admn02BuyerBlacklistRecordEntity> buyerBlacklistStore =
      new ConcurrentHashMap<>();
  private final ConcurrentMap<String, Admn03RolePermissionEntity> admn03RoleStore =
      new ConcurrentHashMap<>();
  private final ConcurrentMap<String, Admn04AuditLogEntity> admn04AuditLogStore =
      new ConcurrentHashMap<>();
  private final ConcurrentMap<String, Admn05CategorySpecDictEntity> admn05CategorySpecStore =
      new ConcurrentHashMap<>();
  private final ConcurrentMap<String, Admn06LeadQualityEntity> admn06LeadQualityStore =
      new ConcurrentHashMap<>();
  private final ConcurrentMap<String, Admn09ArbitrationTicketEntity> admn09ArbitrationStore =
      new ConcurrentHashMap<>();
  private final ConcurrentMap<String, Admn10BillingRuleEntity> admn10BillingRuleStore =
      new ConcurrentHashMap<>();
  private final ConcurrentMap<String, Admn11PaymentRefundEntity> admn11PaymentRefundStore =
      new ConcurrentHashMap<>();
  private final ConcurrentMap<String, Admn12AdSlotScheduleEntity> admn12AdSlotScheduleStore =
      new ConcurrentHashMap<>();
  private final ConcurrentMap<String, Admn13CreditModelVersionEntity> admn13CreditModelVersionStore =
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

  public List<AuthUserEntity> listAllBuyersForAdmin(
      String accountStatus, String blacklistStatus, String keyword) {
    String statusFilter = defaultText(accountStatus, "").toUpperCase(Locale.ROOT);
    String blacklistFilter = defaultText(blacklistStatus, "").toUpperCase(Locale.ROOT);
    String keywordFilter = defaultText(keyword, "").toLowerCase(Locale.ROOT);
    return userStore.values().stream()
        .filter(item -> "BUYER".equalsIgnoreCase(defaultText(item.getRole(), "")))
        .filter(
            item ->
                statusFilter.isBlank()
                    || statusFilter.equals(defaultText(item.getStatus(), "").toUpperCase(Locale.ROOT)))
        .filter(
            item -> {
              boolean blacklisted = isBuyerBlacklisted(item.getUserId());
              if (blacklistFilter.isBlank()) {
                return true;
              }
              if ("BLACKLISTED".equals(blacklistFilter)) {
                return blacklisted;
              }
              if ("NORMAL".equals(blacklistFilter)) {
                return !blacklisted;
              }
              return true;
            })
        .filter(
            item ->
                keywordFilter.isBlank()
                    || defaultText(item.getUserId(), "").toLowerCase(Locale.ROOT).contains(keywordFilter)
                    || defaultText(item.getAccount(), "").toLowerCase(Locale.ROOT).contains(keywordFilter)
                    || defaultText(item.getPhone(), "").toLowerCase(Locale.ROOT).contains(keywordFilter)
                    || defaultText(item.getCompanyName(), "").toLowerCase(Locale.ROOT).contains(keywordFilter)
                    || defaultText(item.getContactName(), "").toLowerCase(Locale.ROOT).contains(keywordFilter))
        .sorted(Comparator.comparing(AuthUserEntity::getUpdatedAt).reversed())
        .toList();
  }

  public AuthUserEntity getBuyerByIdForAdmin(String userId) {
    String normalizedUserId = defaultText(userId, "");
    if (normalizedUserId.isBlank()) {
      throw new BaseException(ErrorCode.BAD_REQUEST.getCode(), "userId 不能为空");
    }
    AuthUserEntity target =
        userStore.values().stream()
            .filter(item -> normalizedUserId.equals(item.getUserId()))
            .findFirst()
            .orElse(null);
    if (target == null) {
      throw new BaseException(ErrorCode.NOT_FOUND.getCode(), "买家不存在");
    }
    if (!"BUYER".equalsIgnoreCase(defaultText(target.getRole(), ""))) {
      throw new BaseException(ErrorCode.BAD_REQUEST.getCode(), "当前账号不是买家");
    }
    return target;
  }

  public Optional<Admn02BuyerBlacklistRecordEntity> getBuyerBlacklistRecord(String userId) {
    return Optional.ofNullable(buyerBlacklistStore.get(defaultText(userId, "")));
  }

  public Admn02BuyerBlacklistRecordEntity updateBuyerBlacklistForAdmin(
      String userId, String action, String reasonCode, String remark, String operator) {
    AuthUserEntity buyer = getBuyerByIdForAdmin(userId);
    String normalizedAction = defaultText(action, "").toUpperCase(Locale.ROOT);
    boolean nextBlacklisted =
        switch (normalizedAction) {
          case "BLACKLIST" -> true;
          case "UNBLACKLIST" -> false;
          default -> throw new BaseException(ErrorCode.BAD_REQUEST.getCode(), "action 仅支持 BLACKLIST/UNBLACKLIST");
        };
    String safeReason = defaultText(reasonCode, nextBlacklisted ? "RISK_CONTROL" : "RECOVERED");
    String safeRemark = defaultText(remark, nextBlacklisted ? "管理端拉黑买家" : "管理端解除拉黑");
    String safeOperator = defaultText(operator, "admn02-system");
    LocalDateTime now = LocalDateTime.now();
    Admn02BuyerBlacklistRecordEntity record = buyerBlacklistStore.get(buyer.getUserId());
    if (record == null) {
      record =
          new Admn02BuyerBlacklistRecordEntity(
              buyer.getUserId(), nextBlacklisted, safeReason, safeRemark, safeOperator, now);
    } else {
      record.update(nextBlacklisted, safeReason, safeRemark, safeOperator, now);
    }
    buyer.touch(now);
    userStore.put(buyer.getAccount(), buyer);
    buyerBlacklistStore.put(buyer.getUserId(), record);
    return record;
  }

  public String latestOrderAtForBuyer(String userId) {
    LocalDateTime latest =
        orderStore.values().stream()
            .filter(item -> defaultText(userId, "").equals(item.getUserId()))
            .map(N06OrderEntity::getUpdatedAt)
            .max(LocalDateTime::compareTo)
            .orElse(null);
    return latest == null ? "" : latest.toString();
  }

  public List<Admn03RolePermissionEntity> listRolesForAdmin(String keyword, String status) {
    String keywordFilter = defaultText(keyword, "").toLowerCase(Locale.ROOT);
    String statusFilter = defaultText(status, "").toUpperCase(Locale.ROOT);
    return admn03RoleStore.values().stream()
        .filter(
            item ->
                statusFilter.isBlank()
                    || statusFilter.equals(defaultText(item.getStatus(), "").toUpperCase(Locale.ROOT)))
        .filter(
            item ->
                keywordFilter.isBlank()
                    || defaultText(item.getRoleCode(), "").toLowerCase(Locale.ROOT).contains(keywordFilter)
                    || defaultText(item.getRoleName(), "").toLowerCase(Locale.ROOT).contains(keywordFilter)
                    || defaultText(item.getDescription(), "").toLowerCase(Locale.ROOT).contains(keywordFilter))
        .sorted(Comparator.comparing(Admn03RolePermissionEntity::getUpdatedAt).reversed())
        .toList();
  }

  public Admn03RolePermissionEntity getRoleForAdmin(String roleId) {
    String normalizedRoleId = defaultText(roleId, "");
    if (normalizedRoleId.isBlank()) {
      throw new BaseException(ErrorCode.BAD_REQUEST.getCode(), "roleId 不能为空");
    }
    Admn03RolePermissionEntity role = admn03RoleStore.get(normalizedRoleId);
    if (role == null) {
      throw new BaseException(ErrorCode.NOT_FOUND.getCode(), "角色不存在");
    }
    return role;
  }

  public Admn03RolePermissionEntity upsertRoleForAdmin(
      String roleCode, String roleName, String description, List<String> permissionCodes, String operator) {
    String safeRoleCode = defaultText(roleCode, "").toUpperCase(Locale.ROOT);
    String safeRoleName = defaultText(roleName, "");
    if (safeRoleCode.isBlank() || safeRoleName.isBlank()) {
      throw new BaseException(ErrorCode.BAD_REQUEST.getCode(), "roleCode/roleName 不能为空");
    }
    List<String> normalizedPermissions = normalizePermissionCodes(permissionCodes);
    LocalDateTime now = LocalDateTime.now();
    Admn03RolePermissionEntity existing =
        admn03RoleStore.values().stream()
            .filter(item -> safeRoleCode.equalsIgnoreCase(item.getRoleCode()))
            .findFirst()
            .orElse(null);
    if (existing == null) {
      String roleId = "RL" + UUID.randomUUID().toString().replace("-", "").substring(0, 10).toUpperCase(Locale.ROOT);
      Admn03RolePermissionEntity created =
          new Admn03RolePermissionEntity(
              roleId,
              safeRoleCode,
              safeRoleName,
              "CUSTOM",
              "ACTIVE",
              defaultText(description, ""),
              normalizedPermissions,
              defaultText(operator, "admn03-create"),
              now,
              now);
      admn03RoleStore.put(created.getRoleId(), created);
      return created;
    }
    existing.updateBasic(
        safeRoleCode,
        safeRoleName,
        existing.getRoleType(),
        defaultText(existing.getStatus(), "ACTIVE"),
        defaultText(description, existing.getDescription()),
        defaultText(operator, "admn03-update"),
        now);
    existing.updatePermissions(normalizedPermissions, defaultText(operator, "admn03-update"), now);
    admn03RoleStore.put(existing.getRoleId(), existing);
    return existing;
  }

  public Admn03RolePermissionEntity updateRolePermissionsForAdmin(
      String roleId, List<String> permissionCodes, String operator) {
    Admn03RolePermissionEntity role = getRoleForAdmin(roleId);
    if ("SYSTEM".equalsIgnoreCase(defaultText(role.getRoleType(), ""))
        && "SUPER_ADMIN".equalsIgnoreCase(defaultText(role.getRoleCode(), ""))) {
      throw new BaseException(ErrorCode.BAD_REQUEST.getCode(), "SUPER_ADMIN 系统角色不允许修改权限");
    }
    List<String> normalizedPermissions = normalizePermissionCodes(permissionCodes);
    LocalDateTime now = LocalDateTime.now();
    role.updatePermissions(normalizedPermissions, defaultText(operator, "admn03-permission"), now);
    admn03RoleStore.put(role.getRoleId(), role);
    return role;
  }

  public int countUsersByRoleCodeForAdmin(String roleCode) {
    String normalized = defaultText(roleCode, "").toUpperCase(Locale.ROOT);
    return (int)
        userStore.values().stream()
            .filter(item -> normalized.equals(defaultText(item.getRole(), "").toUpperCase(Locale.ROOT)))
            .count();
  }

  public String permissionNameForAdmin(String permissionCode) {
    return switch (defaultText(permissionCode, "").toUpperCase(Locale.ROOT)) {
      case "DASHBOARD_VIEW" -> "经营看板查看";
      case "LEAD_OPS_MANAGE" -> "线索运营管理";
      case "RISK_ALERT_MANAGE" -> "风险预警处置";
      case "ADMN01_CERT_REVIEW" -> "商家认证审核";
      case "ADMN02_BLACKLIST_MANAGE" -> "买家黑名单管理";
      case "ADMN03_RBAC_MANAGE" -> "角色权限管理";
      case "ADMN04_AUDIT_LOG_VIEW" -> "操作审计日志查看";
      case "ADMN05_DICT_MANAGE" -> "类目规格词库管理";
      case "ADMN06_LEAD_QA_MANAGE" -> "线索质检中心";
      case "ADMN08_FUNNEL_VIEW" -> "成交漏斗分析";
      case "ADMN09_ARBITRATION_MANAGE" -> "仲裁工单中心";
      case "ADMN10_BILLING_RULE_MANAGE" -> "计费规则配置";
      case "ADMN11_PAYMENT_REFUND_MANAGE" -> "支付与退款管理";
      case "ADMN12_AD_SLOT_SCHEDULE_MANAGE" -> "广告位排期中心";
      case "ADMN13_CREDIT_MODEL_VERSION_MANAGE" -> "信用模型版本管理";
      default -> "未命名权限";
    };
  }

  public List<Admn06LeadQualityEntity> listLeadQualityForAdmin(
      String source, String qualityStatus, String riskLevel, String reviewer, String keyword) {
    String sourceFilter = defaultText(source, "").toUpperCase(Locale.ROOT);
    String statusFilter = defaultText(qualityStatus, "").toUpperCase(Locale.ROOT);
    String riskFilter = defaultText(riskLevel, "").toUpperCase(Locale.ROOT);
    String reviewerFilter = defaultText(reviewer, "").toLowerCase(Locale.ROOT);
    String keywordFilter = defaultText(keyword, "").toLowerCase(Locale.ROOT);
    return admn06LeadQualityStore.values().stream()
        .filter(
            item ->
                sourceFilter.isBlank()
                    || sourceFilter.equals(defaultText(item.getSource(), "").toUpperCase(Locale.ROOT)))
        .filter(
            item ->
                statusFilter.isBlank()
                    || statusFilter.equals(defaultText(item.getQualityStatus(), "").toUpperCase(Locale.ROOT)))
        .filter(
            item ->
                riskFilter.isBlank()
                    || riskFilter.equals(defaultText(item.getRiskLevel(), "").toUpperCase(Locale.ROOT)))
        .filter(
            item ->
                reviewerFilter.isBlank()
                    || defaultText(item.getReviewer(), "").toLowerCase(Locale.ROOT).contains(reviewerFilter))
        .filter(
            item ->
                keywordFilter.isBlank()
                    || defaultText(item.getQualityId(), "").toLowerCase(Locale.ROOT).contains(keywordFilter)
                    || defaultText(item.getLeadId(), "").toLowerCase(Locale.ROOT).contains(keywordFilter)
                    || defaultText(item.getLeadNo(), "").toLowerCase(Locale.ROOT).contains(keywordFilter)
                    || defaultText(item.getIssueTags(), "").toLowerCase(Locale.ROOT).contains(keywordFilter)
                    || defaultText(item.getReviewRemark(), "").toLowerCase(Locale.ROOT).contains(keywordFilter))
        .sorted(Comparator.comparing(Admn06LeadQualityEntity::getUpdatedAt).reversed())
        .toList();
  }

  public Admn06LeadQualityEntity getLeadQualityForAdmin(String qualityId) {
    String normalized = defaultText(qualityId, "");
    if (normalized.isBlank()) {
      throw new BaseException(ErrorCode.BAD_REQUEST.getCode(), "qualityId 不能为空");
    }
    Admn06LeadQualityEntity entity = admn06LeadQualityStore.get(normalized);
    if (entity == null) {
      throw new BaseException(ErrorCode.NOT_FOUND.getCode(), "质检记录不存在");
    }
    return entity;
  }

  public Admn06LeadQualityEntity reviewLeadQualityForAdmin(
      String qualityId,
      String qualityStatus,
      String riskLevel,
      Integer qualityScore,
      String ruleCode,
      String reviewRemark,
      String reviewer) {
    Admn06LeadQualityEntity entity = getLeadQualityForAdmin(qualityId);
    String source = defaultText(entity.getSource(), "").toUpperCase(Locale.ROOT);
    if (!source.matches("INQUIRY|SITE_AD")) {
      throw new BaseException(ErrorCode.BAD_REQUEST.getCode(), "source 仅支持 INQUIRY/SITE_AD");
    }
    String status = defaultText(qualityStatus, "").toUpperCase(Locale.ROOT);
    if (!status.matches("PENDING|PASS|REJECT|RECHECK")) {
      throw new BaseException(ErrorCode.BAD_REQUEST.getCode(), "qualityStatus 仅支持 PENDING/PASS/REJECT/RECHECK");
    }
    String risk = defaultText(riskLevel, entity.getRiskLevel()).toUpperCase(Locale.ROOT);
    if (!risk.matches("LOW|MEDIUM|HIGH")) {
      throw new BaseException(ErrorCode.BAD_REQUEST.getCode(), "riskLevel 仅支持 LOW/MEDIUM/HIGH");
    }
    int score = qualityScore == null ? entity.getQualityScore() : Math.max(0, Math.min(qualityScore, 100));
    String tags = mergeIssueTags(entity.getIssueTags(), ruleCode, status, risk);
    String remark = defaultText(reviewRemark, entity.getReviewRemark());
    String operator = defaultText(reviewer, "admn06-reviewer");
    entity.review(status, score, risk, tags, remark, operator, LocalDateTime.now());
    admn06LeadQualityStore.put(entity.getQualityId(), entity);
    return entity;
  }

  public String admn06SourceText(String source) {
    return switch (defaultText(source, "").toUpperCase(Locale.ROOT)) {
      case "INQUIRY" -> "询价线索";
      case "SITE_AD" -> "广告线索";
      default -> "其他来源";
    };
  }

  public String admn06QualityStatusText(String qualityStatus) {
    return switch (defaultText(qualityStatus, "").toUpperCase(Locale.ROOT)) {
      case "PASS" -> "通过";
      case "REJECT" -> "驳回";
      case "RECHECK" -> "待复检";
      case "PENDING" -> "待审核";
      default -> "未知";
    };
  }

  public String admn06RiskLevelText(String riskLevel) {
    return switch (defaultText(riskLevel, "").toUpperCase(Locale.ROOT)) {
      case "LOW" -> "低风险";
      case "MEDIUM" -> "中风险";
      case "HIGH" -> "高风险";
      default -> "未知";
    };
  }

  public List<Admn09ArbitrationTicketEntity> listArbitrationTicketsForAdmin(
      String arbitrationStatus,
      String priorityLevel,
      String city,
      String assignedArbitrator,
      String keyword) {
    String statusFilter = defaultText(arbitrationStatus, "").toUpperCase(Locale.ROOT);
    String priorityFilter = defaultText(priorityLevel, "").toUpperCase(Locale.ROOT);
    String cityFilter = defaultText(city, "").toLowerCase(Locale.ROOT);
    String arbitratorFilter = defaultText(assignedArbitrator, "").toLowerCase(Locale.ROOT);
    String keywordFilter = defaultText(keyword, "").toLowerCase(Locale.ROOT);
    return admn09ArbitrationStore.values().stream()
        .filter(
            item ->
                statusFilter.isBlank()
                    || statusFilter.equals(defaultText(item.getArbitrationStatus(), "").toUpperCase(Locale.ROOT)))
        .filter(
            item ->
                priorityFilter.isBlank()
                    || priorityFilter.equals(defaultText(item.getPriorityLevel(), "").toUpperCase(Locale.ROOT)))
        .filter(item -> cityFilter.isBlank() || defaultText(item.getCity(), "").toLowerCase(Locale.ROOT).contains(cityFilter))
        .filter(
            item ->
                arbitratorFilter.isBlank()
                    || defaultText(item.getAssignedArbitrator(), "")
                        .toLowerCase(Locale.ROOT)
                        .contains(arbitratorFilter))
        .filter(
            item -> {
              if (keywordFilter.isBlank()) {
                return true;
              }
              N08AfterSaleDisputeEntity dispute = afterSaleStore.get(item.getDisputeId());
              return defaultText(item.getTicketId(), "").toLowerCase(Locale.ROOT).contains(keywordFilter)
                  || defaultText(item.getDisputeId(), "").toLowerCase(Locale.ROOT).contains(keywordFilter)
                  || (dispute != null
                      && (defaultText(dispute.getOrderNo(), "").toLowerCase(Locale.ROOT).contains(keywordFilter)
                          || defaultText(dispute.getInquiryNo(), "").toLowerCase(Locale.ROOT).contains(keywordFilter)
                          || defaultText(dispute.getBuyerCompany(), "").toLowerCase(Locale.ROOT).contains(keywordFilter)
                          || defaultText(dispute.getSupplierName(), "").toLowerCase(Locale.ROOT).contains(keywordFilter)
                          || defaultText(dispute.getIssueSummary(), "").toLowerCase(Locale.ROOT).contains(keywordFilter)))
                  || defaultText(item.getLatestConclusion(), "").toLowerCase(Locale.ROOT).contains(keywordFilter)
                  || defaultText(item.getLatestRemark(), "").toLowerCase(Locale.ROOT).contains(keywordFilter)
                  || defaultText(item.getAssignedArbitrator(), "").toLowerCase(Locale.ROOT).contains(keywordFilter);
            })
        .sorted(Comparator.comparing(Admn09ArbitrationTicketEntity::getUpdatedAt).reversed())
        .toList();
  }

  public Admn09ArbitrationTicketEntity getArbitrationTicketForAdmin(String ticketId) {
    String normalized = defaultText(ticketId, "");
    if (normalized.isBlank()) {
      throw new BaseException(ErrorCode.BAD_REQUEST.getCode(), "ticketId 不能为空");
    }
    Admn09ArbitrationTicketEntity entity = admn09ArbitrationStore.get(normalized);
    if (entity == null) {
      throw new BaseException(ErrorCode.NOT_FOUND.getCode(), "仲裁工单不存在");
    }
    return entity;
  }

  public N08AfterSaleDisputeEntity getAfterSaleDisputeDetailForAdmin(String disputeId) {
    String normalized = defaultText(disputeId, "");
    if (normalized.isBlank()) {
      throw new BaseException(ErrorCode.BAD_REQUEST.getCode(), "disputeId 不能为空");
    }
    N08AfterSaleDisputeEntity entity = afterSaleStore.get(normalized);
    if (entity == null) {
      throw new BaseException(ErrorCode.NOT_FOUND.getCode(), "售后争议单不存在");
    }
    return entity;
  }

  public Admn09ArbitrationTicketEntity assignArbitrationTicketForAdmin(
      String ticketId,
      String action,
      String assignedArbitrator,
      String priorityLevel,
      String handleRemark,
      String operator) {
    Admn09ArbitrationTicketEntity ticket = getArbitrationTicketForAdmin(ticketId);
    N08AfterSaleDisputeEntity dispute = getAfterSaleDisputeDetailForAdmin(ticket.getDisputeId());
    String normalizedAction = defaultText(action, "").toUpperCase(Locale.ROOT);
    if (!normalizedAction.matches("ACCEPT|TRANSFER|START_REVIEW")) {
      throw new BaseException(ErrorCode.BAD_REQUEST.getCode(), "action 仅支持 ACCEPT/TRANSFER/START_REVIEW");
    }
    String nextPriority =
        defaultText(priorityLevel, ticket.getPriorityLevel()).toUpperCase(Locale.ROOT);
    if (!nextPriority.matches("LOW|MEDIUM|HIGH|URGENT")) {
      throw new BaseException(ErrorCode.BAD_REQUEST.getCode(), "priorityLevel 仅支持 LOW/MEDIUM/HIGH/URGENT");
    }
    String nextArbitrator = defaultText(assignedArbitrator, ticket.getAssignedArbitrator());
    if ("ACCEPT".equals(normalizedAction) && nextArbitrator.isBlank()) {
      nextArbitrator = defaultText(operator, "仲裁员-待分配");
    }
    String nextStatus = "PROCESSING";
    String safeOperator = defaultText(operator, "admn09-assign");
    String remark = defaultText(handleRemark, "仲裁工单已进入处理流程");
    LocalDateTime now = LocalDateTime.now();
    ticket.updateCase(
        nextStatus,
        nextPriority,
        nextArbitrator,
        ticket.getHearingAt(),
        ticket.getLatestConclusion(),
        remark,
        safeOperator,
        now);
    ticket.appendTimeline(
        "ASSIGN_" + normalizedAction,
        "仲裁分派",
        nextStatus,
        admn09ArbitrationStatusText(nextStatus),
        safeOperator,
        remark,
        now.toString());
    if ("SUBMITTED".equalsIgnoreCase(dispute.getStatus())) {
      dispute.updateStatus(
          "PROCESSING",
          disputeStatusText("PROCESSING"),
          "平台仲裁处理中：" + remark,
          safeOperator,
          now);
    }
    admn09ArbitrationStore.put(ticket.getTicketId(), ticket);
    return ticket;
  }

  public Admn09ArbitrationTicketEntity reviewArbitrationTicketForAdmin(
      String ticketId, String action, String resolutionSummary, String resolutionDetail, String operator) {
    Admn09ArbitrationTicketEntity ticket = getArbitrationTicketForAdmin(ticketId);
    N08AfterSaleDisputeEntity dispute = getAfterSaleDisputeDetailForAdmin(ticket.getDisputeId());
    String normalizedAction = defaultText(action, "").toUpperCase(Locale.ROOT);
    if (!normalizedAction.matches("SUPPORT_BUYER|SUPPORT_SUPPLIER|MEDIATION|CLOSE_NO_FAULT|REOPEN")) {
      throw new BaseException(
          ErrorCode.BAD_REQUEST.getCode(),
          "action 仅支持 SUPPORT_BUYER/SUPPORT_SUPPLIER/MEDIATION/CLOSE_NO_FAULT/REOPEN");
    }
    String safeOperator = defaultText(operator, "admn09-review");
    String summary =
        defaultText(resolutionSummary, defaultArbitrationConclusionByAction(normalizedAction));
    String detail = defaultText(resolutionDetail, summary);
    String nextStatus = "REOPEN".equals(normalizedAction) ? "PROCESSING" : "RESOLVED";
    if ("CLOSE_NO_FAULT".equals(normalizedAction)) {
      nextStatus = "CLOSED";
    }
    LocalDateTime now = LocalDateTime.now();
    ticket.updateCase(
        nextStatus,
        ticket.getPriorityLevel(),
        ticket.getAssignedArbitrator(),
        ticket.getHearingAt(),
        summary,
        detail,
        safeOperator,
        now);
    ticket.appendTimeline(
        "REVIEW_" + normalizedAction,
        "仲裁裁决",
        nextStatus,
        admn09ArbitrationStatusText(nextStatus),
        safeOperator,
        detail,
        now.toString());
    String disputeNextStatus = "REOPEN".equals(normalizedAction) ? "SUBMITTED" : "RESOLVED";
    if ("CLOSE_NO_FAULT".equals(normalizedAction)) {
      disputeNextStatus = "CLOSED";
    }
    dispute.updateStatus(
        disputeNextStatus,
        disputeStatusText(disputeNextStatus),
        "仲裁结果：" + summary,
        safeOperator,
        now);
    admn09ArbitrationStore.put(ticket.getTicketId(), ticket);
    return ticket;
  }

  public String admn09ArbitrationStatusText(String arbitrationStatus) {
    return switch (defaultText(arbitrationStatus, "").toUpperCase(Locale.ROOT)) {
      case "PENDING_ASSIGN" -> "待分派";
      case "PROCESSING" -> "仲裁处理中";
      case "RESOLVED" -> "已裁决";
      case "CLOSED" -> "已归档";
      default -> "处理中";
    };
  }

  public String admn09PriorityText(String priorityLevel) {
    return switch (defaultText(priorityLevel, "").toUpperCase(Locale.ROOT)) {
      case "LOW" -> "低优先级";
      case "MEDIUM" -> "中优先级";
      case "HIGH" -> "高优先级";
      case "URGENT" -> "紧急";
      default -> "中优先级";
    };
  }

  public List<Admn10BillingRuleEntity> listBillingRulesForAdmin(
      String ruleStatus, String sceneCode, String billingMode, String keyword) {
    String statusFilter = defaultText(ruleStatus, "").toUpperCase(Locale.ROOT);
    String sceneFilter = defaultText(sceneCode, "").toUpperCase(Locale.ROOT);
    String modeFilter = defaultText(billingMode, "").toUpperCase(Locale.ROOT);
    String keywordFilter = defaultText(keyword, "").toLowerCase(Locale.ROOT);
    return admn10BillingRuleStore.values().stream()
        .filter(
            item ->
                statusFilter.isBlank()
                    || statusFilter.equals(defaultText(item.getRuleStatus(), "").toUpperCase(Locale.ROOT)))
        .filter(
            item ->
                sceneFilter.isBlank()
                    || sceneFilter.equals(defaultText(item.getSceneCode(), "").toUpperCase(Locale.ROOT)))
        .filter(
            item ->
                modeFilter.isBlank()
                    || modeFilter.equals(defaultText(item.getBillingMode(), "").toUpperCase(Locale.ROOT)))
        .filter(
            item -> {
              if (keywordFilter.isBlank()) {
                return true;
              }
              return defaultText(item.getRuleId(), "").toLowerCase(Locale.ROOT).contains(keywordFilter)
                  || defaultText(item.getRuleCode(), "").toLowerCase(Locale.ROOT).contains(keywordFilter)
                  || defaultText(item.getRuleName(), "").toLowerCase(Locale.ROOT).contains(keywordFilter)
                  || defaultText(item.getSceneCode(), "").toLowerCase(Locale.ROOT).contains(keywordFilter)
                  || defaultText(item.getBillingMode(), "").toLowerCase(Locale.ROOT).contains(keywordFilter)
                  || defaultText(item.getRemark(), "").toLowerCase(Locale.ROOT).contains(keywordFilter);
            })
        .sorted(Comparator.comparing(Admn10BillingRuleEntity::getUpdatedAt).reversed())
        .toList();
  }

  public Admn10BillingRuleEntity getBillingRuleForAdmin(String ruleId) {
    String normalized = defaultText(ruleId, "");
    if (normalized.isBlank()) {
      throw new BaseException(ErrorCode.BAD_REQUEST.getCode(), "ruleId 不能为空");
    }
    Admn10BillingRuleEntity entity = admn10BillingRuleStore.get(normalized);
    if (entity == null) {
      throw new BaseException(ErrorCode.NOT_FOUND.getCode(), "计费规则不存在");
    }
    return entity;
  }

  public Admn10BillingRuleEntity upsertBillingRuleForAdmin(
      String ruleCode,
      String ruleName,
      String sceneCode,
      String billingMode,
      String feeCurrency,
      String basePriceYuan,
      String minFeeYuan,
      String maxFeeYuan,
      String ladderConfig,
      String effectiveFrom,
      String effectiveTo,
      String ruleStatus,
      String remark,
      String operator) {
    String safeRuleCode = defaultText(ruleCode, "").toUpperCase(Locale.ROOT);
    String safeRuleName = defaultText(ruleName, "");
    String safeSceneCode = defaultText(sceneCode, "").toUpperCase(Locale.ROOT);
    String safeBillingMode = defaultText(billingMode, "").toUpperCase(Locale.ROOT);
    if (safeRuleCode.isBlank() || safeRuleName.isBlank()) {
      throw new BaseException(ErrorCode.BAD_REQUEST.getCode(), "ruleCode 与 ruleName 不能为空");
    }
    if (!safeSceneCode.matches("SUBSCRIPTION|BILLING_ORDER|SETTLEMENT|ARBITRATION")) {
      throw new BaseException(
          ErrorCode.BAD_REQUEST.getCode(), "sceneCode 仅支持 SUBSCRIPTION/BILLING_ORDER/SETTLEMENT/ARBITRATION");
    }
    if (!safeBillingMode.matches("FIXED|LADDER|RATIO")) {
      throw new BaseException(ErrorCode.BAD_REQUEST.getCode(), "billingMode 仅支持 FIXED/LADDER/RATIO");
    }
    String safeCurrency = defaultText(feeCurrency, "CNY").toUpperCase(Locale.ROOT);
    if (!"CNY".equals(safeCurrency)) {
      throw new BaseException(ErrorCode.BAD_REQUEST.getCode(), "feeCurrency 当前仅支持 CNY");
    }
    parseNonNegativeMoney(basePriceYuan, "basePriceYuan");
    parseNonNegativeMoney(minFeeYuan, "minFeeYuan");
    parseNonNegativeMoney(maxFeeYuan, "maxFeeYuan");
    String safeEffectiveFrom = defaultText(effectiveFrom, LocalDate.now().toString());
    String safeEffectiveTo = defaultText(effectiveTo, "");
    if (!safeEffectiveTo.isBlank() && safeEffectiveFrom.compareTo(safeEffectiveTo) > 0) {
      throw new BaseException(ErrorCode.BAD_REQUEST.getCode(), "effectiveTo 不能早于 effectiveFrom");
    }
    String safeRuleStatus = defaultText(ruleStatus, "ACTIVE").toUpperCase(Locale.ROOT);
    if (!safeRuleStatus.matches("ACTIVE|DISABLED|DRAFT")) {
      throw new BaseException(ErrorCode.BAD_REQUEST.getCode(), "ruleStatus 仅支持 ACTIVE/DISABLED/DRAFT");
    }
    String safeLadderConfig = defaultText(ladderConfig, "");
    if ("LADDER".equals(safeBillingMode) && safeLadderConfig.isBlank()) {
      throw new BaseException(ErrorCode.BAD_REQUEST.getCode(), "billingMode=LADDER 时 ladderConfig 不能为空");
    }
    String safeRemark = defaultText(remark, "");
    String safeOperator = defaultText(operator, "admn10-admin");
    LocalDateTime now = LocalDateTime.now();
    Admn10BillingRuleEntity existing =
        admn10BillingRuleStore.values().stream()
            .filter(item -> safeRuleCode.equalsIgnoreCase(item.getRuleCode()))
            .findFirst()
            .orElse(null);
    if (existing == null) {
      Admn10BillingRuleEntity created =
          new Admn10BillingRuleEntity(
              "BR_"
                  + UUID.randomUUID().toString().replace("-", "").substring(0, 10).toUpperCase(Locale.ROOT),
              safeRuleCode,
              safeRuleName,
              safeSceneCode,
              safeBillingMode,
              safeCurrency,
              normalizeMoney(basePriceYuan),
              normalizeMoney(minFeeYuan),
              normalizeMoney(maxFeeYuan),
              safeLadderConfig,
              safeEffectiveFrom,
              safeEffectiveTo,
              safeRuleStatus,
              safeRemark,
              safeOperator,
              now,
              now);
      admn10BillingRuleStore.put(created.getRuleId(), created);
      return created;
    }
    existing.update(
        safeRuleCode,
        safeRuleName,
        safeSceneCode,
        safeBillingMode,
        safeCurrency,
        normalizeMoney(basePriceYuan),
        normalizeMoney(minFeeYuan),
        normalizeMoney(maxFeeYuan),
        safeLadderConfig,
        safeEffectiveFrom,
        safeEffectiveTo,
        safeRuleStatus,
        safeRemark,
        safeOperator,
        now);
    admn10BillingRuleStore.put(existing.getRuleId(), existing);
    return existing;
  }

  public String admn10SceneText(String sceneCode) {
    return switch (defaultText(sceneCode, "").toUpperCase(Locale.ROOT)) {
      case "SUBSCRIPTION" -> "套餐订阅";
      case "BILLING_ORDER" -> "账单出账";
      case "SETTLEMENT" -> "结算回款";
      case "ARBITRATION" -> "争议仲裁";
      default -> "其他";
    };
  }

  public String admn10BillingModeText(String billingMode) {
    return switch (defaultText(billingMode, "").toUpperCase(Locale.ROOT)) {
      case "FIXED" -> "固定金额";
      case "LADDER" -> "阶梯计费";
      case "RATIO" -> "比例计费";
      default -> "未知";
    };
  }

  public String admn10RuleStatusText(String ruleStatus) {
    return switch (defaultText(ruleStatus, "").toUpperCase(Locale.ROOT)) {
      case "ACTIVE" -> "生效中";
      case "DISABLED" -> "已停用";
      case "DRAFT" -> "草稿";
      default -> "未知";
    };
  }

  private String mergeIssueTags(String currentTags, String ruleCode, String qualityStatus, String riskLevel) {
    List<String> tags = new ArrayList<>();
    String existed = defaultText(currentTags, "");
    if (!existed.isBlank()) {
      for (String item : existed.split(",")) {
        String trimmed = item == null ? "" : item.trim().toUpperCase(Locale.ROOT);
        if (!trimmed.isBlank() && !tags.contains(trimmed)) {
          tags.add(trimmed);
        }
      }
    }
    String rule = defaultText(ruleCode, "").toUpperCase(Locale.ROOT);
    if (!rule.isBlank() && !tags.contains(rule)) {
      tags.add(rule);
    }
    String statusTag = "QA_" + defaultText(qualityStatus, "PENDING").toUpperCase(Locale.ROOT);
    if (!tags.contains(statusTag)) {
      tags.add(statusTag);
    }
    String riskTag = "RISK_" + defaultText(riskLevel, "LOW").toUpperCase(Locale.ROOT);
    if (!tags.contains(riskTag)) {
      tags.add(riskTag);
    }
    return String.join(",", tags);
  }

  public List<Admn05CategorySpecDictEntity> listCategorySpecDictsForAdmin(
      String keyword, String status, String sceneCode) {
    String keywordFilter = defaultText(keyword, "").toLowerCase(Locale.ROOT);
    String statusFilter = defaultText(status, "").toUpperCase(Locale.ROOT);
    String sceneFilter = defaultText(sceneCode, "").toUpperCase(Locale.ROOT);
    return admn05CategorySpecStore.values().stream()
        .filter(
            item ->
                statusFilter.isBlank()
                    || statusFilter.equals(defaultText(item.getStatus(), "").toUpperCase(Locale.ROOT)))
        .filter(
            item ->
                sceneFilter.isBlank()
                    || sceneFilter.equals(defaultText(item.getSceneCode(), "").toUpperCase(Locale.ROOT)))
        .filter(
            item ->
                keywordFilter.isBlank()
                    || defaultText(item.getDictId(), "").toLowerCase(Locale.ROOT).contains(keywordFilter)
                    || defaultText(item.getCategoryCode(), "").toLowerCase(Locale.ROOT).contains(keywordFilter)
                    || defaultText(item.getCategoryName(), "").toLowerCase(Locale.ROOT).contains(keywordFilter)
                    || defaultText(item.getSpecName(), "").toLowerCase(Locale.ROOT).contains(keywordFilter)
                    || defaultText(item.getSpecValue(), "").toLowerCase(Locale.ROOT).contains(keywordFilter)
                    || defaultText(item.getRemark(), "").toLowerCase(Locale.ROOT).contains(keywordFilter))
        .sorted(
            Comparator.comparingInt(Admn05CategorySpecDictEntity::getSortNo)
                .thenComparing(Admn05CategorySpecDictEntity::getUpdatedAt, Comparator.reverseOrder()))
        .toList();
  }

  public Admn05CategorySpecDictEntity getCategorySpecDictForAdmin(String dictId) {
    String normalized = defaultText(dictId, "");
    if (normalized.isBlank()) {
      throw new BaseException(ErrorCode.BAD_REQUEST.getCode(), "dictId 不能为空");
    }
    Admn05CategorySpecDictEntity entity = admn05CategorySpecStore.get(normalized);
    if (entity == null) {
      throw new BaseException(ErrorCode.NOT_FOUND.getCode(), "类目规格词库项不存在");
    }
    return entity;
  }

  public Admn05CategorySpecDictEntity upsertCategorySpecDictForAdmin(
      String categoryCode,
      String categoryName,
      String specName,
      String specValue,
      String sceneCode,
      String status,
      Integer sortNo,
      String remark,
      String operator) {
    String safeCategoryCode = defaultText(categoryCode, "").toUpperCase(Locale.ROOT);
    String safeCategoryName = defaultText(categoryName, "");
    String safeSpecName = defaultText(specName, "");
    String safeSpecValue = defaultText(specValue, "");
    if (safeCategoryCode.isBlank()
        || safeCategoryName.isBlank()
        || safeSpecName.isBlank()
        || safeSpecValue.isBlank()) {
      throw new BaseException(ErrorCode.BAD_REQUEST.getCode(), "category/spec 字段不能为空");
    }
    String safeSceneCode = defaultText(sceneCode, "BUYER_INQUIRY").toUpperCase(Locale.ROOT);
    if (!safeSceneCode.matches("BUYER_INQUIRY|MERCHANT_QUOTE|RISK_CONTROL")) {
      throw new BaseException(
          ErrorCode.BAD_REQUEST.getCode(), "sceneCode 仅支持 BUYER_INQUIRY/MERCHANT_QUOTE/RISK_CONTROL");
    }
    String safeStatus = defaultText(status, "ACTIVE").toUpperCase(Locale.ROOT);
    if (!safeStatus.matches("ACTIVE|DISABLED")) {
      throw new BaseException(ErrorCode.BAD_REQUEST.getCode(), "status 仅支持 ACTIVE/DISABLED");
    }
    int safeSortNo = sortNo == null ? 100 : Math.max(sortNo, 0);
    String safeRemark = defaultText(remark, "");
    String safeOperator = defaultText(operator, "admn05-admin");
    LocalDateTime now = LocalDateTime.now();
    Admn05CategorySpecDictEntity existing =
        admn05CategorySpecStore.values().stream()
            .filter(item -> safeCategoryCode.equalsIgnoreCase(item.getCategoryCode()))
            .filter(item -> safeSpecName.equalsIgnoreCase(item.getSpecName()))
            .filter(item -> safeSpecValue.equalsIgnoreCase(item.getSpecValue()))
            .filter(item -> safeSceneCode.equalsIgnoreCase(item.getSceneCode()))
            .findFirst()
            .orElse(null);
    if (existing == null) {
      Admn05CategorySpecDictEntity created =
          new Admn05CategorySpecDictEntity(
              "DICT_" + UUID.randomUUID().toString().replace("-", "").substring(0, 12).toUpperCase(Locale.ROOT),
              safeCategoryCode,
              safeCategoryName,
              safeSpecName,
              safeSpecValue,
              safeSceneCode,
              safeStatus,
              safeSortNo,
              safeRemark,
              safeOperator,
              now,
              now);
      admn05CategorySpecStore.put(created.getDictId(), created);
      return created;
    }
    existing.update(
        safeCategoryCode,
        safeCategoryName,
        safeSpecName,
        safeSpecValue,
        safeSceneCode,
        safeStatus,
        safeSortNo,
        safeRemark,
        safeOperator,
        now);
    admn05CategorySpecStore.put(existing.getDictId(), existing);
    return existing;
  }

  public String admn05SceneText(String sceneCode) {
    return switch (defaultText(sceneCode, "").toUpperCase(Locale.ROOT)) {
      case "BUYER_INQUIRY" -> "买家询价";
      case "MERCHANT_QUOTE" -> "商家报价";
      case "RISK_CONTROL" -> "风控审核";
      default -> "其他场景";
    };
  }

  public Admn04AuditLogEntity appendAuditLogForAdmin(
      String moduleCode,
      String actionCode,
      String targetType,
      String targetId,
      String operator,
      String operatorType,
      String requestId,
      String result,
      String riskLevel,
      String summary,
      String beforeSnapshot,
      String afterSnapshot,
      String clientIp,
      String userAgent) {
    LocalDateTime now = LocalDateTime.now();
    Admn04AuditLogEntity entity =
        new Admn04AuditLogEntity(
            "AUDIT_" + UUID.randomUUID().toString().replace("-", "").substring(0, 12).toUpperCase(Locale.ROOT),
            defaultText(moduleCode, "UNKNOWN"),
            defaultText(actionCode, "UNKNOWN"),
            defaultText(targetType, "UNKNOWN"),
            defaultText(targetId, "-"),
            defaultText(operator, "system"),
            defaultText(operatorType, "ADMIN"),
            defaultText(requestId, ""),
            defaultText(result, "SUCCESS").toUpperCase(Locale.ROOT),
            defaultText(riskLevel, "LOW").toUpperCase(Locale.ROOT),
            defaultText(summary, ""),
            defaultText(beforeSnapshot, ""),
            defaultText(afterSnapshot, ""),
            defaultText(clientIp, ""),
            defaultText(userAgent, ""),
            now);
    admn04AuditLogStore.put(entity.getLogId(), entity);
    return entity;
  }

  public List<Admn04AuditLogEntity> listAuditLogsForAdmin(
      String moduleCode, String actionCode, String resultStatus, String operator, String keyword) {
    String moduleFilter = defaultText(moduleCode, "").toUpperCase(Locale.ROOT);
    String actionFilter = defaultText(actionCode, "").toUpperCase(Locale.ROOT);
    String resultFilter = defaultText(resultStatus, "").toUpperCase(Locale.ROOT);
    String operatorFilter = defaultText(operator, "").toLowerCase(Locale.ROOT);
    String keywordFilter = defaultText(keyword, "").toLowerCase(Locale.ROOT);
    return admn04AuditLogStore.values().stream()
        .filter(
            item ->
                moduleFilter.isBlank()
                    || moduleFilter.equals(defaultText(item.getModuleCode(), "").toUpperCase(Locale.ROOT)))
        .filter(
            item ->
                actionFilter.isBlank()
                    || actionFilter.equals(defaultText(item.getActionCode(), "").toUpperCase(Locale.ROOT)))
        .filter(
            item ->
                resultFilter.isBlank()
                    || resultFilter.equals(defaultText(item.getResult(), "").toUpperCase(Locale.ROOT)))
        .filter(
            item ->
                operatorFilter.isBlank()
                    || defaultText(item.getOperator(), "").toLowerCase(Locale.ROOT).contains(operatorFilter))
        .filter(
            item ->
                keywordFilter.isBlank()
                    || defaultText(item.getLogId(), "").toLowerCase(Locale.ROOT).contains(keywordFilter)
                    || defaultText(item.getSummary(), "").toLowerCase(Locale.ROOT).contains(keywordFilter)
                    || defaultText(item.getTargetId(), "").toLowerCase(Locale.ROOT).contains(keywordFilter)
                    || defaultText(item.getRequestId(), "").toLowerCase(Locale.ROOT).contains(keywordFilter))
        .sorted(Comparator.comparing(Admn04AuditLogEntity::getOperateAt).reversed())
        .toList();
  }

  public Admn04AuditLogEntity getAuditLogForAdmin(String logId) {
    String normalizedLogId = defaultText(logId, "");
    if (normalizedLogId.isBlank()) {
      throw new BaseException(ErrorCode.BAD_REQUEST.getCode(), "logId 不能为空");
    }
    Admn04AuditLogEntity entity = admn04AuditLogStore.get(normalizedLogId);
    if (entity == null) {
      throw new BaseException(ErrorCode.NOT_FOUND.getCode(), "审计日志不存在");
    }
    return entity;
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

  private boolean isBuyerBlacklisted(String userId) {
    Admn02BuyerBlacklistRecordEntity record = buyerBlacklistStore.get(defaultText(userId, ""));
    return record != null && record.isBlacklisted();
  }

  private List<String> normalizePermissionCodes(List<String> permissionCodes) {
    if (permissionCodes == null) {
      throw new BaseException(ErrorCode.BAD_REQUEST.getCode(), "permissionCodes 不能为空");
    }
    List<String> normalized =
        permissionCodes.stream()
            .map(code -> defaultText(code, "").toUpperCase(Locale.ROOT))
            .filter(code -> !code.isBlank())
            .distinct()
            .toList();
    if (normalized.isEmpty()) {
      throw new BaseException(ErrorCode.BAD_REQUEST.getCode(), "permissionCodes 至少包含1项");
    }
    return normalized;
  }

  public String admn04ModuleName(String moduleCode) {
    return switch (defaultText(moduleCode, "").toUpperCase(Locale.ROOT)) {
      case "ADMN01" -> "商家认证审核";
      case "ADMN02" -> "买家与黑名单管理";
      case "ADMN03" -> "角色权限管理";
      case "ADMN04" -> "操作审计日志";
      case "ADMN05" -> "类目规格词库管理";
      case "ADMN06" -> "线索质检中心";
      case "ADMN08" -> "成交漏斗分析";
      case "ADMN09" -> "仲裁工单中心";
      case "ADMN10" -> "计费规则配置";
      case "ADMN11" -> "支付与退款管理";
      case "ADMN12" -> "广告位排期中心";
      case "ADMN13" -> "信用模型版本管理";
      default -> "其他模块";
    };
  }

  public String admn04ActionName(String actionCode) {
    return switch (defaultText(actionCode, "").toUpperCase(Locale.ROOT)) {
      case "CERT_REVIEW" -> "审核认证单";
      case "BUYER_BLACKLIST" -> "买家黑名单操作";
      case "ROLE_UPSERT" -> "角色新增/编辑";
      case "ROLE_PERMISSION_UPDATE" -> "角色权限更新";
      case "AUDIT_QUERY" -> "审计日志查询";
      case "DICT_UPSERT" -> "词库新增/更新";
      case "DICT_QUERY" -> "词库查询";
      case "LEAD_QA_REVIEW" -> "线索质检复核";
      case "LEAD_QA_QUERY" -> "线索质检查询";
      case "DEAL_FUNNEL_QUERY" -> "成交漏斗查询";
      case "ARBITRATION_ASSIGN" -> "仲裁分派处理";
      case "ARBITRATION_REVIEW" -> "仲裁裁决处理";
      case "ARBITRATION_QUERY" -> "仲裁工单查询";
      case "BILLING_RULE_UPSERT" -> "计费规则新增/更新";
      case "BILLING_RULE_QUERY" -> "计费规则查询";
      case "PAYMENT_REFUND_REVIEW" -> "退款审核处理";
      case "PAYMENT_REFUND_QUERY" -> "支付退款查询";
      case "AD_SLOT_SCHEDULE_UPSERT" -> "广告位排期新增/更新";
      case "AD_SLOT_SCHEDULE_QUERY" -> "广告位排期查询";
      case "CREDIT_MODEL_VERSION_UPSERT" -> "信用模型版本新增/更新";
      case "CREDIT_MODEL_VERSION_QUERY" -> "信用模型版本查询";
      default -> "通用操作";
    };
  }

  public String admn04ResultText(String resultCode) {
    return switch (defaultText(resultCode, "").toUpperCase(Locale.ROOT)) {
      case "SUCCESS" -> "成功";
      case "FAILED" -> "失败";
      default -> "未知";
    };
  }

  public String admn04OperatorRole(String operatorType) {
    return switch (defaultText(operatorType, "").toUpperCase(Locale.ROOT)) {
      case "ADMIN" -> "管理端账号";
      case "SYSTEM" -> "系统任务";
      default -> "未知";
    };
  }

  public List<String> admn04Tags(String moduleCode, String riskLevel, String resultCode) {
    List<String> tags = new ArrayList<>();
    tags.add(defaultText(moduleCode, "UNKNOWN").toUpperCase(Locale.ROOT));
    tags.add(defaultText(riskLevel, "LOW").toUpperCase(Locale.ROOT));
    tags.add(defaultText(resultCode, "SUCCESS").toUpperCase(Locale.ROOT));
    return tags;
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
    seedAdmn02BuyerBlacklist(seed);
    seedAdmn02ExtraBuyers();
    seedAdmn03Roles();
    seedAdmn04AuditLogs();
    seedAdmn05CategorySpecDicts();
    seedAdmn06LeadQuality();
    seedAdmn09ArbitrationTickets();
    seedAdmn10BillingRules();
    seedAdmn12AdSlotSchedules();
    seedAdmn13CreditModelVersions();
    seedNegotiation(seed);
    seedOrders(seed);
    seedTradeTerms(seed);
    seedAfterSaleDisputes(seed);
    seedCashierOrders(seed);
    seedAdmn11PaymentRefunds(seed);
    seedInvoiceTitles(seed);
    seedInvoiceApplications(seed);
    seedCreditScores(seed);
    seedDispatchAppeals(seed);
  }

  private void seedAdmn02BuyerBlacklist(AuthUserEntity buyer) {
    buyerBlacklistStore.put(
        buyer.getUserId(),
        new Admn02BuyerBlacklistRecordEntity(
            buyer.getUserId(),
            false,
            "NORMAL",
            "初始买家状态正常",
            "seed",
            LocalDateTime.now().minusDays(2)));
  }

  private void seedAdmn02ExtraBuyers() {
    LocalDateTime now = LocalDateTime.now();
    AuthUserEntity normalBuyer =
        new AuthUserEntity(
            "U000000000021",
            "MOBILE",
            "13966668888",
            "13966668888",
            maskPhone("13966668888"),
            hashPassword("Demo@123456"),
            "河北北方钢联采购中心",
            "采购经理李宁",
            "BUYER",
            "ACTIVE",
            now.minusDays(10),
            now.minusHours(22));
    userStore.put(normalBuyer.getAccount(), normalBuyer);
    buyerBlacklistStore.put(
        normalBuyer.getUserId(),
        new Admn02BuyerBlacklistRecordEntity(
            normalBuyer.getUserId(),
            false,
            "NORMAL",
            "历史行为正常",
            "seed",
            now.minusHours(20)));

    AuthUserEntity blacklistedBuyer =
        new AuthUserEntity(
            "U000000000022",
            "MOBILE",
            "13955557777",
            "13955557777",
            maskPhone("13955557777"),
            hashPassword("Demo@123456"),
            "华北波动采购账号",
            "风控关注对象",
            "BUYER",
            "ACTIVE",
            now.minusDays(15),
            now.minusHours(6));
    userStore.put(blacklistedBuyer.getAccount(), blacklistedBuyer);
    buyerBlacklistStore.put(
        blacklistedBuyer.getUserId(),
        new Admn02BuyerBlacklistRecordEntity(
            blacklistedBuyer.getUserId(),
            true,
            "MULTI_DISPUTE",
            "近30天争议率偏高，临时拉黑",
            "seed-risk",
            now.minusHours(4)));
  }

  private void seedAdmn03Roles() {
    LocalDateTime now = LocalDateTime.now();
    Admn03RolePermissionEntity superAdmin =
        new Admn03RolePermissionEntity(
            "RL00000001",
            "SUPER_ADMIN",
            "超级管理员",
            "SYSTEM",
            "ACTIVE",
            "平台全局权限管理角色",
            List.of(
                "DASHBOARD_VIEW",
                "LEAD_OPS_MANAGE",
                "RISK_ALERT_MANAGE",
                "ADMN01_CERT_REVIEW",
                "ADMN02_BLACKLIST_MANAGE",
                "ADMN03_RBAC_MANAGE",
                "ADMN04_AUDIT_LOG_VIEW",
                "ADMN05_DICT_MANAGE",
                "ADMN06_LEAD_QA_MANAGE",
                "ADMN08_FUNNEL_VIEW",
                "ADMN09_ARBITRATION_MANAGE",
                "ADMN10_BILLING_RULE_MANAGE",
                "ADMN11_PAYMENT_REFUND_MANAGE",
                "ADMN12_AD_SLOT_SCHEDULE_MANAGE",
                "ADMN13_CREDIT_MODEL_VERSION_MANAGE"),
            "seed",
            now.minusDays(30),
            now.minusDays(1));
    admn03RoleStore.put(superAdmin.getRoleId(), superAdmin);

    Admn03RolePermissionEntity riskAdmin =
        new Admn03RolePermissionEntity(
            "RL00000002",
            "RISK_ADMIN",
            "风控管理员",
            "SYSTEM",
            "ACTIVE",
            "负责风控预警与黑名单治理",
            List.of(
                "RISK_ALERT_MANAGE",
                "ADMN02_BLACKLIST_MANAGE",
                "DASHBOARD_VIEW",
                "ADMN04_AUDIT_LOG_VIEW",
                "ADMN05_DICT_MANAGE",
                "ADMN06_LEAD_QA_MANAGE",
                "ADMN09_ARBITRATION_MANAGE",
                "ADMN10_BILLING_RULE_MANAGE",
                "ADMN11_PAYMENT_REFUND_MANAGE",
                "ADMN12_AD_SLOT_SCHEDULE_MANAGE",
                "ADMN13_CREDIT_MODEL_VERSION_MANAGE"),
            "seed",
            now.minusDays(20),
            now.minusDays(2));
    admn03RoleStore.put(riskAdmin.getRoleId(), riskAdmin);

    Admn03RolePermissionEntity certReviewer =
        new Admn03RolePermissionEntity(
            "RL00000003",
            "CERT_REVIEWER",
            "认证审核员",
            "CUSTOM",
            "ACTIVE",
            "负责商家认证审核与资料复核",
            List.of("ADMN01_CERT_REVIEW", "DASHBOARD_VIEW"),
            "seed",
            now.minusDays(18),
            now.minusHours(12));
    admn03RoleStore.put(certReviewer.getRoleId(), certReviewer);
  }

  private void seedAdmn04AuditLogs() {
    appendAuditLogForAdmin(
        "ADMN01",
        "CERT_REVIEW",
        "CERTIFICATION",
        "EC_SAMPLE_0001",
        "seed-admin",
        "ADMIN",
        "TRACE_SEED_ADMN01",
        "SUCCESS",
        "MEDIUM",
        "初始化审计日志：商家认证审核通过",
        "{\"status\":\"PENDING_REVIEW\"}",
        "{\"status\":\"APPROVED\"}",
        "127.0.0.1",
        "seed");
    appendAuditLogForAdmin(
        "ADMN02",
        "BUYER_BLACKLIST",
        "BUYER",
        "U000000000022",
        "seed-risk",
        "ADMIN",
        "TRACE_SEED_ADMN02",
        "SUCCESS",
        "HIGH",
        "初始化审计日志：买家加入黑名单",
        "{\"blacklisted\":false}",
        "{\"blacklisted\":true,\"reason\":\"MULTI_DISPUTE\"}",
        "127.0.0.1",
        "seed");
    appendAuditLogForAdmin(
        "ADMN03",
        "ROLE_PERMISSION_UPDATE",
        "ROLE",
        "RL00000002",
        "seed",
        "ADMIN",
        "TRACE_SEED_ADMN03",
        "SUCCESS",
        "LOW",
        "初始化审计日志：角色权限调整",
        "{\"permissions\":[\"RISK_ALERT_MANAGE\"]}",
        "{\"permissions\":[\"RISK_ALERT_MANAGE\",\"ADMN04_AUDIT_LOG_VIEW\"]}",
        "127.0.0.1",
        "seed");
  }

  private void seedAdmn05CategorySpecDicts() {
    LocalDateTime now = LocalDateTime.now();
    Admn05CategorySpecDictEntity rebar20 =
        new Admn05CategorySpecDictEntity(
            "DICT_REBAR_001",
            "REBAR",
            "螺纹钢",
            "规格",
            "HRB400E Φ20*12m",
            "BUYER_INQUIRY",
            "ACTIVE",
            10,
            "常用工地规格",
            "seed",
            now.minusDays(8),
            now.minusDays(1));
    admn05CategorySpecStore.put(rebar20.getDictId(), rebar20);

    Admn05CategorySpecDictEntity hrc3mm =
        new Admn05CategorySpecDictEntity(
            "DICT_HRC_001",
            "HRC",
            "热轧卷板",
            "厚度",
            "3.0*1500*C",
            "MERCHANT_QUOTE",
            "ACTIVE",
            20,
            "高频报价规格",
            "seed",
            now.minusDays(7),
            now.minusHours(18));
    admn05CategorySpecStore.put(hrc3mm.getDictId(), hrc3mm);

    Admn05CategorySpecDictEntity plate10mm =
        new Admn05CategorySpecDictEntity(
            "DICT_PLATE_001",
            "PLATE",
            "中厚板",
            "材质",
            "Q355B 10*2000*8000",
            "RISK_CONTROL",
            "DISABLED",
            30,
            "历史规格，暂不建议前台曝光",
            "seed",
            now.minusDays(6),
            now.minusHours(30));
    admn05CategorySpecStore.put(plate10mm.getDictId(), plate10mm);
  }

  private void seedAdmn06LeadQuality() {
    LocalDateTime now = LocalDateTime.now();
    Admn06LeadQualityEntity inquiryQa =
        new Admn06LeadQualityEntity(
            "QA_INQ_0001",
            "INQUIRY",
            "ML20260418001",
            "ML-20260418-20260418001",
            "PASS",
            92,
            "LOW",
            "SPEC_STANDARD,CONTACT_VALID,QA_PASS",
            "字段完整，联系方式有效",
            "qa-seed",
            now.minusDays(4),
            now.minusHours(20));
    admn06LeadQualityStore.put(inquiryQa.getQualityId(), inquiryQa);

    Admn06LeadQualityEntity siteAdQa =
        new Admn06LeadQualityEntity(
            "QA_AD_0001",
            "SITE_AD",
            "SAL20260418001",
            "ADL-20260418-SAL20260418001",
            "RECHECK",
            66,
            "MEDIUM",
            "BUDGET_AMBIGUOUS,FOLLOW_DELAY,RISK_MEDIUM",
            "预算区间偏宽，需补充投放目标与复联计划",
            "qa-seed",
            now.minusDays(3),
            now.minusHours(8));
    admn06LeadQualityStore.put(siteAdQa.getQualityId(), siteAdQa);

    Admn06LeadQualityEntity inquiryRiskQa =
        new Admn06LeadQualityEntity(
            "QA_INQ_0002",
            "INQUIRY",
            "ML20260418002",
            "ML-20260418-20260418002",
            "PENDING",
            48,
            "HIGH",
            "CONTACT_SUSPECT,REPEAT_SUBMIT,RISK_HIGH",
            "疑似重复提交，待人工二次核验",
            "qa-seed",
            now.minusDays(2),
            now.minusHours(2));
    admn06LeadQualityStore.put(inquiryRiskQa.getQualityId(), inquiryRiskQa);
  }

  private void seedAdmn09ArbitrationTickets() {
    LocalDateTime now = LocalDateTime.now();
    Admn09ArbitrationTicketEntity first =
        new Admn09ArbitrationTicketEntity(
            "ARB_0001",
            "AS00000001",
            "唐山",
            "PENDING_ASSIGN",
            "HIGH",
            "",
            "",
            "",
            "争议单已升级至平台仲裁，待分派仲裁员",
            "seed",
            now.minusHours(6),
            now.minusHours(3));
    first.appendTimeline(
        "UPGRADE_FROM_DISPUTE",
        "升级仲裁",
        "PENDING_ASSIGN",
        admn09ArbitrationStatusText("PENDING_ASSIGN"),
        "system",
        "交付延迟争议升级至平台仲裁",
        now.minusHours(6).toString());
    first.appendTimeline(
        "WAIT_ASSIGN",
        "待分派仲裁员",
        "PENDING_ASSIGN",
        admn09ArbitrationStatusText("PENDING_ASSIGN"),
        "system",
        "待值班仲裁员接单",
        now.minusHours(3).toString());
    admn09ArbitrationStore.put(first.getTicketId(), first);

    Admn09ArbitrationTicketEntity second =
        new Admn09ArbitrationTicketEntity(
            "ARB_0002",
            "AS00000002",
            "无锡",
            "PROCESSING",
            "MEDIUM",
            "仲裁员-周宁",
            now.plusHours(4).toString(),
            "已受理质量偏差争议，待复检报告回传",
            "双方同意第三方复检后裁决",
            "seed",
            now.minusDays(1),
            now.minusHours(2));
    second.appendTimeline(
        "UPGRADE_FROM_DISPUTE",
        "升级仲裁",
        "PENDING_ASSIGN",
        admn09ArbitrationStatusText("PENDING_ASSIGN"),
        "system",
        "质量异议升级至仲裁流程",
        now.minusDays(1).toString());
    second.appendTimeline(
        "ASSIGN_ACCEPT",
        "仲裁分派",
        "PROCESSING",
        admn09ArbitrationStatusText("PROCESSING"),
        "admn09-seed",
        "已指派仲裁员-周宁",
        now.minusHours(12).toString());
    admn09ArbitrationStore.put(second.getTicketId(), second);
  }

  private void seedAdmn10BillingRules() {
    LocalDateTime now = LocalDateTime.now();
    Admn10BillingRuleEntity subscriptionFixed =
        new Admn10BillingRuleEntity(
            "BR_0001",
            "SUBSCRIPTION_FIXED",
            "套餐订阅固定月费规则",
            "SUBSCRIPTION",
            "FIXED",
            "CNY",
            "2999",
            "1999",
            "9999",
            "",
            now.minusDays(30).toLocalDate().toString(),
            "",
            "ACTIVE",
            "标准套餐固定月费，按自然月出账",
            "seed",
            now.minusDays(30),
            now.minusDays(2));
    admn10BillingRuleStore.put(subscriptionFixed.getRuleId(), subscriptionFixed);

    Admn10BillingRuleEntity billingLadder =
        new Admn10BillingRuleEntity(
            "BR_0002",
            "BILLING_LADDER_VOLUME",
            "账单出账阶梯计费规则",
            "BILLING_ORDER",
            "LADDER",
            "CNY",
            "0",
            "500",
            "20000",
            "0-100:8.5;101-300:7.2;301-999999:6.5",
            now.minusDays(15).toLocalDate().toString(),
            "",
            "ACTIVE",
            "按月成交吨位阶梯计费，自动写入账单",
            "seed",
            now.minusDays(15),
            now.minusHours(12));
    admn10BillingRuleStore.put(billingLadder.getRuleId(), billingLadder);

    Admn10BillingRuleEntity settlementRatio =
        new Admn10BillingRuleEntity(
            "BR_0003",
            "SETTLEMENT_RATIO_SERVICE",
            "结算回款比例计费规则",
            "SETTLEMENT",
            "RATIO",
            "CNY",
            "0.008",
            "300",
            "12000",
            "",
            now.minusDays(7).toLocalDate().toString(),
            now.plusDays(60).toLocalDate().toString(),
            "DRAFT",
            "按回款额0.8%计费，当前为灰度草稿",
            "seed",
            now.minusDays(7),
            now.minusHours(6));
    admn10BillingRuleStore.put(settlementRatio.getRuleId(), settlementRatio);
  }

  private void seedAdmn11PaymentRefunds(AuthUserEntity user) {
    LocalDateTime now = LocalDateTime.now();
    Admn11PaymentRefundEntity first =
        new Admn11PaymentRefundEntity(
            "RF00000001",
            "RF-20260418-0001",
            "CS00000002",
            "OD0002",
            "OD-20260416-0008",
            "INQ-20260418-2210",
            user.getUserId(),
            "演示钢贸有限公司",
            "无锡铭泰供应链",
            "热轧卷板Q235B 3.0*1500 300吨",
            "BANK_TRANSFER",
            payChannelText("BANK_TRANSFER"),
            "1104000",
            "1104000",
            "FULL",
            "QUALITY_DISPUTE",
            "45000",
            "",
            "",
            "PENDING_REVIEW",
            "",
            "buyer-finance",
            "",
            "",
            "重复质检不通过，买方申请全额退回",
            now.minusDays(2).toString(),
            "",
            "",
            now.minusDays(2),
            now.minusHours(3));
    first.appendProgress(
        "SUBMIT",
        "提交退款申请",
        "PENDING_REVIEW",
        "待审核",
        "buyer-finance",
        "买方提交退款申请，等待管理端审核",
        now.minusDays(2).toString());
    first.appendProgress(
        "PAYMENT_PROOF",
        "支付凭证核验",
        "PENDING_REVIEW",
        "待审核",
        "system",
        "已拉取支付流水，待人工复核",
        now.minusDays(1).toString());
    admn11PaymentRefundStore.put(first.getRefundId(), first);

    Admn11PaymentRefundEntity second =
        new Admn11PaymentRefundEntity(
            "RF00000002",
            "RF-20260416-0008",
            "CS00000001",
            "OD0001",
            "OD-20260418-0001",
            "INQ-20260419-3301",
            user.getUserId(),
            "演示钢贸有限公司",
            "唐山弘达钢贸",
            "螺纹钢HRB400E Φ20 500吨",
            "ALIPAY",
            payChannelText("ALIPAY"),
            "1760000",
            "1760000",
            "PARTIAL",
            "DOUBLE_PAYMENT",
            "12000",
            "12000",
            "12000",
            "REFUNDED",
            "",
            "buyer-finance",
            "finance-ops",
            "admn11-seed",
            "确认重复支付，已原路退款",
            now.minusDays(6).toString(),
            now.minusDays(4).toString(),
            now.minusDays(2).toString(),
            now.minusDays(6),
            now.minusDays(2));
    second.appendProgress(
        "SUBMIT",
        "提交退款申请",
        "PENDING_REVIEW",
        "待审核",
        "buyer-finance",
        "发现重复支付并提交退款",
        now.minusDays(6).toString());
    second.appendProgress(
        "APPROVE",
        "审核通过",
        "APPROVED",
        "退款通过",
        "admn11-seed",
        "审核通过，进入退款执行",
        now.minusDays(4).toString());
    second.appendProgress(
        "REFUND_DONE",
        "退款完成",
        "REFUNDED",
        "已退款",
        "finance-ops",
        "款项已回退至原支付账户",
        now.minusDays(2).toString());
    admn11PaymentRefundStore.put(second.getRefundId(), second);
  }

  private void seedAdmn12AdSlotSchedules() {
    LocalDateTime now = LocalDateTime.now();
    Admn12AdSlotScheduleEntity first =
        new Admn12AdSlotScheduleEntity(
            "SCH000001",
            "SLOT_HOME_FOCUS_01",
            "首页焦点大图位",
            "HOME_TOP_BANNER",
            "NORTH_CHINA",
            "唐山",
            "ACTIVE",
            "ON_SALE",
            "2026-04-20",
            "2026-05-20",
            "1",
            "1",
            "¥12,000/天",
            "https://cdn.huodaizi.com/ad/home-focus-01.png",
            "唐山钢贸联合会",
            "弘达钢贸 5.20 钢市采购节",
            "李商务",
            "主打螺纹钢采购节活动，支持跳转落地页",
            now.minusDays(12),
            now.minusDays(1));
    first.appendWindow("2026-04-20", "2026-04-30", "SOLD", "已售", "唐山钢贸联合会");
    first.appendWindow("2026-05-01", "2026-05-20", "SOLD", "已售", "唐山钢贸联合会");
    admn12AdSlotScheduleStore.put(first.getScheduleId(), first);

    Admn12AdSlotScheduleEntity second =
        new Admn12AdSlotScheduleEntity(
            "SCH000002",
            "SLOT_CITY_RECO_03",
            "城市推荐轮播位",
            "CITY_RECOMMEND_CAROUSEL",
            "EAST_CHINA",
            "无锡",
            "ACTIVE",
            "PARTIAL",
            "2026-04-18",
            "2026-05-31",
            "3",
            "2",
            "¥6,500/天",
            "https://cdn.huodaizi.com/ad/city-reco-03.png",
            "无锡板材联盟",
            "无锡板材周特辑",
            "周商务",
            "轮播第三位，5月中旬有空档可售",
            now.minusDays(9),
            now.minusHours(20));
    second.appendWindow("2026-04-18", "2026-05-10", "SOLD", "已售", "无锡板材联盟");
    second.appendWindow("2026-05-11", "2026-05-18", "LOCKED", "锁定中", "待签约客户");
    second.appendWindow("2026-05-19", "2026-05-31", "AVAILABLE", "可售", "");
    admn12AdSlotScheduleStore.put(second.getScheduleId(), second);

    Admn12AdSlotScheduleEntity third =
        new Admn12AdSlotScheduleEntity(
            "SCH000003",
            "SLOT_LOGISTICS_02",
            "仓配服务推荐位",
            "LOGISTICS_SERVICE_TILE",
            "SOUTH_CHINA",
            "佛山",
            "PAUSED",
            "OFFLINE",
            "2026-04-10",
            "2026-04-30",
            "2",
            "0",
            "¥4,800/天",
            "",
            "",
            "",
            "赵运营",
            "版位素材整改中，暂不售卖",
            now.minusDays(20),
            now.minusDays(2));
    third.appendWindow("2026-04-10", "2026-04-20", "OFFLINE", "下线", "");
    third.appendWindow("2026-04-21", "2026-04-30", "OFFLINE", "下线", "");
    admn12AdSlotScheduleStore.put(third.getScheduleId(), third);
  }

  public List<Admn12AdSlotScheduleEntity> listAdSlotSchedulesForAdmin(
      String scheduleStatus, String slotType, String cityCode, String keyword) {
    String statusFilter = defaultText(scheduleStatus, "").toUpperCase(Locale.ROOT);
    String slotTypeFilter = defaultText(slotType, "").toUpperCase(Locale.ROOT);
    String cityFilter = defaultText(cityCode, "").toUpperCase(Locale.ROOT);
    String keywordFilter = defaultText(keyword, "").toLowerCase(Locale.ROOT);
    return admn12AdSlotScheduleStore.values().stream()
        .filter(
            item ->
                statusFilter.isBlank()
                    || statusFilter.equals(defaultText(item.getScheduleStatus(), "").toUpperCase(Locale.ROOT)))
        .filter(
            item ->
                slotTypeFilter.isBlank()
                    || slotTypeFilter.equals(defaultText(item.getSlotType(), "").toUpperCase(Locale.ROOT)))
        .filter(
            item ->
                cityFilter.isBlank()
                    || cityFilter.equals(defaultText(item.getCityCode(), "").toUpperCase(Locale.ROOT)))
        .filter(
            item -> {
              if (keywordFilter.isBlank()) {
                return true;
              }
              return defaultText(item.getScheduleId(), "").toLowerCase(Locale.ROOT).contains(keywordFilter)
                  || defaultText(item.getSlotCode(), "").toLowerCase(Locale.ROOT).contains(keywordFilter)
                  || defaultText(item.getSlotName(), "").toLowerCase(Locale.ROOT).contains(keywordFilter)
                  || defaultText(item.getAdvertiserName(), "").toLowerCase(Locale.ROOT).contains(keywordFilter)
                  || defaultText(item.getCampaignName(), "").toLowerCase(Locale.ROOT).contains(keywordFilter)
                  || defaultText(item.getOperator(), "").toLowerCase(Locale.ROOT).contains(keywordFilter);
            })
        .sorted(Comparator.comparing(Admn12AdSlotScheduleEntity::getUpdatedAt).reversed())
        .toList();
  }

  public Admn12AdSlotScheduleEntity getAdSlotScheduleForAdmin(String scheduleId) {
    String normalizedId = defaultText(scheduleId, "");
    if (normalizedId.isBlank()) {
      throw new BaseException(ErrorCode.BAD_REQUEST.getCode(), "scheduleId 不能为空");
    }
    Admn12AdSlotScheduleEntity entity = admn12AdSlotScheduleStore.get(normalizedId);
    if (entity == null) {
      throw new BaseException(ErrorCode.NOT_FOUND.getCode(), "排期记录不存在");
    }
    return entity;
  }

  public Admn12AdSlotScheduleEntity upsertAdSlotScheduleForAdmin(
      String slotCode,
      String slotName,
      String slotType,
      String cityCode,
      String cityName,
      String scheduleStatus,
      String scheduleFillStatus,
      String startDate,
      String endDate,
      String totalSlots,
      String soldSlots,
      String pricePerDay,
      String creativeUrl,
      String advertiserName,
      String campaignName,
      String operator,
      String remark,
      List<Admn12AdSlotScheduleEntity.ScheduleWindow> windows) {
    String safeSlotCode = defaultText(slotCode, "").toUpperCase(Locale.ROOT);
    String safeSlotName = defaultText(slotName, "");
    String safeSlotType = defaultText(slotType, "").toUpperCase(Locale.ROOT);
    String safeCityCode = defaultText(cityCode, "").toUpperCase(Locale.ROOT);
    if (safeSlotCode.isBlank() || safeSlotName.isBlank() || safeSlotType.isBlank() || safeCityCode.isBlank()) {
      throw new BaseException(ErrorCode.BAD_REQUEST.getCode(), "slotCode/slotName/slotType/cityCode 不能为空");
    }
    LocalDateTime now = LocalDateTime.now();
    Admn12AdSlotScheduleEntity existing =
        admn12AdSlotScheduleStore.values().stream()
            .filter(
                item ->
                    safeSlotCode.equalsIgnoreCase(defaultText(item.getSlotCode(), ""))
                        && safeCityCode.equalsIgnoreCase(defaultText(item.getCityCode(), "")))
            .findFirst()
            .orElse(null);
    if (existing == null) {
      String scheduleId =
          "SCH"
              + UUID.randomUUID().toString().replace("-", "").substring(0, 8).toUpperCase(Locale.ROOT);
      Admn12AdSlotScheduleEntity created =
          new Admn12AdSlotScheduleEntity(
              scheduleId,
              safeSlotCode,
              safeSlotName,
              safeSlotType,
              safeCityCode,
              defaultText(cityName, admn12CityText(safeCityCode)),
              defaultText(scheduleStatus, "ACTIVE").toUpperCase(Locale.ROOT),
              defaultText(scheduleFillStatus, "ON_SALE").toUpperCase(Locale.ROOT),
              defaultText(startDate, LocalDate.now().toString()),
              defaultText(endDate, LocalDate.now().plusDays(30).toString()),
              defaultText(totalSlots, "1"),
              defaultText(soldSlots, "0"),
              defaultText(pricePerDay, "¥0/天"),
              defaultText(creativeUrl, ""),
              defaultText(advertiserName, ""),
              defaultText(campaignName, ""),
              defaultText(operator, "admn12-create"),
              defaultText(remark, ""),
              now,
              now);
      if (windows != null) {
        windows.forEach(
            item ->
                created.appendWindow(
                    defaultText(item.startDate(), ""),
                    defaultText(item.endDate(), ""),
                    defaultText(item.windowStatus(), ""),
                    defaultText(item.windowStatusText(), ""),
                    defaultText(item.bookedBy(), "")));
      }
      admn12AdSlotScheduleStore.put(created.getScheduleId(), created);
      return created;
    }
    existing.update(
        safeSlotName,
        safeSlotType,
        safeCityCode,
        defaultText(cityName, existing.getCityName()),
        defaultText(scheduleStatus, existing.getScheduleStatus()).toUpperCase(Locale.ROOT),
        defaultText(scheduleFillStatus, existing.getScheduleFillStatus()).toUpperCase(Locale.ROOT),
        defaultText(startDate, existing.getStartDate()),
        defaultText(endDate, existing.getEndDate()),
        defaultText(totalSlots, existing.getTotalSlots()),
        defaultText(soldSlots, existing.getSoldSlots()),
        defaultText(pricePerDay, existing.getPricePerDay()),
        defaultText(creativeUrl, existing.getCreativeUrl()),
        defaultText(advertiserName, existing.getAdvertiserName()),
        defaultText(campaignName, existing.getCampaignName()),
        defaultText(operator, "admn12-update"),
        defaultText(remark, existing.getRemark()),
        windows,
        now);
    admn12AdSlotScheduleStore.put(existing.getScheduleId(), existing);
    return existing;
  }

  public String admn12SlotTypeText(String slotType) {
    return switch (defaultText(slotType, "").toUpperCase(Locale.ROOT)) {
      case "HOME_TOP_BANNER" -> "首页焦点大图位";
      case "CITY_RECOMMEND_CAROUSEL" -> "城市推荐轮播位";
      case "LOGISTICS_SERVICE_TILE" -> "仓配服务推荐位";
      case "NEWS_FEED_INSERT" -> "资讯流插播位";
      default -> "其他广告位";
    };
  }

  public String admn12ScheduleStatusText(String scheduleStatus) {
    return switch (defaultText(scheduleStatus, "").toUpperCase(Locale.ROOT)) {
      case "ACTIVE" -> "投放中";
      case "PAUSED" -> "暂停";
      case "EXPIRED" -> "已到期";
      default -> "未知状态";
    };
  }

  public String admn12CityText(String cityCode) {
    return switch (defaultText(cityCode, "").toUpperCase(Locale.ROOT)) {
      case "NORTH_CHINA" -> "华北";
      case "EAST_CHINA" -> "华东";
      case "SOUTH_CHINA" -> "华南";
      case "CENTRAL_CHINA" -> "华中";
      case "WEST_CHINA" -> "西南";
      default -> "其他区域";
    };
  }

  private void seedAdmn13CreditModelVersions() {
    LocalDateTime now = LocalDateTime.now();
    Admn13CreditModelVersionEntity first =
        new Admn13CreditModelVersionEntity(
            "CMV000001",
            "CREDIT_MODEL_CORE",
            "钢贸核心信用模型",
            "v2026.04",
            "ACTIVE",
            "MERCHANT",
            "0-100",
            "{\"low\":60,\"medium\":75,\"high\":88}",
            "2026-04-01",
            "",
            "12540",
            "89.4%",
            "0.421",
            "0.913",
            "风控策略组",
            "覆盖履约、争议、支付与数据完整性四大维度",
            "admn13-seed",
            now.minusDays(20),
            now.minusDays(1),
            List.of(
                new Admn13CreditModelVersionEntity.FactorWeight("FULFILL", "履约稳定性", "35", "POSITIVE"),
                new Admn13CreditModelVersionEntity.FactorWeight("DISPUTE", "争议率", "25", "NEGATIVE"),
                new Admn13CreditModelVersionEntity.FactorWeight("PAYMENT", "回款及时率", "25", "POSITIVE"),
                new Admn13CreditModelVersionEntity.FactorWeight("DATA", "数据完整性", "15", "POSITIVE")));
    admn13CreditModelVersionStore.put(first.getVersionId(), first);

    Admn13CreditModelVersionEntity second =
        new Admn13CreditModelVersionEntity(
            "CMV000002",
            "CREDIT_MODEL_CORE",
            "钢贸核心信用模型",
            "v2026.05-beta",
            "DRAFT",
            "MERCHANT",
            "0-100",
            "{\"low\":62,\"medium\":78,\"high\":90}",
            "2026-05-01",
            "2026-06-30",
            "8420",
            "87.8%",
            "0.398",
            "0.901",
            "模型实验组",
            "提高履约指标权重并引入波动惩罚项",
            "admn13-seed",
            now.minusDays(12),
            now.minusHours(10),
            List.of(
                new Admn13CreditModelVersionEntity.FactorWeight("FULFILL", "履约稳定性", "40", "POSITIVE"),
                new Admn13CreditModelVersionEntity.FactorWeight("DISPUTE", "争议率", "22", "NEGATIVE"),
                new Admn13CreditModelVersionEntity.FactorWeight("PAYMENT", "回款及时率", "23", "POSITIVE"),
                new Admn13CreditModelVersionEntity.FactorWeight("DATA", "数据完整性", "15", "POSITIVE")));
    admn13CreditModelVersionStore.put(second.getVersionId(), second);

    Admn13CreditModelVersionEntity third =
        new Admn13CreditModelVersionEntity(
            "CMV000003",
            "CREDIT_MODEL_LIGHT",
            "轻量信用预估模型",
            "v2026.03",
            "ARCHIVED",
            "LEAD",
            "0-100",
            "{\"low\":55,\"medium\":70,\"high\":85}",
            "2026-03-01",
            "2026-03-31",
            "5600",
            "84.2%",
            "0.355",
            "0.872",
            "数据科学组",
            "历史版本，仅用于线索分发回溯分析",
            "admn13-seed",
            now.minusDays(45),
            now.minusDays(28),
            List.of(
                new Admn13CreditModelVersionEntity.FactorWeight("RESPONSE", "响应时效", "30", "POSITIVE"),
                new Admn13CreditModelVersionEntity.FactorWeight("QUOTE", "报价有效率", "30", "POSITIVE"),
                new Admn13CreditModelVersionEntity.FactorWeight("DISPUTE", "争议率", "20", "NEGATIVE"),
                new Admn13CreditModelVersionEntity.FactorWeight("DATA", "数据完整性", "20", "POSITIVE")));
    admn13CreditModelVersionStore.put(third.getVersionId(), third);
  }

  public List<Admn13CreditModelVersionEntity> listCreditModelVersionsForAdmin(
      String versionStatus, String scenarioCode, String riskLevel, String keyword) {
    String statusFilter = defaultText(versionStatus, "").toUpperCase(Locale.ROOT);
    String scenarioFilter = defaultText(scenarioCode, "").toUpperCase(Locale.ROOT);
    String riskFilter = defaultText(riskLevel, "").toUpperCase(Locale.ROOT);
    String keywordFilter = defaultText(keyword, "").toLowerCase(Locale.ROOT);
    return admn13CreditModelVersionStore.values().stream()
        .filter(
            item ->
                statusFilter.isBlank()
                    || statusFilter.equals(defaultText(item.getVersionStatus(), "").toUpperCase(Locale.ROOT)))
        .filter(
            item ->
                scenarioFilter.isBlank()
                    || scenarioFilter.equals(defaultText(item.getScenarioCode(), "").toUpperCase(Locale.ROOT)))
        .filter(
            item ->
                riskFilter.isBlank()
                    || riskFilter.equals(admn13RiskLevelText(item.getRiskThresholdJson()).toUpperCase(Locale.ROOT)))
        .filter(
            item -> {
              if (keywordFilter.isBlank()) {
                return true;
              }
              return defaultText(item.getVersionId(), "").toLowerCase(Locale.ROOT).contains(keywordFilter)
                  || defaultText(item.getModelCode(), "").toLowerCase(Locale.ROOT).contains(keywordFilter)
                  || defaultText(item.getModelName(), "").toLowerCase(Locale.ROOT).contains(keywordFilter)
                  || defaultText(item.getModelVersion(), "").toLowerCase(Locale.ROOT).contains(keywordFilter)
                  || defaultText(item.getOwner(), "").toLowerCase(Locale.ROOT).contains(keywordFilter)
                  || defaultText(item.getRemark(), "").toLowerCase(Locale.ROOT).contains(keywordFilter);
            })
        .sorted(Comparator.comparing(Admn13CreditModelVersionEntity::getUpdatedAt).reversed())
        .toList();
  }

  public Admn13CreditModelVersionEntity getCreditModelVersionForAdmin(String versionId) {
    String normalized = defaultText(versionId, "");
    if (normalized.isBlank()) {
      throw new BaseException(ErrorCode.BAD_REQUEST.getCode(), "versionId 不能为空");
    }
    Admn13CreditModelVersionEntity entity = admn13CreditModelVersionStore.get(normalized);
    if (entity == null) {
      throw new BaseException(ErrorCode.NOT_FOUND.getCode(), "信用模型版本不存在");
    }
    return entity;
  }

  public Admn13CreditModelVersionEntity upsertCreditModelVersionForAdmin(
      String modelCode,
      String modelName,
      String versionNo,
      String versionStatus,
      String applicableScope,
      String effectiveFrom,
      String effectiveTo,
      String baseScore,
      String passThreshold,
      String riskThreshold,
      List<Admn13CreditModelVersionEntity.FactorWeight> factors,
      String remark,
      String operator) {
    String safeModelCode = defaultText(modelCode, "").toUpperCase(Locale.ROOT);
    String safeModelName = defaultText(modelName, "");
    String safeVersionNo = defaultText(versionNo, "");
    if (safeModelCode.isBlank() || safeModelName.isBlank() || safeVersionNo.isBlank()) {
      throw new BaseException(ErrorCode.BAD_REQUEST.getCode(), "modelCode/modelName/versionNo 不能为空");
    }
    LocalDateTime now = LocalDateTime.now();
    Admn13CreditModelVersionEntity existing =
        admn13CreditModelVersionStore.values().stream()
            .filter(
                item ->
                    safeModelCode.equalsIgnoreCase(defaultText(item.getModelCode(), ""))
                        && safeVersionNo.equalsIgnoreCase(defaultText(item.getModelVersion(), "")))
            .findFirst()
            .orElse(null);
    String thresholdJson =
        "{\"base\":"
            + defaultText(baseScore, "0")
            + ",\"pass\":"
            + defaultText(passThreshold, "0")
            + ",\"risk\":"
            + defaultText(riskThreshold, "0")
            + "}";
    List<Admn13CreditModelVersionEntity.FactorWeight> safeFactors =
        factors == null ? List.of() : factors;

    if (existing == null) {
      String versionId =
          "CMV"
              + UUID.randomUUID().toString().replace("-", "").substring(0, 8).toUpperCase(Locale.ROOT);
      Admn13CreditModelVersionEntity created =
          new Admn13CreditModelVersionEntity(
              versionId,
              safeModelCode,
              safeModelName,
              safeVersionNo,
              defaultText(versionStatus, "DRAFT").toUpperCase(Locale.ROOT),
              defaultText(applicableScope, "MERCHANT").toUpperCase(Locale.ROOT),
              "0-100",
              thresholdJson,
              defaultText(effectiveFrom, LocalDate.now().toString()),
              defaultText(effectiveTo, ""),
              "0",
              "0%",
              "0.000",
              "0.000",
              defaultText(operator, "admn13-upsert"),
              defaultText(remark, ""),
              defaultText(operator, "admn13-upsert"),
              now,
              now,
              safeFactors);
      admn13CreditModelVersionStore.put(created.getVersionId(), created);
      return created;
    }

    existing.update(
        safeModelName,
        defaultText(versionStatus, existing.getVersionStatus()).toUpperCase(Locale.ROOT),
        defaultText(applicableScope, existing.getScenarioCode()).toUpperCase(Locale.ROOT),
        existing.getScoreScale(),
        thresholdJson,
        defaultText(effectiveFrom, existing.getEffectiveFrom()),
        defaultText(effectiveTo, existing.getEffectiveTo()),
        existing.getSampleSize(),
        existing.getHitRate(),
        existing.getKsValue(),
        existing.getAucValue(),
        defaultText(operator, existing.getOwner()),
        defaultText(remark, existing.getRemark()),
        defaultText(operator, existing.getOperator()),
        safeFactors,
        now);
    admn13CreditModelVersionStore.put(existing.getVersionId(), existing);
    return existing;
  }

  public String admn13ModelStatusText(String versionStatus) {
    return switch (defaultText(versionStatus, "").toUpperCase(Locale.ROOT)) {
      case "ACTIVE" -> "生效中";
      case "DRAFT" -> "草稿";
      case "ARCHIVED" -> "已归档";
      default -> "未知";
    };
  }

  public String admn13RiskLevelText(String riskThresholdJson) {
    String text = defaultText(riskThresholdJson, "");
    if (text.contains("\"risk\":90") || text.contains("\"risk\":89")) {
      return "HIGH";
    }
    if (text.contains("\"risk\":80") || text.contains("\"risk\":85") || text.contains("\"risk\":88")) {
      return "MEDIUM";
    }
    return "LOW";
  }

  public String admn13AuditRiskLevel(String versionStatus, String scenarioCode) {
    String status = defaultText(versionStatus, "").toUpperCase(Locale.ROOT);
    String scope = defaultText(scenarioCode, "").toUpperCase(Locale.ROOT);
    if ("ACTIVE".equals(status) && "MERCHANT".equals(scope)) {
      return "HIGH";
    }
    if ("ACTIVE".equals(status) || "DRAFT".equals(status)) {
      return "MEDIUM";
    }
    return "LOW";
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

  private String defaultArbitrationConclusionByAction(String action) {
    return switch (defaultText(action, "").toUpperCase(Locale.ROOT)) {
      case "SUPPORT_BUYER" -> "支持买方诉求并执行补偿方案";
      case "SUPPORT_SUPPLIER" -> "支持卖方诉求并关闭异议";
      case "MEDIATION" -> "平台调解达成一致方案";
      case "CLOSE_NO_FAULT" -> "证据不足，工单归档";
      case "REOPEN" -> "根据补充证据重新仲裁";
      default -> "仲裁处理中";
    };
  }

  private String normalizeMoney(String amountText) {
    return parseNonNegativeMoney(amountText, "amount").stripTrailingZeros().toPlainString();
  }

  private BigDecimal parseNonNegativeMoney(String amountText, String fieldName) {
    String normalized = defaultText(amountText, "");
    if (normalized.isBlank()) {
      throw new BaseException(ErrorCode.BAD_REQUEST.getCode(), fieldName + " 不能为空");
    }
    try {
      BigDecimal value = new BigDecimal(normalized);
      if (value.compareTo(BigDecimal.ZERO) < 0) {
        throw new BaseException(ErrorCode.BAD_REQUEST.getCode(), fieldName + " 不能小于0");
      }
      return value;
    } catch (NumberFormatException ex) {
      throw new BaseException(ErrorCode.BAD_REQUEST.getCode(), fieldName + " 格式错误");
    }
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

  public String admn11RefundStatusText(String refundStatus) {
    return switch (defaultText(refundStatus, "").toUpperCase(Locale.ROOT)) {
      case "PENDING_REVIEW" -> "待审核";
      case "APPROVED" -> "退款通过";
      case "REJECTED" -> "退款驳回";
      case "REFUNDED" -> "已退款";
      default -> "处理中";
    };
  }

  public String admn11RefundReasonText(String refundReasonCode) {
    return switch (defaultText(refundReasonCode, "").toUpperCase(Locale.ROOT)) {
      case "DOUBLE_PAYMENT" -> "重复支付";
      case "ORDER_CANCELLED" -> "订单取消";
      case "QUALITY_DISPUTE" -> "质量争议";
      case "OTHER" -> "其他原因";
      default -> "其他原因";
    };
  }

  public List<Admn11PaymentRefundEntity> listPaymentRefundsForAdmin(
      String refundStatus,
      String refundReasonCode,
      String payChannel,
      String keyword) {
    String statusFilter = defaultText(refundStatus, "").toUpperCase(Locale.ROOT);
    String reasonFilter = defaultText(refundReasonCode, "").toUpperCase(Locale.ROOT);
    String channelFilter = defaultText(payChannel, "").toUpperCase(Locale.ROOT);
    String keywordFilter = defaultText(keyword, "").toLowerCase(Locale.ROOT);
    return admn11PaymentRefundStore.values().stream()
        .filter(
            item ->
                statusFilter.isBlank()
                    || statusFilter.equals(defaultText(item.getRefundStatus(), "").toUpperCase(Locale.ROOT)))
        .filter(
            item ->
                reasonFilter.isBlank()
                    || reasonFilter.equals(defaultText(item.getRefundReasonCode(), "").toUpperCase(Locale.ROOT)))
        .filter(
            item ->
                channelFilter.isBlank()
                    || channelFilter.equals(defaultText(item.getPayChannel(), "").toUpperCase(Locale.ROOT)))
        .filter(
            item -> {
              if (keywordFilter.isBlank()) {
                return true;
              }
              return defaultText(item.getRefundId(), "").toLowerCase(Locale.ROOT).contains(keywordFilter)
                  || defaultText(item.getCashierId(), "").toLowerCase(Locale.ROOT).contains(keywordFilter)
                  || defaultText(item.getOrderNo(), "").toLowerCase(Locale.ROOT).contains(keywordFilter)
                  || defaultText(item.getInquiryNo(), "").toLowerCase(Locale.ROOT).contains(keywordFilter)
                  || defaultText(item.getBuyerCompany(), "").toLowerCase(Locale.ROOT).contains(keywordFilter)
                  || defaultText(item.getSupplierName(), "").toLowerCase(Locale.ROOT).contains(keywordFilter)
                  || defaultText(item.getRefundReasonCode(), "").toLowerCase(Locale.ROOT).contains(keywordFilter)
                  || defaultText(item.getLatestRemark(), "").toLowerCase(Locale.ROOT).contains(keywordFilter);
            })
        .sorted(Comparator.comparing(Admn11PaymentRefundEntity::getUpdatedAt).reversed())
        .toList();
  }

  public Admn11PaymentRefundEntity getPaymentRefundForAdmin(String refundId) {
    String normalized = defaultText(refundId, "");
    if (normalized.isBlank()) {
      throw new BaseException(ErrorCode.BAD_REQUEST.getCode(), "refundId 不能为空");
    }
    Admn11PaymentRefundEntity entity = admn11PaymentRefundStore.get(normalized);
    if (entity == null) {
      throw new BaseException(ErrorCode.NOT_FOUND.getCode(), "退款工单不存在");
    }
    return entity;
  }

  public Admn11PaymentRefundEntity reviewPaymentRefundForAdmin(
      String refundId,
      String action,
      String reviewRemark,
      String rejectReason,
      String operator) {
    Admn11PaymentRefundEntity entity = getPaymentRefundForAdmin(refundId);
    String normalizedAction = defaultText(action, "").toUpperCase(Locale.ROOT);
    if (!normalizedAction.matches("APPROVE|REJECT|CONFIRM_REFUND")) {
      throw new BaseException(
          ErrorCode.BAD_REQUEST.getCode(), "action 仅支持 APPROVE/REJECT/CONFIRM_REFUND");
    }
    String safeOperator = defaultText(operator, "admn11-review");
    String safeRemark = defaultText(reviewRemark, "退款工单处理");
    String safeRejectReason = defaultText(rejectReason, "");

    String nextStatus = entity.getRefundStatus();
    if ("APPROVE".equals(normalizedAction)) {
      nextStatus = "APPROVED";
    } else if ("REJECT".equals(normalizedAction)) {
      nextStatus = "REJECTED";
    } else if ("CONFIRM_REFUND".equals(normalizedAction)) {
      nextStatus = "REFUNDED";
    }

    LocalDateTime now = LocalDateTime.now();
    String reviewedAt = now.toString();
    String refundedAt = "REFUNDED".equals(nextStatus) ? now.toString() : entity.getRefundedAt();
    String approvedAmount =
        "REJECTED".equals(nextStatus)
            ? "0"
            : defaultText(entity.getApprovedAmountYuan(), entity.getRefundAmountYuan());
    entity.review(
        nextStatus,
        approvedAmount,
        "REJECTED".equals(nextStatus) ? safeRejectReason : "",
        safeRemark,
        safeOperator,
        "REFUNDED".equals(nextStatus) ? safeOperator : entity.getFinanceOperator(),
        reviewedAt,
        refundedAt,
        now);
    admn11PaymentRefundStore.put(entity.getRefundId(), entity);

    if ("REFUNDED".equals(nextStatus)) {
      N10CashierOrderEntity cashier = cashierStore.get(entity.getCashierId());
      if (cashier != null) {
        cashier.markPaid(
            cashier.getPayChannel(),
            cashier.getPayChannelText(),
            "0",
            cashier.getAmountPayable(),
            "ADM-N11退款确认：" + safeRemark,
            now,
            safeOperator);
      }
      N06OrderEntity order = orderStore.get(entity.getOrderId());
      if (order != null) {
        order.updateStatus("RECONCILING", safeOperator, now, "ADM-N11退款确认：" + safeRemark);
      }
    }
    return entity;
  }

  public String admn11AuditRiskLevel(String refundStatus, String refundAmountYuan) {
    String status = defaultText(refundStatus, "").toUpperCase(Locale.ROOT);
    if ("REJECTED".equals(status)) {
      return "LOW";
    }
    BigDecimal amount = parseNonNegativeMoney(defaultText(refundAmountYuan, "0"), "refundAmountYuan");
    if (amount.compareTo(new BigDecimal("100000")) >= 0) {
      return "HIGH";
    }
    if (amount.compareTo(new BigDecimal("30000")) >= 0) {
      return "MEDIUM";
    }
    return "LOW";
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
