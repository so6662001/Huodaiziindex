package com.huodaizi.backend.dto.storagedemand;

import java.util.List;

public record StorageDemandFilterOptions(
    List<String> cityOptions, List<String> goodsCategoryOptions, List<String> serviceNeedOptions) {}
