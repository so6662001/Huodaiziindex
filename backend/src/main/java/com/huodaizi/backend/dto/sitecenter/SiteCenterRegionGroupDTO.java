package com.huodaizi.backend.dto.sitecenter;

import java.util.List;

public record SiteCenterRegionGroupDTO(String region, String desc, List<SiteCenterCityItemDTO> cities) {}
