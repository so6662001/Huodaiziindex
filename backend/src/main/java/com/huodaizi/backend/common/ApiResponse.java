package com.huodaizi.backend.common;

public record ApiResponse<T>(String code, String message, T data) {

  public static <T> ApiResponse<T> success(T data) {
    return new ApiResponse<>("0", "OK", data);
  }

  public static ApiResponse<Void> success() {
    return new ApiResponse<>("0", "OK", null);
  }

  public static <T> ApiResponse<T> fail(String code, String message) {
    return new ApiResponse<>(code, message, null);
  }

  public static <T> ApiResponse<T> fail(ErrorCode errorCode, String message) {
    return new ApiResponse<>(errorCode.getCode(), message, null);
  }
}
