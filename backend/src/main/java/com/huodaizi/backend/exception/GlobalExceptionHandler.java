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
    HttpStatus status = mapStatus(ex.getCode());
    return ResponseEntity.status(status).body(ApiResponse.fail(ex.getCode(), ex.getMessage()));
  }

  @ExceptionHandler({
    MethodArgumentNotValidException.class,
    ConstraintViolationException.class,
    HttpMessageNotReadableException.class,
    IllegalArgumentException.class
  })
  public ResponseEntity<ApiResponse<Void>> handleBadRequest(Exception ex) {
    String msg;
    if (ex instanceof MethodArgumentNotValidException manve
        && manve.getBindingResult().getFieldError() != null) {
      msg = manve.getBindingResult().getFieldError().getDefaultMessage();
    } else if (ex instanceof ConstraintViolationException cve
        && !cve.getConstraintViolations().isEmpty()) {
      msg = cve.getConstraintViolations().iterator().next().getMessage();
    } else {
      msg = ex.getMessage();
    }
    return ResponseEntity.badRequest().body(ApiResponse.fail(ErrorCode.BAD_REQUEST, msg));
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ApiResponse<Void>> handleAny(Exception ex) {
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
        .body(ApiResponse.fail(ErrorCode.INTERNAL_ERROR, ErrorCode.INTERNAL_ERROR.getMessage()));
  }

  private HttpStatus mapStatus(String code) {
    if (ErrorCode.UNAUTHORIZED.getCode().equals(code)) {
      return HttpStatus.UNAUTHORIZED;
    }
    if (ErrorCode.NOT_FOUND.getCode().equals(code)) {
      return HttpStatus.NOT_FOUND;
    }
    if (ErrorCode.BAD_REQUEST.getCode().equals(code)
        || ErrorCode.AUTH_CONFIG_ERROR.getCode().equals(code)) {
      return HttpStatus.BAD_REQUEST;
    }
    return HttpStatus.INTERNAL_SERVER_ERROR;
  }
}
