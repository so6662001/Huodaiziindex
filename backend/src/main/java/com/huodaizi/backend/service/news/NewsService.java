package com.huodaizi.backend.service.news;

import com.huodaizi.backend.common.BaseException;
import com.huodaizi.backend.common.ErrorCode;
import com.huodaizi.backend.dto.news.NewsAdminCreateRequest;
import com.huodaizi.backend.dto.news.NewsAdminUpdateRequest;
import com.huodaizi.backend.dto.news.NewsItemDTO;
import com.huodaizi.backend.dto.news.NewsListRequest;
import com.huodaizi.backend.dto.news.NewsListResponse;
import com.huodaizi.backend.dto.news.NewsOverviewResponse;
import com.huodaizi.backend.dto.news.NewsSectionType;
import com.huodaizi.backend.repository.news.InMemoryNewsRepository;
import com.huodaizi.backend.repository.news.NewsEntity;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class NewsService {

  private final InMemoryNewsRepository repository;

  public NewsService(InMemoryNewsRepository repository) {
    this.repository = repository;
  }

  public NewsOverviewResponse overview(NewsListRequest request) {
    List<NewsEntity> filtered = repository.listOnline(request, NewsSectionType.NEWS);
    int page = request.safePage();
    int pageSize = request.safePageSize();
    int from = Math.max((page - 1) * pageSize, 0);
    int to = Math.min(from + pageSize, filtered.size());
    List<NewsEntity> paged = from >= filtered.size() ? List.of() : filtered.subList(from, to);
    List<NewsItemDTO> items = paged.stream().map(this::toDTO).toList();

    NewsListRequest noFilter = new NewsListRequest(null, null, null, 1, 8);
    List<String> hotReads =
        repository.listOnline(noFilter, NewsSectionType.HOT_READ).stream()
            .map(NewsEntity::getTitle)
            .limit(8)
            .toList();
    NewsItemDTO ad =
        repository.listOnline(noFilter, NewsSectionType.AD).stream()
            .findFirst()
            .map(this::toDTO)
            .orElse(null);
    List<String> categoryTabs = List.of("全部", "政策", "市场", "库存", "产业链");
    return new NewsOverviewResponse(categoryTabs, items, hotReads, ad, filtered.size(), page, pageSize);
  }

  public NewsListResponse list(NewsListRequest request) {
    List<NewsEntity> filtered = repository.listOnline(request, NewsSectionType.NEWS);
    int page = request.safePage();
    int pageSize = request.safePageSize();
    int from = Math.max((page - 1) * pageSize, 0);
    int to = Math.min(from + pageSize, filtered.size());
    List<NewsEntity> paged = from >= filtered.size() ? List.of() : filtered.subList(from, to);
    List<NewsItemDTO> items = paged.stream().map(this::toDTO).toList();

    NewsListRequest noFilter = new NewsListRequest(null, null, null, 1, 8);
    List<String> hotReads =
        repository.listOnline(noFilter, NewsSectionType.HOT_READ).stream()
            .map(NewsEntity::getTitle)
            .limit(8)
            .toList();
    return new NewsListResponse(items, hotReads, filtered.size(), page, pageSize);
  }

  public List<NewsItemDTO> adminList(NewsSectionType section) {
    return repository.adminList(section).stream().map(this::toDTO).toList();
  }

  public NewsItemDTO adminCreate(NewsSectionType section, NewsAdminCreateRequest request) {
    return toDTO(repository.adminCreate(section, request));
  }

  public NewsItemDTO adminUpdate(NewsSectionType section, String id, NewsAdminUpdateRequest request) {
    return toDTO(repository.adminUpdate(id, section, request));
  }

  public NewsItemDTO adminChangeStatus(NewsSectionType section, String id, String status) {
    return toDTO(repository.adminChangeStatus(id, section, status));
  }

  public NewsItemDTO adminPin(NewsSectionType section, String id, Boolean pinned) {
    if (pinned == null) {
      throw new BaseException(ErrorCode.BAD_REQUEST.getCode(), "pinned 不能为空");
    }
    return toDTO(repository.adminPin(id, section, pinned));
  }

  public void adminDelete(NewsSectionType section, String id) {
    repository.adminDelete(id, section);
  }

  private NewsItemDTO toDTO(NewsEntity entity) {
    String summary = fallback(entity.getSummary(), entity.getTitle());
    return new NewsItemDTO(
        entity.getId(),
        entity.getTitle(),
        summary,
        entity.getCategory(),
        entity.getCity(),
        entity.getPublishAt(),
        entity.getLink(),
        entity.getStatus(),
        entity.isPinned(),
        entity.getUpdatedAt().toString());
  }

  private String fallback(String value, String defaultText) {
    if (value == null || value.isBlank() || "-".equals(value.trim())) {
      return defaultText;
    }
    return value;
  }
}
