package com.huodaizi.backend.dto.identity;

import java.util.List;

public record H5N02IdentityListResponse(
    String userId,
    String account,
    String companyName,
    String activeIdentityCode,
    String activeIdentityName,
    List<H5N02IdentityOptionDTO> identities,
    String channel,
    String tokenExpireAt) {}
