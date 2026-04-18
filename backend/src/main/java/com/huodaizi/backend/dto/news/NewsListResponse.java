package com.huodaizi.backend.dto.news;

import java.util.List;

public record NewsListResponse(
    List<NewsItemDTO> items,
    List<String> hotItems,
    Integer total,
    Integer page,
    Integer pageSize) {}
