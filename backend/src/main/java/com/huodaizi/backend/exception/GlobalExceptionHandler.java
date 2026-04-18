package com.huodaizi.backend.exception;

import com.huodaizi.backend.common.ApiResponse;
import com.huodaizi.backend.common.BaseException;
import com.huodaizi.backend.common.ErrorCode;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(BaseException.class)
  public ApiResponse<Void> handleBase(BaseException ex) {
    return ApiResponse.fail(ex.getCode(), ex.getMessage());
  }

  @ExceptionHandler({
    MethodArgumentNotValidException.class,
    ConstraintViolationException.class,
    HttpMessageNotReadableException.class,
    IllegalArgumentException.class
  })
  public ApiResponse<Void> handleBadRequest(Exception ex) {
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
    return ApiResponse.fail(ErrorCode.BAD_REQUEST, msg);
  }

  @ExceptionHandler(Exception.class)
  public ApiResponse<Void> handleAny(Exception ex) {
    return ApiResponse.fail(ErrorCode.INTERNAL_ERROR, "服务器内部错误: " + ex.getMessage());
  }
}
