package com.huodaizi.backend.dto.sitead;

import java.util.List;

public record SiteAdOptionsResponse(
    List<String> cityOptions, List<String> placementOptions, List<String> durationOptions) {}
