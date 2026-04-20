package com.huodaizi.backend.dto.auth;

public record H5N01QuickLoginResponse(
    String userId,
    String account,
    String mobileMasked,
    String displayName,
    String role,
    String token,
    String tokenExpireAt,
    String loginAt,
    String channel) {}
