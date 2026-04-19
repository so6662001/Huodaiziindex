package com.huodaizi.backend.service.newsdetail;

import com.huodaizi.backend.common.BaseException;
import com.huodaizi.backend.common.ErrorCode;
import com.huodaizi.backend.dto.newsdetail.NewsDetailAdminCreateRequest;
import com.huodaizi.backend.dto.newsdetail.NewsDetailAdminItemDTO;
import com.huodaizi.backend.dto.newsdetail.NewsDetailAdminUpdateRequest;
import com.huodaizi.backend.dto.newsdetail.NewsDetailArticleDTO;
import com.huodaizi.backend.dto.newsdetail.NewsDetailRelatedItemDTO;
import com.huodaizi.backend.dto.newsdetail.NewsDetailRequest;
import com.huodaizi.backend.dto.newsdetail.NewsDetailResponse;
import com.huodaizi.backend.dto.newsdetail.NewsDetailSectionType;
import com.huodaizi.backend.repository.newsdetail.InMemoryNewsDetailRepository;
import com.huodaizi.backend.repository.newsdetail.NewsDetailEntity;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class NewsDetailService {

  private static final String DEFAULT_NEWS_ID = "N20260418001";

  private final InMemoryNewsDetailRepository repository;

  public NewsDetailService(InMemoryNewsDetailRepository repository) {
    this.repository = repository;
  }

  public NewsDetailResponse detail(NewsDetailRequest request) {
    String newsId = sanitizeNewsId(request.id());
    NewsDetailEntity articleEntity = repository.onlineByNewsIdAndSection(newsId, NewsDetailSectionType.ARTICLE);
    if (articleEntity == null) {
      throw new BaseException(ErrorCode.NOT_FOUND.getCode(), "资讯详情不存在");
    }

    List<String> content =
        repository.listOnlineByNewsIdAndSection(newsId, NewsDetailSectionType.CONTENT).stream()
            .sorted(Comparator.comparingInt(this::contentOrder))
            .map(NewsDetailEntity::getContent)
            .filter(text -> text != null && !text.isBlank() && !"-".equals(text))
            .toList();
    if (content.isEmpty()) {
      content = parseLines(articleEntity.getContent());
    }

    NewsDetailArticleDTO article = toArticle(articleEntity, content);
    List<NewsDetailRelatedItemDTO> related =
        repository.listOnlineByNewsIdAndSection(newsId, NewsDetailSectionType.RELATED).stream()
            .map(this::toRelated)
            .toList();
    List<String> tips =
        repository.listOnlineByNewsIdAndSection(newsId, NewsDetailSectionType.TIP).stream()
            .map(NewsDetailEntity::getTitle)
            .toList();
    NewsDetailEntity ad = repository.onlineByNewsIdAndSection(newsId, NewsDetailSectionType.AD);
    return new NewsDetailResponse(
        article,
        related,
        tips,
        ad == null ? null : toRelated(ad));
  }

  public List<NewsDetailAdminItemDTO> adminList(String newsId, NewsDetailSectionType section) {
    return repository.adminListByNewsIdAndSection(sanitizeNewsId(newsId), section).stream()
        .map(this::toAdminDto)
        .toList();
  }

  public NewsDetailAdminItemDTO adminCreate(
      String newsId, NewsDetailSectionType section, NewsDetailAdminCreateRequest request) {
    return toAdminDto(repository.adminCreate(sanitizeNewsId(newsId), section, request));
  }

  public NewsDetailAdminItemDTO adminUpdate(
      String newsId, NewsDetailSectionType section, String id, NewsDetailAdminUpdateRequest request) {
    return toAdminDto(repository.adminUpdate(sanitizeNewsId(newsId), section, id, request));
  }

  public NewsDetailAdminItemDTO adminChangeStatus(
      String newsId, NewsDetailSectionType section, String id, String status) {
    return toAdminDto(repository.adminChangeStatus(sanitizeNewsId(newsId), section, id, status));
  }

  public NewsDetailAdminItemDTO adminPin(
      String newsId, NewsDetailSectionType section, String id, Boolean pinned) {
    if (pinned == null) {
      throw new BaseException(ErrorCode.BAD_REQUEST.getCode(), "pinned 不能为空");
    }
    return toAdminDto(repository.adminPin(sanitizeNewsId(newsId), section, id, pinned));
  }

  public void adminDelete(String newsId, NewsDetailSectionType section, String id) {
    repository.adminDelete(sanitizeNewsId(newsId), section, id);
  }

  private NewsDetailArticleDTO toArticle(NewsDetailEntity entity, List<String> content) {
    return new NewsDetailArticleDTO(
        entity.getNewsId(),
        entity.getTitle(),
        entity.getCategory(),
        entity.getCity(),
        entity.getPublishAt(),
        entity.getSource(),
        entity.getSummary(),
        content,
        parseTags(entity.getTags()),
        entity.getStatus(),
        entity.isPinned(),
        entity.getUpdatedAt().toString());
  }

  private NewsDetailRelatedItemDTO toRelated(NewsDetailEntity entity) {
    return new NewsDetailRelatedItemDTO(
        entity.getRelatedNewsId(),
        entity.getTitle(),
        entity.getCategory(),
        entity.getPublishAt(),
        entity.getLink());
  }

  private NewsDetailAdminItemDTO toAdminDto(NewsDetailEntity entity) {
    return new NewsDetailAdminItemDTO(
        entity.getId(),
        entity.getNewsId(),
        entity.getSectionType().name(),
        entity.getTitle(),
        entity.getCategory(),
        entity.getCity(),
        entity.getPublishAt(),
        entity.getSource(),
        entity.getSummary(),
        entity.getTags(),
        entity.getRelatedNewsId(),
        entity.getContent(),
        entity.getLink(),
        entity.getStatus(),
        entity.isPinned(),
        entity.getUpdatedAt().toString());
  }

  private String sanitizeNewsId(String id) {
    if (id == null || id.isBlank()) {
      return DEFAULT_NEWS_ID;
    }
    return id.trim().toUpperCase();
  }

  private List<String> parseTags(String tags) {
    if (tags == null || tags.isBlank()) {
      return List.of();
    }
    return Arrays.stream(tags.split(",")).map(String::trim).filter(s -> !s.isBlank()).toList();
  }

  private List<String> parseLines(String content) {
    if (content == null || content.isBlank()) {
      return List.of();
    }
    return Arrays.stream(content.split("\\n"))
        .map(String::trim)
        .filter(s -> !s.isBlank() && !"-".equals(s))
        .toList();
  }

  private int contentOrder(NewsDetailEntity entity) {
    String order = entity.getPublishAt();
    if (order == null || order.isBlank()) {
      return Integer.MAX_VALUE;
    }
    String digits = order.replaceAll("[^0-9]", "");
    if (digits.isBlank()) {
      return Integer.MAX_VALUE;
    }
    try {
      return Integer.parseInt(digits);
    } catch (NumberFormatException ex) {
      return Integer.MAX_VALUE;
    }
  }
}
