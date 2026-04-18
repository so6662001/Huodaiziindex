package com.huodaizi.backend.repository.warehouse;

import com.huodaizi.backend.common.BaseException;
import com.huodaizi.backend.common.ErrorCode;
import com.huodaizi.backend.dto.warehouse.WarehouseAdminUpdateRequest;
import com.huodaizi.backend.dto.warehouse.WarehouseFilterOptions;
import com.huodaizi.backend.dto.warehouse.WarehouseFilterRequest;
import com.huodaizi.backend.dto.warehouse.WarehousePublishRequest;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicLong;
import org.springframework.stereotype.Repository;

@Repository
public class InMemoryWarehouseRepository {

  private static final String STATUS_ONLINE = "ONLINE";
  private static final String STATUS_OFFLINE = "OFFLINE";

  private final AtomicLong seq = new AtomicLong(20260418000L);
  private final ConcurrentMap<String, WarehouseEntity> store = new ConcurrentHashMap<>();

  public InMemoryWarehouseRepository() {
    seed();
  }

  public WarehouseFilterOptions getFilterOptions() {
    return WarehouseFilterOptions.defaultOptions();
  }

  public List<WarehouseEntity> query(WarehouseFilterRequest request) {
    String city = normalize(request.city());
    String type = normalize(request.warehouseType());
    String category = normalize(request.category());
    String lifting = normalize(request.lifting());
    String priceRange = normalize(request.priceRange());
    String keyword = normalize(request.keyword());
    return store.values().stream()
        .filter(item -> STATUS_ONLINE.equals(item.getStatus()))
        .filter(item -> city.isBlank() || "全部城市".equals(city) || normalize(item.getCity()).contains(city))
        .filter(
            item ->
                type.isBlank()
                    || "全部库型".equals(type)
                    || normalize(item.getType()).contains(type))
        .filter(
            item ->
                category.isBlank()
                    || "全部品类".equals(category)
                    || normalize(item.getCategories()).contains(category))
        .filter(
            item ->
                lifting.isBlank()
                    || "全部能力".equals(lifting)
                    || normalize(item.getCapability()).contains(lifting))
        .filter(item -> matchPriceRange(item.getPrice(), priceRange))
        .filter(
            item ->
                keyword.isBlank()
                    || normalize(item.getName()).contains(keyword)
                    || normalize(item.getCapability()).contains(keyword)
                    || normalize(item.getAddress()).contains(keyword))
        .sorted(
            Comparator.comparing(WarehouseEntity::isPinned).reversed()
                .thenComparing(WarehouseEntity::getUpdatedAt, Comparator.reverseOrder()))
        .toList();
  }

  public WarehouseEntity getById(String id) {
    WarehouseEntity entity = requireById(id);
    if (!STATUS_ONLINE.equals(entity.getStatus())) {
      throw new BaseException(ErrorCode.NOT_FOUND.getCode(), "仓库信息不存在或已下线");
    }
    return entity;
  }

  public List<WarehouseEntity> listAll() {
    return store.values().stream()
        .sorted(
            Comparator.comparing(WarehouseEntity::isPinned).reversed()
                .thenComparing(WarehouseEntity::getUpdatedAt, Comparator.reverseOrder()))
        .toList();
  }

  public WarehouseEntity publish(WarehousePublishRequest request) {
    String id = "W" + seq.incrementAndGet();
    WarehouseEntity entity =
        new WarehouseEntity(
            id,
            request.name(),
            request.city(),
            request.warehouseType(),
            request.capacity(),
            request.throughput(),
            request.capability(),
            request.categories(),
            request.price(),
            defaultText(request.address(), "-"),
            defaultText(request.contactName(), "未公开"),
            maskPhone(request.contactPhone()),
            STATUS_ONLINE,
            false,
            LocalDateTime.now());
    store.put(id, entity);
    return entity;
  }

  public WarehouseEntity adminCreate(WarehousePublishRequest request) {
    return publish(request);
  }

  public WarehouseEntity adminUpdate(String id, WarehouseAdminUpdateRequest request) {
    WarehouseEntity entity = requireById(id);
    String normalizedStatus = request.status() == null ? null : normalizeStatus(request.status());
    String maskedPhone = request.contactPhone() == null ? null : maskPhone(request.contactPhone());
    entity.update(
        request.name(),
        request.city(),
        request.warehouseType(),
        request.capacity(),
        request.throughput(),
        request.capability(),
        request.categories(),
        request.price(),
        request.address(),
        request.contactName(),
        maskedPhone,
        normalizedStatus,
        request.pinned());
    return entity;
  }

  public WarehouseEntity adminChangeStatus(String id, String status) {
    WarehouseEntity entity = requireById(id);
    entity.setStatus(normalizeStatus(status));
    return entity;
  }

  public WarehouseEntity adminPin(String id, boolean pinned) {
    WarehouseEntity entity = requireById(id);
    entity.setPinned(pinned);
    return entity;
  }

  public void adminDelete(String id) {
    WarehouseEntity removed = store.remove(id);
    if (removed == null) {
      throw new BaseException(ErrorCode.NOT_FOUND.getCode(), "仓库记录不存在");
    }
  }

  private WarehouseEntity requireById(String id) {
    WarehouseEntity entity = store.get(id);
    if (entity == null) {
      throw new BaseException(ErrorCode.NOT_FOUND.getCode(), "仓库记录不存在");
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

  private String defaultText(String text, String fallback) {
    return text == null || text.isBlank() ? fallback : text;
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
    double value = parsePriceValue(priceText);
    if (value < 0) {
      return true;
    }
    return switch (range) {
      case "0.8元/吨/天以下" -> value < 0.8D;
      case "0.8-1.0元/吨/天" -> value >= 0.8D && value <= 1.0D;
      case "1.0元/吨/天以上" -> value > 1.0D;
      default -> true;
    };
  }

  private double parsePriceValue(String priceText) {
    if (priceText == null || priceText.isBlank()) {
      return -1D;
    }
    String num = priceText.replaceAll("[^0-9.]", "");
    if (num.isBlank()) {
      return -1D;
    }
    try {
      return Double.parseDouble(num);
    } catch (NumberFormatException ex) {
      return -1D;
    }
  }

  private void seed() {
    addSeed(
        "W20260418001",
        "唐山海港仓储中心",
        "唐山",
        "室内库",
        "50,000吨",
        "日吞吐1,200吨",
        "20吨行车 / 夜间作业",
        "螺纹钢,型钢,热卷",
        "0.9元/吨/天",
        "唐山海港开发区",
        "赵经理",
        "13800021234",
        STATUS_ONLINE,
        true,
        5);
    addSeed(
        "W20260418002",
        "无锡城南钢材仓",
        "无锡",
        "露天场",
        "32,000吨",
        "日吞吐800吨",
        "15吨行车 / 分拣服务",
        "热卷,中厚板",
        "0.8元/吨/天",
        "无锡城南物流园",
        "钱经理",
        "13900025678",
        STATUS_ONLINE,
        false,
        12);
    addSeed(
        "W20260418003",
        "佛山顺德物流仓",
        "佛山",
        "综合库",
        "26,000吨",
        "日吞吐620吨",
        "10吨行车 / 临港短驳",
        "镀锌卷,管材,型钢",
        "1.1元/吨/天",
        "佛山顺德临港区",
        "孙经理",
        "13700026789",
        STATUS_ONLINE,
        false,
        18);
    addSeed(
        "W20260418004",
        "武汉临港钢材库",
        "武汉",
        "室内库",
        "42,000吨",
        "日吞吐980吨",
        "25吨行车 / 可分批出库",
        "螺纹钢,中厚板,型钢",
        "1.0元/吨/天",
        "武汉临港工业园",
        "李经理",
        "13600027890",
        STATUS_ONLINE,
        false,
        24);
  }

  private void addSeed(
      String id,
      String name,
      String city,
      String warehouseType,
      String capacity,
      String throughput,
      String capability,
      String categoriesText,
      String price,
      String address,
      String contactName,
      String contactPhone,
      String status,
      boolean pinned,
      long minusMinutes) {
    seq.updateAndGet(v -> Math.max(v, parseNumericId(id)));
    WarehouseEntity entity =
        new WarehouseEntity(
            id,
            name,
            city,
            warehouseType,
            capacity,
            throughput,
            capability,
            categoriesText,
            price,
            address,
            contactName,
            maskPhone(contactPhone),
            status,
            pinned,
            LocalDateTime.now().minusMinutes(minusMinutes));
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
