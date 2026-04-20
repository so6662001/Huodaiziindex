package com.huodaizi.backend.dto.identity;

import java.util.List;

public record N02IdentityListResponse(
    String userId,
    String account,
    String companyName,
    String defaultIdentityCode,
    String currentIdentityCode,
    String currentIdentityName,
    List<N02IdentityOptionDTO> identities,
    String tokenExpireAt) {}
