package com.huodaizi.backend.dto.identity;

public record N02IdentityOptionDTO(
    String identityCode,
    String identityName,
    String sceneDesc,
    String redirectUrl,
    boolean selected,
    boolean defaultIdentity,
    boolean enabled) {}
