package com.huodaizi.backend.dto.news;

import java.util.List;

public record NewsOverviewResponse(
    List<String> categoryTabs,
    List<NewsItemDTO> items,
    List<String> hotItems,
    NewsItemDTO ad,
    Integer total,
    Integer page,
    Integer pageSize) {}
