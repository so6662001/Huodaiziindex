package com.huodaizi.backend.repository.sitead;

import com.huodaizi.backend.common.BaseException;
import com.huodaizi.backend.common.ErrorCode;
import com.huodaizi.backend.dto.sitead.SiteAdAdminCreateRequest;
import com.huodaizi.backend.dto.sitead.SiteAdAdminUpdateRequest;
import com.huodaizi.backend.dto.sitead.SiteAdOptionsResponse;
import com.huodaizi.backend.dto.sitead.SiteAdPublishRequest;
import com.huodaizi.backend.dto.sitead.SiteAdSectionType;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicLong;
import org.springframework.stereotype.Repository;

@Repository
public class InMemorySiteAdRepository {

  private static final String STATUS_ONLINE = "ONLINE";
  private static final String STATUS_OFFLINE = "OFFLINE";

  private final AtomicLong seq = new AtomicLong(20260418000L);
  private final ConcurrentMap<String, SiteAdEntity> store = new ConcurrentHashMap<>();

  public InMemorySiteAdRepository() {
    seed();
  }

  public List<SiteAdEntity> listOnlineBySection(SiteAdSectionType section) {
    return store.values().stream()
        .filter(item -> item.getSectionType() == section)
        .filter(item -> STATUS_ONLINE.equals(item.getStatus()))
        .sorted(
            Comparator.comparing(SiteAdEntity::isPinned).reversed()
                .thenComparing(SiteAdEntity::getUpdatedAt, Comparator.reverseOrder()))
        .toList();
  }

  public List<SiteAdEntity> adminListBySection(SiteAdSectionType section) {
    return store.values().stream()
        .filter(item -> item.getSectionType() == section)
        .sorted(
            Comparator.comparing(SiteAdEntity::isPinned).reversed()
                .thenComparing(SiteAdEntity::getUpdatedAt, Comparator.reverseOrder()))
        .toList();
  }

  public SiteAdOptionsResponse options() {
    List<SiteAdEntity> rows = listOnlineBySection(SiteAdSectionType.OPTION);
    List<String> cityOptions = splitPipe(rows, "CITY");
    List<String> placementOptions = splitPipe(rows, "PLACEMENT");
    List<String> durationOptions = splitPipe(rows, "DURATION");
    return new SiteAdOptionsResponse(cityOptions, placementOptions, durationOptions);
  }

  public SiteAdEntity submit(SiteAdPublishRequest request) {
    String id = "SAD" + seq.incrementAndGet();
    SiteAdEntity entity =
        new SiteAdEntity(
            id,
            SiteAdSectionType.DEMAND,
            request.city(),
            request.placement(),
            request.duration(),
            request.companyName(),
            request.contactName(),
            request.phone(),
            request.budget(),
            request.remark(),
            "#",
            STATUS_ONLINE,
            false,
            LocalDateTime.now());
    store.put(id, entity);
    return entity;
  }

  public SiteAdEntity adminCreate(SiteAdSectionType section, SiteAdAdminCreateRequest request) {
    String id = "SAD" + seq.incrementAndGet();
    String city = defaultText(request.city(), "-");
    String placement = defaultText(request.placement(), "-");
    String duration = defaultText(request.duration(), "-");
    String companyName = defaultText(request.companyName(), "-");
    String contactName = defaultText(request.contactName(), "-");
    String phone = defaultText(request.phone(), "-");
    String budget = defaultText(request.budget(), "-");
    String remark = defaultText(request.remark(), "-");
    String link = defaultText(request.link(), "#");

    if (section == SiteAdSectionType.PRODUCT) {
      placement = defaultText(request.name(), "-");
      budget = defaultText(request.price(), "-");
      remark = defaultText(request.desc(), "-");
    } else if (section == SiteAdSectionType.OPTION) {
      String optionType = request.optionType() == null ? "CITY" : request.optionType().trim().toUpperCase(Locale.ROOT);
      if (!"CITY".equals(optionType) && !"PLACEMENT".equals(optionType) && !"DURATION".equals(optionType)) {
        throw new BaseException(ErrorCode.BAD_REQUEST.getCode(), "optionType 仅支持 CITY/PLACEMENT/DURATION");
      }
      city = optionType;
      placement = defaultText(request.optionValues(), "-");
      duration = "-";
      companyName = "-";
      contactName = "-";
      phone = "-";
      budget = "-";
      remark = "-";
      link = "#";
    }

    SiteAdEntity entity =
        new SiteAdEntity(
            id,
            section,
            city,
            placement,
            duration,
            companyName,
            contactName,
            phone,
            budget,
            remark,
            link,
            request.status() == null ? STATUS_ONLINE : normalizeStatus(request.status()),
            request.pinned() != null && request.pinned(),
            LocalDateTime.now());

    store.put(id, entity);
    return entity;
  }

  public SiteAdEntity adminUpdate(SiteAdSectionType section, String id, SiteAdAdminUpdateRequest request) {
    SiteAdEntity entity = requireByIdAndSection(id, section);
    String normalizedStatus = request.status() == null ? null : normalizeStatus(request.status());

    String city = request.city();
    String placement = request.placement();
    String duration = request.duration();
    String companyName = request.companyName();
    String contactName = request.contactName();
    String phone = request.phone();
    String budget = request.budget();
    String remark = request.remark();
    String link = request.link();

    if (section == SiteAdSectionType.PRODUCT) {
      placement = request.name() != null ? request.name() : placement;
      budget = request.price() != null ? request.price() : budget;
      remark = request.desc() != null ? request.desc() : remark;
    } else if (section == SiteAdSectionType.OPTION) {
      city = request.optionType() != null ? request.optionType().trim().toUpperCase(Locale.ROOT) : city;
      placement = request.optionValues() != null ? request.optionValues() : placement;
    }

    entity.update(
        city,
        placement,
        duration,
        companyName,
        contactName,
        phone,
        budget,
        remark,
        link,
        normalizedStatus,
        request.pinned());
    return entity;
  }

  public SiteAdEntity adminChangeStatus(SiteAdSectionType section, String id, String status) {
    SiteAdEntity entity = requireByIdAndSection(id, section);
    entity.setStatus(normalizeStatus(status));
    return entity;
  }

  public SiteAdEntity adminPin(SiteAdSectionType section, String id, boolean pinned) {
    SiteAdEntity entity = requireByIdAndSection(id, section);
    entity.setPinned(pinned);
    return entity;
  }

  public void adminDelete(SiteAdSectionType section, String id) {
    SiteAdEntity entity = requireByIdAndSection(id, section);
    store.remove(entity.getId());
  }

  private SiteAdEntity requireByIdAndSection(String id, SiteAdSectionType section) {
    SiteAdEntity entity = store.get(id);
    if (entity == null || entity.getSectionType() != section) {
      throw new BaseException(ErrorCode.NOT_FOUND.getCode(), "分站广告记录不存在");
    }
    return entity;
  }

  private String defaultText(String text, String fallback) {
    return text == null || text.isBlank() ? fallback : text;
  }

  private String normalizeStatus(String status) {
    if (status == null || status.isBlank()) {
      throw new BaseException(ErrorCode.BAD_REQUEST.getCode(), "status 不能为空");
    }
    String normalized = status.trim().toUpperCase(Locale.ROOT);
    if (!STATUS_ONLINE.equals(normalized) && !STATUS_OFFLINE.equals(normalized)) {
      throw new BaseException(ErrorCode.BAD_REQUEST.getCode(), "status 仅支持 ONLINE/OFFLINE");
    }
    return normalized;
  }

  private void seed() {
    seedOption("CITY", "全国|唐山|天津|邯郸|无锡|南京|上海|佛山|武汉|郑州|成都");
    seedOption("PLACEMENT", "分站首页焦点位|分站信息流广告位|分站仓储物流推荐位|分站品牌推广位");
    seedOption("DURATION", "7天|15天|30天|90天");

    seedProduct("分站首页焦点位", "3000元/月起", "适合品牌招商与新品推广，支持城市和品类定向。", true, 5);
    seedProduct("分站信息流广告位", "120元/天起", "融入分站供求与资讯流，提升曝光与线索转化效率。", false, 4);
    seedProduct("分站仓储物流推荐位", "1800元/月起", "面向仓库与车队服务商，支持线路与车型精准投放。", false, 3);

    seedDemand("唐山", "分站首页焦点位", "30天", "唐山钢贸集团", "王经理", "13800138000", "5000-10000", "螺纹钢品类重点推广", 2);
    seedDemand("无锡", "分站信息流广告位", "15天", "无锡板材贸易", "李总", "13900139000", "3000-5000", "提升询盘转化", 1);
  }

  private void seedOption(String optionType, String optionValues) {
    SiteAdEntity entity =
        new SiteAdEntity(
            "SAD" + seq.incrementAndGet(),
            SiteAdSectionType.OPTION,
            optionType,
            optionValues,
            "-",
            "-",
            "-",
            "-",
            "-",
            "#",
            STATUS_ONLINE,
            false,
            LocalDateTime.now());
    store.put(entity.getId(), entity);
  }

  private void seedProduct(String name, String price, String desc, boolean pinned, long minusMinutes) {
    SiteAdEntity entity =
        new SiteAdEntity(
            "SAD" + seq.incrementAndGet(),
            SiteAdSectionType.PRODUCT,
            "-",
            name,
            "-",
            "-",
            "-",
            "-",
            price,
            desc,
            "#",
            STATUS_ONLINE,
            pinned,
            LocalDateTime.now().minusMinutes(minusMinutes));
    store.put(entity.getId(), entity);
  }

  private void seedDemand(
      String city,
      String placement,
      String duration,
      String companyName,
      String contactName,
      String phone,
      String budget,
      String remark,
      long minusMinutes) {
    SiteAdEntity entity =
        new SiteAdEntity(
            "SAD" + seq.incrementAndGet(),
            SiteAdSectionType.DEMAND,
            city,
            placement,
            duration,
            companyName,
            contactName,
            phone,
            budget,
            remark,
            "-",
            STATUS_ONLINE,
            false,
            LocalDateTime.now().minusMinutes(minusMinutes));
    store.put(entity.getId(), entity);
  }

  public String generateDemandNo(String id) {
    return "AD" + java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd").format(LocalDateTime.now()) + "-" + id;
  }

  private List<String> splitPipe(List<SiteAdEntity> rows, String optionType) {
    return rows.stream()
        .filter(item -> optionType.equalsIgnoreCase(item.getCity()))
        .findFirst()
        .map(item -> List.of(item.getPlacement().split("\\|")))
        .orElse(List.of());
  }
}
