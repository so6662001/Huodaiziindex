package com.huodaizi.backend.config;

import com.huodaizi.backend.common.BaseException;
import com.huodaizi.backend.common.ErrorCode;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class AdminAuthInterceptor implements HandlerInterceptor {

  private static final String HEADER_NAME = "X-Admin-Token";
  private final AdminAuthProperties properties;

  public AdminAuthInterceptor(AdminAuthProperties properties) {
    this.properties = properties;
  }

  @Override
  public boolean preHandle(
      HttpServletRequest request, HttpServletResponse response, Object handler) {
    if (!properties.isEnabled()) {
      return true;
    }
    String configuredToken = properties.getToken();
    if (configuredToken == null || configuredToken.isBlank()) {
      throw new BaseException(ErrorCode.AUTH_CONFIG_ERROR.getCode(), "中台鉴权未配置");
    }
    String requestToken = request.getHeader(HEADER_NAME);
    if (requestToken == null || !configuredToken.equals(requestToken)) {
      throw new BaseException(ErrorCode.UNAUTHORIZED.getCode(), "中台访问未授权");
    }
    return true;
  }
}
