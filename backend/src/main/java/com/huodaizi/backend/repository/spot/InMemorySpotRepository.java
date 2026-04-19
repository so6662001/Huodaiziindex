package com.huodaizi.backend.repository.spot;

import com.huodaizi.backend.common.BaseException;
import com.huodaizi.backend.common.ErrorCode;
import com.huodaizi.backend.dto.spot.SpotAdminUpdateRequest;
import com.huodaizi.backend.dto.spot.SpotFilterOptions;
import com.huodaizi.backend.dto.spot.SpotFilterRequest;
import com.huodaizi.backend.dto.spot.SpotPublishRequest;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicLong;
import org.springframework.stereotype.Repository;

@Repository
public class InMemorySpotRepository {

  private static final String STATUS_ONLINE = "ONLINE";
  private static final String STATUS_OFFLINE = "OFFLINE";

  private final AtomicLong seq = new AtomicLong(20260418000L);
  private final ConcurrentMap<String, SpotEntity> store = new ConcurrentHashMap<>();

  public InMemorySpotRepository() {
    seed();
  }

  public SpotFilterOptions getFilterOptions() {
    return SpotFilterOptions.defaultOptions();
  }

  public List<SpotEntity> query(SpotFilterRequest request) {
    String category = normalize(request.category());
    String spec = normalize(request.spec());
    String city = normalize(request.city());
    String keyword = normalize(request.keyword());
    String priceRange = normalize(request.priceRange());
    return store.values().stream()
        .filter(item -> STATUS_ONLINE.equals(item.getStatus()))
        .filter(
            item ->
                category.isBlank()
                    || "全部品类".equals(category)
                    || normalize(item.getCategory()).contains(category))
        .filter(
            item ->
                spec.isBlank() || "全部规格".equals(spec) || normalize(item.getSpec()).contains(spec))
        .filter(
            item ->
                city.isBlank() || "全部城市".equals(city) || normalize(item.getCity()).contains(city))
        .filter(
            item ->
                keyword.isBlank()
                    || normalize(item.getTitle()).contains(keyword)
                    || normalize(item.getSeller()).contains(keyword)
                    || normalize(item.getCategory()).contains(keyword)
                    || normalize(item.getSpec()).contains(keyword))
        .filter(item -> matchPriceRange(item.getPrice(), priceRange))
        .sorted(
            Comparator.comparing(SpotEntity::isPinned).reversed()
                .thenComparing(SpotEntity::getUpdatedAt, Comparator.reverseOrder()))
        .toList();
  }

  public SpotEntity getById(String id) {
    SpotEntity entity = requireById(id);
    if (!STATUS_ONLINE.equals(entity.getStatus())) {
      throw new BaseException(ErrorCode.NOT_FOUND.getCode(), "现货信息不存在或已下架");
    }
    return entity;
  }

  public SpotEntity publish(SpotPublishRequest request) {
    String id = "S" + seq.incrementAndGet();
    String contactName =
        request.contactName() == null || request.contactName().isBlank()
            ? request.seller()
            : request.contactName();
    SpotEntity entity =
        new SpotEntity(
            id,
            request.title(),
            request.seller(),
            request.category(),
            request.spec(),
            request.city(),
            request.price(),
            request.delivery(),
            request.tonnage(),
            contactName,
            maskPhone(request.contactPhone()),
            STATUS_ONLINE,
            false,
            LocalDateTime.now());
    store.put(id, entity);
    return entity;
  }

  public List<SpotEntity> listAll() {
    return store.values().stream()
        .sorted(
            Comparator.comparing(SpotEntity::isPinned).reversed()
                .thenComparing(SpotEntity::getUpdatedAt, Comparator.reverseOrder()))
        .toList();
  }

  public SpotEntity adminCreate(SpotPublishRequest request) {
    return publish(request);
  }

  public SpotEntity adminUpdate(String id, SpotAdminUpdateRequest request) {
    SpotEntity current = requireById(id);
    String normalizedStatus = request.status() == null ? null : normalizeStatus(request.status());
    String maskedPhone =
        request.contactPhone() == null ? null : maskPhone(request.contactPhone());
    current.update(
        request.title(),
        request.seller(),
        request.category(),
        request.spec(),
        request.city(),
        request.price(),
        request.delivery(),
        request.tonnage(),
        request.contactName(),
        maskedPhone,
        normalizedStatus,
        request.pinned());
    return current;
  }

  public SpotEntity adminToggleStatus(String id, String status) {
    SpotEntity current = requireById(id);
    String normalized = normalizeStatus(status);
    current.setStatus(normalized);
    return current;
  }

  public SpotEntity adminPin(String id, boolean pinned) {
    SpotEntity current = requireById(id);
    current.setPinned(pinned);
    return current;
  }

  public void adminDelete(String id) {
    SpotEntity removed = store.remove(id);
    if (removed == null) {
      throw new BaseException(ErrorCode.NOT_FOUND.getCode(), "现货记录不存在");
    }
  }

  private SpotEntity requireById(String id) {
    SpotEntity entity = store.get(id);
    if (entity == null) {
      throw new BaseException(ErrorCode.NOT_FOUND.getCode(), "现货记录不存在");
    }
    return entity;
  }

  private String normalize(String text) {
    return text == null ? "" : text.trim().toLowerCase(Locale.ROOT);
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

  private String maskPhone(String phone) {
    if (phone == null || phone.isBlank()) {
      return "未公开";
    }
    String digits = phone.replaceAll("[^0-9]", "");
    if (digits.length() < 7) {
      return phone;
    }
    return digits.substring(0, 3) + "****" + digits.substring(digits.length() - 4);
  }

  private boolean matchPriceRange(String priceText, String range) {
    if (range.isBlank() || "全部价格".equals(range)) {
      return true;
    }
    int value = parsePrice(priceText);
    if (value <= 0) {
      return true;
    }
    return switch (range) {
      case "3000以下" -> value < 3000;
      case "3000-3500" -> value >= 3000 && value <= 3500;
      case "3500-4000" -> value > 3500 && value <= 4000;
      case "4000以上" -> value > 4000;
      default -> true;
    };
  }

  private int parsePrice(String priceText) {
    if (priceText == null || priceText.isBlank()) {
      return -1;
    }
    String digits = priceText.replaceAll("[^0-9]", "");
    if (digits.isBlank()) {
      return -1;
    }
    try {
      return Integer.parseInt(digits);
    } catch (NumberFormatException ex) {
      return -1;
    }
  }

  private void seed() {
    addSeed(
        "S20260418001",
        "唐山螺纹钢 HRB400E 12-25mm",
        "唐山宏信钢贸",
        "螺纹钢",
        "HRB400E 12-25mm",
        "860吨",
        "唐山",
        "3620元/吨",
        "现货即提",
        "张经理",
        "13800001234",
        STATUS_ONLINE,
        true,
        5);
    addSeed(
        "S20260418002",
        "无锡热卷 Q235B 4.75*1500",
        "无锡金港供应链",
        "热卷",
        "Q235B 4.75*1500",
        "520吨",
        "无锡",
        "3780元/吨",
        "2天内发货",
        "刘经理",
        "13900005678",
        STATUS_ONLINE,
        false,
        12);
    addSeed(
        "S20260418003",
        "天津中厚板 Q355B 16-40mm",
        "天津北方钢材",
        "中厚板",
        "Q355B 16-40mm",
        "300吨",
        "天津",
        "3950元/吨",
        "可分批提货",
        "王经理",
        "13700006789",
        STATUS_ONLINE,
        false,
        18);
    addSeed(
        "S20260418004",
        "佛山镀锌卷 DX51D 1.0mm",
        "佛山汇海钢铁",
        "管材",
        "DX51D 1.0mm",
        "240吨",
        "佛山",
        "4230元/吨",
        "现货即提",
        "陈经理",
        "13600007890",
        STATUS_ONLINE,
        false,
        26);
    addSeed(
        "S20260418005",
        "武汉型钢 H型钢 200*200",
        "武汉联盛钢贸",
        "型钢",
        "H型钢 200*200",
        "410吨",
        "武汉",
        "3880元/吨",
        "48小时内发货",
        "李经理",
        "13500008901",
        STATUS_ONLINE,
        false,
        31);
  }

  private void addSeed(
      String id,
      String title,
      String seller,
      String category,
      String spec,
      String tonnage,
      String city,
      String price,
      String delivery,
      String contactName,
      String contactPhone,
      String status,
      boolean pinned,
      long minutesAgo) {
    seq.updateAndGet(v -> Math.max(v, parseNumericId(id)));
    SpotEntity entity =
        new SpotEntity(
            id,
            title,
            seller,
            category,
            spec,
            city,
            price,
            delivery,
            tonnage,
            contactName,
            maskPhone(contactPhone),
            status,
            pinned,
            LocalDateTime.now().minusMinutes(minutesAgo));
    store.put(id, entity);
  }

  private long parseNumericId(String id) {
    if (id == null) {
      return seq.get();
    }
    String digits = id.replaceAll("[^0-9]", "");
    if (digits.isBlank()) {
      return seq.get();
    }
    try {
      return Long.parseLong(digits);
    } catch (NumberFormatException ex) {
      return seq.get();
    }
  }
}
