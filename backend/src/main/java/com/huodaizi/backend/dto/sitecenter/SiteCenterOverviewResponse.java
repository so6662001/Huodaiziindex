package com.huodaizi.backend.dto.sitecenter;

import java.util.List;

public record SiteCenterOverviewResponse(
    List<SiteCenterStatDTO> stats,
    List<SiteCenterRegionGroupDTO> regionGroups,
    List<SiteCenterAdProductDTO> adProducts) {}
