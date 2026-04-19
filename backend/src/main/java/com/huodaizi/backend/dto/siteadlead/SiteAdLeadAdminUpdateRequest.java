package com.huodaizi.backend.dto.siteadlead;

import jakarta.validation.constraints.Size;

public record SiteAdLeadAdminUpdateRequest(
    @Size(max = 32, message = "status 最大长度32") String status,
    @Size(max = 120, message = "operatorName 最大长度120") String operatorName,
    @Size(max = 1000, message = "comment 最大长度1000") String comment) {}
