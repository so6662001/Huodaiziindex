package com.huodaizi.backend.dto.newsdetail;

import java.util.List;

public record NewsDetailResponse(
    NewsDetailArticleDTO article,
    List<NewsDetailRelatedItemDTO> related,
    List<String> tips,
    NewsDetailRelatedItemDTO ad) {}
