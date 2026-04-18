package com.huodaizi.backend.repository.buy;

import com.huodaizi.backend.common.BaseException;
import com.huodaizi.backend.common.ErrorCode;
import com.huodaizi.backend.dto.buy.BuyAdminUpdateRequest;
import com.huodaizi.backend.dto.buy.BuyFilterOptions;
import com.huodaizi.backend.dto.buy.BuyFilterRequest;
import com.huodaizi.backend.dto.buy.BuyPublishRequest;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicLong;
import org.springframework.stereotype.Repository;

@Repository
public class InMemoryBuyRepository {

  private static final String STATUS_ONLINE = "ONLINE";
  private static final String STATUS_OFFLINE = "OFFLINE";

  private final AtomicLong seq = new AtomicLong(20260418000L);
  private final ConcurrentMap<String, BuyEntity> store = new ConcurrentHashMap<>();

  public InMemoryBuyRepository() {
    seed();
  }

  public BuyFilterOptions getFilterOptions() {
    return BuyFilterOptions.defaultOptions();
  }

  public List<BuyEntity> query(BuyFilterRequest request) {
    String category = normalize(request.category());
    String spec = normalize(request.spec());
    String city = normalize(request.city());
    String keyword = normalize(request.keyword());
    String arrival = normalize(request.arrival());
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
                arrival.isBlank()
                    || "全部交期".equals(arrival)
                    || normalize(item.getArrival()).contains(arrival))
        .filter(
            item ->
                keyword.isBlank()
                    || normalize(item.getTitle()).contains(keyword)
                    || normalize(item.getBuyer()).contains(keyword)
                    || normalize(item.getCategory()).contains(keyword)
                    || normalize(item.getSpec()).contains(keyword))
        .sorted(
            Comparator.comparing(BuyEntity::isPinned).reversed()
                .thenComparing(BuyEntity::getUpdatedAt, Comparator.reverseOrder()))
        .toList();
  }

  public BuyEntity getById(String id) {
    BuyEntity entity = requireById(id);
    if (!STATUS_ONLINE.equals(entity.getStatus())) {
      throw new BaseException(ErrorCode.NOT_FOUND.getCode(), "求购信息不存在或已下架");
    }
    return entity;
  }

  public BuyEntity publish(BuyPublishRequest request) {
    String id = "B" + seq.incrementAndGet();
    String contactName =
        request.contactName() == null || request.contactName().isBlank()
            ? request.buyer()
            : request.contactName();
    BuyEntity entity =
        new BuyEntity(
            id,
            request.title(),
            request.buyer(),
            request.category(),
            request.spec(),
            request.city(),
            request.budget(),
            request.arrival(),
            request.demand(),
            contactName,
            maskPhone(request.contactPhone()),
            STATUS_ONLINE,
            false,
            LocalDateTime.now());
    store.put(id, entity);
    return entity;
  }

  public List<BuyEntity> listAll() {
    return store.values().stream()
        .sorted(
            Comparator.comparing(BuyEntity::isPinned).reversed()
                .thenComparing(BuyEntity::getUpdatedAt, Comparator.reverseOrder()))
        .toList();
  }

  public BuyEntity adminCreate(BuyPublishRequest request) {
    return publish(request);
  }

  public BuyEntity adminUpdate(String id, BuyAdminUpdateRequest request) {
    BuyEntity current = requireById(id);
    String normalizedStatus = request.status() == null ? null : normalizeStatus(request.status());
    String maskedPhone =
        request.contactPhone() == null ? null : maskPhone(request.contactPhone());
    current.update(
        request.title(),
        request.buyer(),
        request.category(),
        request.spec(),
        request.city(),
        request.budget(),
        request.arrival(),
        request.demand(),
        request.contactName(),
        maskedPhone,
        normalizedStatus,
        request.pinned());
    return current;
  }

  public BuyEntity adminToggleStatus(String id, String status) {
    BuyEntity current = requireById(id);
    String normalized = normalizeStatus(status);
    current.setStatus(normalized);
    return current;
  }

  public BuyEntity adminPin(String id, boolean pinned) {
    BuyEntity current = requireById(id);
    current.setPinned(pinned);
    return current;
  }

  public void adminDelete(String id) {
    BuyEntity removed = store.remove(id);
    if (removed == null) {
      throw new BaseException(ErrorCode.NOT_FOUND.getCode(), "求购记录不存在");
    }
  }

  private BuyEntity requireById(String id) {
    BuyEntity entity = store.get(id);
    if (entity == null) {
      throw new BaseException(ErrorCode.NOT_FOUND.getCode(), "求购记录不存在");
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

  private void seed() {
    addSeed(
        "B20260418001",
        "求购螺纹钢 HRB400E 16-25mm",
        "郑州中原钢材采购中心",
        "螺纹钢",
        "HRB400E 16-25mm",
        "600吨",
        "郑州",
        "3580元/吨",
        "3天内到货",
        "赵经理",
        "13800011234",
        STATUS_ONLINE,
        true,
        8);
    addSeed(
        "B20260418002",
        "求购热卷 Q235B 4.75*1500",
        "南京华东制造企业",
        "热卷",
        "Q235B 4.75*1500",
        "420吨",
        "南京",
        "3750元/吨",
        "7天内到货",
        "孙经理",
        "13900015678",
        STATUS_ONLINE,
        false,
        15);
    addSeed(
        "B20260418003",
        "求购中厚板 Q355B 20mm",
        "武汉桥梁工程项目部",
        "中厚板",
        "Q355B 20mm",
        "300吨",
        "武汉",
        "3920元/吨",
        "现货即提",
        "周经理",
        "13700016789",
        STATUS_ONLINE,
        false,
        19);
    addSeed(
        "B20260418004",
        "求购镀锌卷 DX51D 1.2mm",
        "佛山家电制造工厂",
        "管材",
        "DX51D 1.2mm",
        "260吨",
        "佛山",
        "4260元/吨",
        "7天内到货",
        "吴经理",
        "13600017890",
        STATUS_ONLINE,
        false,
        24);
    addSeed(
        "B20260418005",
        "求购H型钢 Q235B 200*200",
        "成都基建施工单位",
        "型钢",
        "Q235B 200*200",
        "450吨",
        "成都",
        "3850元/吨",
        "长期采购",
        "郑经理",
        "13500018901",
        STATUS_ONLINE,
        false,
        33);
  }

  private void addSeed(
      String id,
      String title,
      String buyer,
      String category,
      String spec,
      String demand,
      String city,
      String budget,
      String arrival,
      String contactName,
      String contactPhone,
      String status,
      boolean pinned,
      long minutesAgo) {
    seq.updateAndGet(v -> Math.max(v, parseNumericId(id)));
    BuyEntity entity =
        new BuyEntity(
            id,
            title,
            buyer,
            category,
            spec,
            city,
            budget,
            arrival,
            demand,
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
