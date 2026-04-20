package com.huodaizi.backend.dto.auth;

public record AuthSessionResponse(
    String userId,
    String account,
    String companyName,
    String role,
    String contactName,
    String contactMobile,
    String token,
    String tokenExpireAt) {}
