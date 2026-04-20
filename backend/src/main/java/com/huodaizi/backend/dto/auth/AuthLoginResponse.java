package com.huodaizi.backend.dto.auth;

public record AuthLoginResponse(
    String userId,
    String account,
    String displayName,
    String role,
    String token,
    String expireAt,
    String loginAt) {}
