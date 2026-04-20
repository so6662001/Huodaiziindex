package com.huodaizi.backend.dto.auth;

public record H5N01SendLoginCodeResponse(
    String mobile, String maskedMobile, String codeToken, String expireAt, String tipText) {}
