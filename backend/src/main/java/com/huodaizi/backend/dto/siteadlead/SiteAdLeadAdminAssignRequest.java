package com.huodaizi.backend.dto.siteadlead;

import jakarta.validation.constraints.Size;

public record SiteAdLeadAdminAssignRequest(
    @Size(max = 120, message = "ownerName 最大长度120") String ownerName,
    @Size(max = 120, message = "team 最大长度120") String team,
    @Size(max = 1000, message = "comment 最大长度1000") String comment) {}
