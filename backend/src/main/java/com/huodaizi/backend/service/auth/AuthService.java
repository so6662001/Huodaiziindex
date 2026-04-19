package com.huodaizi.backend.service.auth;

import com.huodaizi.backend.common.BaseException;
import com.huodaizi.backend.common.ErrorCode;
import com.huodaizi.backend.dto.auth.AuthLoginRequest;
import com.huodaizi.backend.dto.auth.AuthLoginResponse;
import com.huodaizi.backend.dto.auth.AuthRegisterRequest;
import com.huodaizi.backend.dto.auth.AuthRegisterResponse;
import com.huodaizi.backend.dto.auth.AuthSessionResponse;
import com.huodaizi.backend.repository.auth.AuthUserEntity;
import com.huodaizi.backend.repository.auth.InMemoryAuthRepository;
import com.huodaizi.backend.repository.auth.InMemoryAuthRepository.AuthRegistration;
import com.huodaizi.backend.repository.auth.InMemoryAuthRepository.SessionEntity;
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
}
