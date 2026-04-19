package com.huodaizi.backend.dto.freightdetail;

import java.util.List;

public record FreightDetailMainDTO(
    String id,
    String provider,
    String route,
    String vehicle,
    String loadRange,
    String frequency,
    String timeliness,
    String quote,
    String desc,
    List<String> serviceTags,
    String status,
    boolean pinned,
    String updatedAt) {}
