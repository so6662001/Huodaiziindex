package com.huodaizi.backend.common;

public enum ErrorCode {
  BAD_REQUEST("BAD_REQUEST", "请求参数错误"),
  UNAUTHORIZED("UNAUTHORIZED", "未授权访问"),
  NOT_FOUND("NOT_FOUND", "资源不存在"),
  INTERNAL_ERROR("INTERNAL_ERROR", "服务器内部错误");

  private final String code;
  private final String message;

  ErrorCode(String code, String message) {
    this.code = code;
    this.message = message;
  }

  public String getCode() {
    return code;
  }

  public String getMessage() {
    return message;
  }
}
