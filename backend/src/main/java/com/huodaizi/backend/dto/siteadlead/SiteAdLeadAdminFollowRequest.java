package com.huodaizi.backend.dto.siteadlead;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record SiteAdLeadAdminFollowRequest(
    @NotBlank(message = "content 不能为空")
    @Size(max = 1000, message = "content 最大长度1000")
    String content,
    @Size(max = 120, message = "nextAction 最大长度120")
    String nextAction,
    @Size(max = 120, message = "operator 最大长度120")
    String operator) {}
