package com.huodaizi.backend.repository.freight;

import com.huodaizi.backend.common.BaseException;
import com.huodaizi.backend.common.ErrorCode;
import com.huodaizi.backend.dto.freight.FreightAdminUpdateRequest;
import com.huodaizi.backend.dto.freight.FreightFilterOptions;
import com.huodaizi.backend.dto.freight.FreightFilterRequest;
import com.huodaizi.backend.dto.freight.FreightPublishRequest;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicLong;
import org.springframework.stereotype.Repository;

@Repository
public class InMemoryFreightRepository {

  private static final String STATUS_ONLINE = "ONLINE";
  private static final String STATUS_OFFLINE = "OFFLINE";

  private final AtomicLong seq = new AtomicLong(20260418000L);
  private final ConcurrentMap<String, FreightEntity> store = new ConcurrentHashMap<>();

  public InMemoryFreightRepository() {
    seed();
  }

  public FreightFilterOptions getFilterOptions() {
    return FreightFilterOptions.defaultOptions();
  }

  public List<FreightEntity> query(FreightFilterRequest request) {
    String origin = normalize(request.origin());
    String destination = normalize(request.destination());
    String vehicleType = normalize(request.vehicleType());
    String timeliness = normalize(request.timeliness());
    String returnTruck = normalize(request.returnTruck());
    String keyword = normalize(request.keyword());
    return store.values().stream()
        .filter(item -> STATUS_ONLINE.equals(item.getStatus()))
        .filter(
            item ->
                origin.isBlank()
                    || "全部起运地".equals(origin)
                    || normalize(item.getOrigin()).contains(origin))
        .filter(
            item ->
                destination.isBlank()
                    || "全部目的地".equals(destination)
                    || normalize(item.getDestination()).contains(destination))
        .filter(
            item ->
                vehicleType.isBlank()
                    || "全部车型".equals(vehicleType)
                    || normalize(item.getVehicleType()).contains(vehicleType))
        .filter(item -> matchTimeliness(item.getTimeliness(), timeliness))
        .filter(
            item ->
                returnTruck.isBlank()
                    || "不限".equals(returnTruck)
                    || ("有回程车".equals(returnTruck) && item.isReturnTruck())
                    || ("无回程车".equals(returnTruck) && !item.isReturnTruck()))
        .filter(
            item ->
                keyword.isBlank()
                    || normalize(item.getProvider()).contains(keyword)
                    || normalize(item.getRoute()).contains(keyword)
                    || normalize(item.getVehicleType()).contains(keyword))
        .sorted(
            Comparator.comparing(FreightEntity::isPinned).reversed()
                .thenComparing(FreightEntity::getUpdatedAt, Comparator.reverseOrder()))
        .toList();
  }

  public FreightEntity getById(String id) {
    FreightEntity entity = requireById(id);
    if (!STATUS_ONLINE.equals(entity.getStatus())) {
      throw new BaseException(ErrorCode.NOT_FOUND.getCode(), "车线信息不存在或已下线");
    }
    return entity;
  }

  public List<FreightEntity> listAll() {
    return store.values().stream()
        .sorted(
            Comparator.comparing(FreightEntity::isPinned).reversed()
                .thenComparing(FreightEntity::getUpdatedAt, Comparator.reverseOrder()))
        .toList();
  }

  public FreightEntity publish(FreightPublishRequest request) {
    String id = "F" + seq.incrementAndGet();
    FreightEntity entity =
        new FreightEntity(
            id,
            request.provider(),
            request.origin(),
            request.destination(),
            request.vehicleType(),
            request.loadRange(),
            request.frequency(),
            request.timelinessHours(),
            request.price(),
            request.returnTruck() != null && request.returnTruck(),
            defaultText(request.contactName(), "未公开"),
            maskPhone(request.contactPhone()),
            STATUS_ONLINE,
            false,
            LocalDateTime.now());
    store.put(id, entity);
    return entity;
  }

  public FreightEntity adminCreate(FreightPublishRequest request) {
    return publish(request);
  }

  public FreightEntity adminUpdate(String id, FreightAdminUpdateRequest request) {
    FreightEntity entity = requireById(id);
    String normalizedStatus = request.status() == null ? null : normalizeStatus(request.status());
    String maskedPhone = request.contactPhone() == null ? null : maskPhone(request.contactPhone());
    entity.update(
        request.provider(),
        request.origin(),
        request.destination(),
        request.vehicleType(),
        request.loadRange(),
        request.frequency(),
        request.timelinessHours(),
        request.price(),
        request.returnTruck(),
        request.contactName(),
        maskedPhone,
        normalizedStatus,
        request.pinned());
    return entity;
  }

  public FreightEntity adminChangeStatus(String id, String status) {
    FreightEntity entity = requireById(id);
    entity.setStatus(normalizeStatus(status));
    return entity;
  }

  public FreightEntity adminPin(String id, boolean pinned) {
    FreightEntity entity = requireById(id);
    entity.setPinned(pinned);
    return entity;
  }

  public void adminDelete(String id) {
    FreightEntity removed = store.remove(id);
    if (removed == null) {
      throw new BaseException(ErrorCode.NOT_FOUND.getCode(), "车线记录不存在");
    }
  }

  private FreightEntity requireById(String id) {
    FreightEntity entity = store.get(id);
    if (entity == null) {
      throw new BaseException(ErrorCode.NOT_FOUND.getCode(), "车线记录不存在");
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

  private boolean matchTimeliness(String timelinessValue, String timelinessFilter) {
    if (timelinessFilter.isBlank() || "全部时效".equals(timelinessFilter)) {
      return true;
    }
    int hour = parseHours(timelinessValue);
    if (hour < 0) {
      return true;
    }
    return switch (timelinessFilter) {
      case "24小时内" -> hour <= 24;
      case "48小时内" -> hour <= 48;
      case "72小时内" -> hour <= 72;
      case "72小时以上" -> hour > 72;
      default -> true;
    };
  }

  private int parseHours(String timelinessValue) {
    if (timelinessValue == null || timelinessValue.isBlank()) {
      return -1;
    }
    String digits = timelinessValue.replaceAll("[^0-9]", "");
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
        "F20260418001",
        "唐山宏运车队",
        "唐山",
        "无锡",
        "13米平板",
        "30-35吨",
        "每日发车",
        48,
        "120元/吨起",
        true,
        "赵经理",
        "13800031234",
        STATUS_ONLINE,
        true,
        6);
    addSeed(
        "F20260418002",
        "天津北方专线",
        "天津",
        "佛山",
        "17.5米平板",
        "35-40吨",
        "隔日发车",
        72,
        "165元/吨起",
        false,
        "钱经理",
        "13900035678",
        STATUS_ONLINE,
        false,
        10);
    addSeed(
        "F20260418003",
        "武汉联速物流",
        "武汉",
        "长沙",
        "13米高栏",
        "28-32吨",
        "每日发车",
        24,
        "98元/吨起",
        true,
        "孙经理",
        "13700036789",
        STATUS_ONLINE,
        false,
        14);
    addSeed(
        "F20260418004",
        "佛山华南车队",
        "佛山",
        "广州",
        "厢式货车",
        "18-22吨",
        "每日多班",
        12,
        "85元/吨起",
        false,
        "李经理",
        "13600037890",
        STATUS_ONLINE,
        false,
        19);
  }

  private void addSeed(
      String id,
      String provider,
      String origin,
      String destination,
      String vehicleType,
      String loadRange,
      String frequency,
      Integer timelinessHours,
      String price,
      boolean returnTruck,
      String contactName,
      String contactPhone,
      String status,
      boolean pinned,
      long minusMinutes) {
    seq.updateAndGet(v -> Math.max(v, parseNumericId(id)));
    FreightEntity entity =
        new FreightEntity(
            id,
            provider,
            origin,
            destination,
            vehicleType,
            loadRange,
            frequency,
            timelinessHours,
            price,
            returnTruck,
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
