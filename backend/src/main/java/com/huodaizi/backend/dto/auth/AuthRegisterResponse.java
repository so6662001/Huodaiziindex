package com.huodaizi.backend.dto.auth;

public record AuthRegisterResponse(
    String userId,
    String account,
    String mobileMasked,
    String merchantId,
    String role,
    String status,
    String token,
    String tokenExpireAt,
    String message) {}
