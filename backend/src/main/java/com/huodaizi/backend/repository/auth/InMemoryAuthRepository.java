package com.huodaizi.backend.repository.auth;

import com.huodaizi.backend.common.BaseException;
import com.huodaizi.backend.common.ErrorCode;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import org.springframework.stereotype.Repository;

@Repository
public class InMemoryAuthRepository {
  private final ConcurrentMap<String, AuthUserEntity> userStore = new ConcurrentHashMap<>();
  private final ConcurrentMap<String, SessionEntity> sessionStore = new ConcurrentHashMap<>();

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
            "MERCHANT",
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
            "MERCHANT",
            "ACTIVE",
            LocalDateTime.now().minusDays(3),
            LocalDateTime.now().minusDays(1));
    userStore.put(seed.getAccount(), seed);
  }

  public static final class SessionEntity {
    private final String token;
    private final String userId;
    private final String account;
    private final String channel;
    private final LocalDateTime expireAt;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;

    public SessionEntity(
        String token,
        String userId,
        String account,
        String channel,
        LocalDateTime expireAt,
        LocalDateTime createdAt,
        LocalDateTime updatedAt) {
      this.token = token;
      this.userId = userId;
      this.account = account;
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
