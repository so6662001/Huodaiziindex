package com.huodaizi.backend.exception;

import com.huodaizi.backend.common.ApiResponse;
import com.huodaizi.backend.common.BaseException;
import com.huodaizi.backend.common.ErrorCode;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(BaseException.class)
  public ResponseEntity<ApiResponse<Void>> handleBase(BaseException ex) {
    HttpStatus status = toHttpStatus(ex.getCode());
    return ResponseEntity.status(status).body(ApiResponse.fail(ex.getCode(), ex.getMessage()));
  }

  @ExceptionHandler({MethodArgumentNotValidException.class, ConstraintViolationException.class,
      HttpMessageNotReadableException.class, IllegalArgumentException.class})
  public ResponseEntity<ApiResponse<Void>> handleBadRequest(Exception ex) {
    return ResponseEntity
        .status(HttpStatus.BAD_REQUEST)
        .body(ApiResponse.fail(ErrorCode.BAD_REQUEST, ErrorCode.BAD_REQUEST.getMessage()));
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ApiResponse<Void>> handleAny(Exception ex) {
    return ResponseEntity
        .status(HttpStatus.INTERNAL_SERVER_ERROR)
        .body(ApiResponse.fail(ErrorCode.INTERNAL_ERROR, ErrorCode.INTERNAL_ERROR.getMessage()));
  }

  private HttpStatus toHttpStatus(String code) {
    if (ErrorCode.UNAUTHORIZED.getCode().equals(code)) {
      return HttpStatus.UNAUTHORIZED;
    }
    if (ErrorCode.BAD_REQUEST.getCode().equals(code)) {
      return HttpStatus.BAD_REQUEST;
    }
    if (ErrorCode.NOT_FOUND.getCode().equals(code)) {
      return HttpStatus.NOT_FOUND;
    }
    if (ErrorCode.AUTH_CONFIG_ERROR.getCode().equals(code)) {
      return HttpStatus.INTERNAL_SERVER_ERROR;
    }
    return HttpStatus.INTERNAL_SERVER_ERROR;
  }
}
