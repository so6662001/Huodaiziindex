package com.huodaizi.backend.dto.siteaddetail;

import java.util.List;

public record SiteAdDetailOverviewResponse(
    SiteAdDetailPlacementDTO placement,
    List<SiteAdDetailBenefitDTO> benefits,
    List<SiteAdDetailFaqDTO> faq) {}
