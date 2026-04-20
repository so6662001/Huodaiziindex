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
}
