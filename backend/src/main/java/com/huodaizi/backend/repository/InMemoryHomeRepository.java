package com.huodaizi.backend.repository;

import com.huodaizi.backend.common.BaseException;
import com.huodaizi.backend.common.ErrorCode;
import com.huodaizi.backend.dto.AdSlotDTO;
import com.huodaizi.backend.dto.HomeConfigDTO;
import com.huodaizi.backend.dto.HomeOverviewResponse;
import com.huodaizi.backend.dto.LogisticsFreightDTO;
import com.huodaizi.backend.dto.LogisticsWarehouseDTO;
import com.huodaizi.backend.dto.MarketQuoteDTO;
import com.huodaizi.backend.dto.NewsItemDTO;
import com.huodaizi.backend.dto.StationDTO;
import com.huodaizi.backend.dto.SupplyDemandItemDTO;
import com.huodaizi.backend.dto.admin.AdminCreateRequest;
import com.huodaizi.backend.dto.admin.AdminUpdateRequest;
import com.huodaizi.backend.repository.model.AdminEntityType;
import com.huodaizi.backend.repository.model.BaseAdminEntity;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;
import org.springframework.stereotype.Repository;

@Repository
public class InMemoryHomeRepository {

  private final AtomicLong seq = new AtomicLong(1000);
  private final Map<AdminEntityType, LinkedHashMap<String, BaseAdminEntity>> store =
      new EnumMap<>(AdminEntityType.class);

  public InMemoryHomeRepository() {
    for (AdminEntityType type : AdminEntityType.values()) {
      store.put(type, new LinkedHashMap<>());
    }
    seed();
  }

  public HomeOverviewResponse getOverview() {
    return new HomeOverviewResponse(
        getHomeConfig(),
        getMarketQuotes(),
        getLatestSupplies(),
        getLatestDemands(),
        getWarehouses(),
        getFreights(),
        getStations(),
        getNews(),
        getAdSlots());
  }

  public HomeConfigDTO getHomeConfig() {
    BaseAdminEntity entity = requireFirst(AdminEntityType.HOME_CONFIG);
    return new HomeConfigDTO(
        entity.getTitle(),
        entity.getContent(),
        "发布供应",
        "发布求购",
        "发布物流需求",
        "请输入品类、规格、企业或城市",
        "螺纹钢 | 热卷 | 中厚板 | 唐山到无锡专线");
  }

  public List<MarketQuoteDTO> getMarketQuotes() {
    return listByType(AdminEntityType.MARKET_QUOTE).stream()
        .map(
            e ->
                new MarketQuoteDTO(
                    e.getTitle(),
                    e.getCity(),
                    defaultText(e.getPrice(), "-"),
                    defaultText(e.getTrend(), "0")))
        .toList();
  }

  public List<SupplyDemandItemDTO> getLatestSupplies() {
    return listByType(AdminEntityType.SUPPLY).stream()
        .map(
            e ->
                new SupplyDemandItemDTO(
                    e.getId(),
                    e.getTitle(),
                    e.getCity(),
                    defaultText(e.getCategory(), "现货"),
                    defaultText(e.getPrice(), e.getMeta()),
                    e.getUpdatedAt().toString()))
        .toList();
  }

  public List<SupplyDemandItemDTO> getLatestDemands() {
    return listByType(AdminEntityType.DEMAND).stream()
        .map(
            e ->
                new SupplyDemandItemDTO(
                    e.getId(),
                    e.getTitle(),
                    e.getCity(),
                    defaultText(e.getCategory(), "求购"),
                    defaultText(e.getPrice(), e.getMeta()),
                    e.getUpdatedAt().toString()))
        .toList();
  }

  public List<LogisticsWarehouseDTO> getWarehouses() {
    return listByType(AdminEntityType.WAREHOUSE).stream()
        .map(
            e ->
                new LogisticsWarehouseDTO(
                    e.getId(),
                    e.getTitle(),
                    e.getCity(),
                    defaultText(e.getMeta(), "仓储服务"),
                    defaultText(e.getPrice(), "-")))
        .toList();
  }

  public List<LogisticsFreightDTO> getFreights() {
    return listByType(AdminEntityType.FREIGHT).stream()
        .map(
            e ->
                new LogisticsFreightDTO(
                    e.getId(),
                    e.getTitle(),
                    defaultText(e.getCategory(), "13米平板"),
                    defaultText(e.getMeta(), "48小时"),
                    defaultText(e.getPrice(), "-")))
        .toList();
  }

  public List<StationDTO> getStations() {
    return listByType(AdminEntityType.STATION).stream()
        .map(
            e ->
                new StationDTO(
                    e.getId(),
                    e.getTitle(),
                    slugify(e.getTitle()),
                    defaultText(e.getMeta(), "本地供求与行情信息"),
                    100 + (int) (Math.abs(e.getId().hashCode()) % 80),
                    true))
        .toList();
  }

  public List<NewsItemDTO> getNews() {
    return listByType(AdminEntityType.NEWS).stream()
        .map(
            e ->
                new NewsItemDTO(
                    e.getId(),
                    e.getTitle(),
                    defaultText(e.getMeta(), "行业资讯"),
                    defaultText(e.getCategory(), "市场"),
                    e.getUpdatedAt().toString()))
        .toList();
  }

  public List<AdSlotDTO> getAdSlots() {
    return listByType(AdminEntityType.AD_SLOT).stream()
        .map(
            e ->
                new AdSlotDTO(
                    e.getId(),
                    e.getTitle(),
                    defaultText(e.getCategory(), "分站"),
                    e.getCity(),
                    "ACTIVE",
                    defaultText(e.getMeta(), "咨询投放")))
        .toList();
  }

  public List<BaseAdminEntity> listByType(AdminEntityType type) {
    return sortedCopy(store.get(type).values());
  }

  public BaseAdminEntity getConfig() {
    return requireFirst(AdminEntityType.HOME_CONFIG);
  }

  public BaseAdminEntity createByType(AdminEntityType type, AdminCreateRequest request) {
    if (type == AdminEntityType.HOME_CONFIG) {
      throw new BaseException(ErrorCode.BAD_REQUEST.getCode(), "HOME_CONFIG 仅支持更新");
    }
    BaseAdminEntity entity =
        new BaseAdminEntity(
            nextId(),
            type,
            valueOrFallback(request.name(), request.title()),
            valueOrFallback(request.title(), request.name()),
            valueOrFallback(request.city(), inferCity(request.title())),
            valueOrFallback(request.category(), request.type()),
            request.content(),
            valueOrFallback(request.meta(), request.content()),
            request.price(),
            request.trend(),
            LocalDateTime.now());
    store.get(type).put(entity.getId(), entity);
    return entity;
  }

  public BaseAdminEntity updateConfig(String title, String content) {
    BaseAdminEntity config = requireFirst(AdminEntityType.HOME_CONFIG);
    BaseAdminEntity updated =
        new BaseAdminEntity(
            config.getId(),
            config.getType(),
            config.getName(),
            title,
            config.getCity(),
            config.getCategory(),
            content,
            config.getMeta(),
            config.getPrice(),
            config.getTrend(),
            LocalDateTime.now());
    store.get(AdminEntityType.HOME_CONFIG).put(updated.getId(), updated);
    return updated;
  }

  public BaseAdminEntity updateByType(
      AdminEntityType type, String id, AdminUpdateRequest request) {
    BaseAdminEntity existing = requireByTypeAndId(type, id);
    BaseAdminEntity updated =
        new BaseAdminEntity(
            existing.getId(),
            existing.getType(),
            valueOrFallback(request.name(), existing.getName()),
            valueOrFallback(request.title(), request.name(), existing.getTitle()),
            valueOrFallback(request.city(), existing.getCity()),
            valueOrFallback(request.category(), request.type(), existing.getCategory()),
            valueOrFallback(request.content(), existing.getContent()),
            valueOrFallback(request.meta(), existing.getMeta()),
            valueOrFallback(request.price(), existing.getPrice()),
            valueOrFallback(request.trend(), existing.getTrend()),
            LocalDateTime.now());
    store.get(type).put(id, updated);
    return updated;
  }

  public void deleteByTypeAndId(AdminEntityType type, String id) {
    BaseAdminEntity existing = requireByTypeAndId(type, id);
    if (existing.getType() == AdminEntityType.HOME_CONFIG) {
      throw new BaseException(ErrorCode.BAD_REQUEST.getCode(), "HOME_CONFIG 不支持删除");
    }
    store.get(type).remove(id);
  }

  public List<BaseAdminEntity> searchEntities(String city, String type, String keyword) {
    String cityKey = normalize(city);
    String keywordKey = normalize(keyword);
    String typeKey = normalize(type);
    return store.values().stream()
        .flatMap(m -> m.values().stream())
        .filter(e -> cityKey.isBlank() || normalize(e.getCity()).contains(cityKey))
        .filter(e -> typeKey.isBlank() || normalize(e.getType().name()).contains(typeKey))
        .filter(
            e ->
                keywordKey.isBlank()
                    || normalize(e.getTitle()).contains(keywordKey)
                    || normalize(e.getContent()).contains(keywordKey)
                    || normalize(e.getMeta()).contains(keywordKey))
        .sorted(Comparator.comparing(BaseAdminEntity::getUpdatedAt).reversed())
        .toList();
  }

  private BaseAdminEntity requireByTypeAndId(AdminEntityType type, String id) {
    BaseAdminEntity entity = store.get(type).get(id);
    if (entity == null) {
      throw new BaseException(
          ErrorCode.NOT_FOUND.getCode(), type.name() + " 下未找到ID=" + id + " 的记录");
    }
    return entity;
  }

  private BaseAdminEntity requireFirst(AdminEntityType type) {
    return sortedCopy(store.get(type).values()).stream()
        .findFirst()
        .orElseThrow(() -> new BaseException(ErrorCode.NOT_FOUND.getCode(), type + " 数据为空"));
  }

  private List<BaseAdminEntity> sortedCopy(Iterable<BaseAdminEntity> values) {
    List<BaseAdminEntity> list = new ArrayList<>();
    values.forEach(list::add);
    list.sort(Comparator.comparing(BaseAdminEntity::getUpdatedAt).reversed());
    return list;
  }

  private String nextId() {
    return String.valueOf(seq.incrementAndGet());
  }

  private String valueOrFallback(String primary, String fallback) {
    if (primary != null && !primary.isBlank()) {
      return primary;
    }
    return fallback == null ? "" : fallback;
  }

  private String valueOrFallback(String primary, String fallback, String fallback2) {
    if (primary != null && !primary.isBlank()) {
      return primary;
    }
    if (fallback != null && !fallback.isBlank()) {
      return fallback;
    }
    return fallback2 == null ? "" : fallback2;
  }

  private String normalize(String text) {
    return text == null ? "" : text.trim().toLowerCase(Locale.ROOT);
  }

  private String defaultText(String text, String fallback) {
    return text == null || text.isBlank() ? fallback : text;
  }

  private String inferCity(String text) {
    if (text == null || text.isBlank()) {
      return "全国";
    }
    List<String> cities =
        List.of("唐山", "无锡", "佛山", "武汉", "天津", "郑州", "南京", "上海", "成都", "邯郸");
    return cities.stream().filter(text::contains).findFirst().orElse("全国");
  }

  public List<BaseAdminEntity> search(String city, String type, String keyword) {
    return searchEntities(city, type, keyword);
  }

  private String slugify(String text) {
    if (text == null || text.isBlank()) {
      return "city";
    }
    Map<String, String> mapping =
        Map.of(
            "唐山", "tangshan",
            "邯郸", "handan",
            "天津", "tianjin",
            "无锡", "wuxi",
            "南京", "nanjing",
            "上海", "shanghai",
            "佛山", "foshan",
            "武汉", "wuhan",
            "郑州", "zhengzhou",
            "成都", "chengdu");
    return mapping.entrySet().stream()
        .filter(e -> text.contains(e.getKey()))
        .map(Map.Entry::getValue)
        .findFirst()
        .orElse("city");
  }

  private void seed() {
    seedEntity(
        AdminEntityType.HOME_CONFIG,
        "钢铁交易，一站直达",
        "覆盖现货、求购、行情、仓储物流与城市分站，帮助钢贸企业高效获客与撮合交易。",
        "全国",
        null,
        null,
        "首页配置",
        "CONFIG",
        1);
    seedEntity(
        AdminEntityType.MARKET_QUOTE,
        "螺纹钢",
        "唐山主流成交价",
        "唐山",
        "3620",
        "+25",
        "行情速览",
        "行情",
        5);
    seedEntity(
        AdminEntityType.MARKET_QUOTE,
        "热轧卷板",
        "无锡主流成交价",
        "无锡",
        "3780",
        "-15",
        "行情速览",
        "行情",
        8);
    seedEntity(
        AdminEntityType.SUPPLY,
        "唐山螺纹钢HRB400E 12-25",
        "现货充足，可当天提货",
        "唐山",
        "860吨",
        null,
        "860吨 · 唐山 · 5分钟前",
        "供应",
        12);
    seedEntity(
        AdminEntityType.SUPPLY,
        "无锡热卷Q235B 3.0*1250",
        "支持分批发货",
        "无锡",
        "520吨",
        null,
        "520吨 · 无锡 · 12分钟前",
        "供应",
        15);
    seedEntity(
        AdminEntityType.DEMAND,
        "求购螺纹钢HRB400E 16-25",
        "工地项目需求",
        "郑州",
        "600吨",
        null,
        "600吨 · 郑州 · 8分钟前",
        "求购",
        9);
    seedEntity(
        AdminEntityType.DEMAND,
        "求购热卷Q235B 4.75*1500",
        "现货优先",
        "南京",
        "420吨",
        null,
        "420吨 · 南京 · 15分钟前",
        "求购",
        18);
    seedEntity(
        AdminEntityType.WAREHOUSE,
        "唐山海港仓储中心",
        "室内库 / 20吨行车",
        "唐山",
        "0.9元/吨/天",
        null,
        "室内库 / 20吨行车",
        "仓储",
        7);
    seedEntity(
        AdminEntityType.WAREHOUSE,
        "无锡城南钢材仓",
        "露天场 / 可夜间作业",
        "无锡",
        "0.8元/吨/天",
        null,
        "露天场 / 可夜间作业",
        "仓储",
        11);
    seedEntity(
        AdminEntityType.FREIGHT,
        "唐山 → 无锡",
        "13米平板 / 48小时",
        "唐山",
        "120元/吨起",
        null,
        "48小时",
        "13米平板",
        10);
    seedEntity(
        AdminEntityType.FREIGHT,
        "天津 → 佛山",
        "17.5米平板 / 72小时",
        "天津",
        "165元/吨起",
        null,
        "72小时",
        "17.5米平板",
        13);
    seedEntity(
        AdminEntityType.STATION,
        "唐山分站",
        "建材流通活跃",
        "唐山",
        null,
        null,
        "今日新增 126 条供求",
        "分站",
        6);
    seedEntity(
        AdminEntityType.STATION,
        "无锡分站",
        "热卷需求旺盛",
        "无锡",
        null,
        null,
        "今日新增 138 条供求",
        "分站",
        16);
    seedEntity(
        AdminEntityType.NEWS,
        "钢材库存连续两周下降，建材成交回暖",
        "市场动态",
        "唐山",
        null,
        null,
        "市场动态",
        "资讯",
        4);
    seedEntity(
        AdminEntityType.NEWS,
        "华东热卷报价小幅震荡，终端按需采购",
        "行情快讯",
        "无锡",
        null,
        null,
        "行情快讯",
        "资讯",
        14);
    seedEntity(
        AdminEntityType.AD_SLOT,
        "分站广告招商",
        "支持按城市、品类、频道定向投放",
        "全国",
        "3000元/月起",
        null,
        "咨询投放",
        "分站广告",
        3);
    seedEntity(
        AdminEntityType.AD_SLOT,
        "分站信息流广告位",
        "融入供求与资讯流，提升点击与询盘转化",
        "全国",
        "120元/天起",
        null,
        "咨询投放",
        "分站广告",
        2);
  }

  private void seedEntity(
      AdminEntityType type,
      String title,
      String content,
      String city,
      String price,
      String trend,
      String meta,
      String category,
      long minusMinutes) {
    String id = nextId();
    BaseAdminEntity entity =
        new BaseAdminEntity(
            id,
            type,
            title,
            title,
            city,
            category,
            content,
            meta,
            price,
            trend,
            LocalDateTime.now().minusMinutes(minusMinutes));
    store.get(type).put(id, entity);
  }
}
