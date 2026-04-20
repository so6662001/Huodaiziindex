package com.huodaizi.backend.dto.siteadlead;

import java.util.List;

public record SiteAdLeadListResponse(List<SiteAdLeadItemDTO> items, int total, int page, int pageSize) {}
