package com.huodaizi.backend.service.auth;

import com.huodaizi.backend.common.BaseException;
import com.huodaizi.backend.common.ErrorCode;
import com.huodaizi.backend.dto.auth.AuthLoginRequest;
import com.huodaizi.backend.dto.auth.AuthLoginResponse;
import com.huodaizi.backend.dto.auth.AuthRegisterRequest;
import com.huodaizi.backend.dto.auth.AuthRegisterResponse;
import com.huodaizi.backend.dto.auth.AuthSessionResponse;
import com.huodaizi.backend.dto.auth.N03EnterpriseCertificationDetailResponse;
import com.huodaizi.backend.dto.auth.N03EnterpriseCertificationSubmitRequest;
import com.huodaizi.backend.repository.auth.AuthUserEntity;
import com.huodaizi.backend.repository.auth.EnterpriseCertificationDraft;
import com.huodaizi.backend.repository.auth.EnterpriseCertificationEntity;
import com.huodaizi.backend.repository.auth.InMemoryAuthRepository;
import com.huodaizi.backend.repository.auth.InMemoryAuthRepository.AuthRegistration;
import com.huodaizi.backend.repository.auth.InMemoryAuthRepository.SessionEntity;
import java.time.LocalDateTime;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
  private final InMemoryAuthRepository repository;

  public AuthService(InMemoryAuthRepository repository) {
    this.repository = repository;
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

  private String safeText(String text) {
    return text == null ? "" : text.trim();
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
}
