package com.huodaizi.backend.dto.identity;

public record H5N02IdentityOptionDTO(
    String identityCode,
    String identityName,
    String sceneDesc,
    String h5Route,
    boolean selected,
    boolean defaultIdentity,
    boolean enabled) {}
