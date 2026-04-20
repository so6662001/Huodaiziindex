package com.huodaizi.backend.dto.identity;

public record N02IdentitySwitchResponse(
    String userId,
    String account,
    String defaultRoleCode,
    String activeIdentityCode,
    String activeIdentityName,
    String switchedAt,
    String token,
    String tokenExpireAt,
    String message) {}
