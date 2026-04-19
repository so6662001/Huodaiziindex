package com.huodaizi.backend.dto.freightdetail;

import java.util.List;

public record FreightDetailResponse(
    FreightDetailMainDTO main,
    List<FreightDetailRelatedLineDTO> relatedLines,
    List<FreightDetailRelatedDemandDTO> relatedDemands,
    FreightDetailContactDTO contact,
    FreightDetailRelatedLineDTO ad) {}
