package com.huodaizi.backend.dto.identity;

public record H5N02IdentitySwitchResponse(
    String userId,
    String account,
    String activeIdentityCode,
    String activeIdentityName,
    String switchedAt,
    String token,
    String tokenExpireAt,
    String nextRoute,
    String message) {}
