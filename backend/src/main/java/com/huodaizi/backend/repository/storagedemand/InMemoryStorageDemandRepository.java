package com.huodaizi.backend.repository.storagedemand;

import com.huodaizi.backend.common.BaseException;
import com.huodaizi.backend.common.ErrorCode;
import com.huodaizi.backend.dto.storagedemand.StorageDemandAdminUpdateRequest;
import com.huodaizi.backend.dto.storagedemand.StorageDemandFilterOptions;
import com.huodaizi.backend.dto.storagedemand.StorageDemandFilterRequest;
import com.huodaizi.backend.dto.storagedemand.StorageDemandPublishRequest;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicLong;
import org.springframework.stereotype.Repository;

@Repository
public class InMemoryStorageDemandRepository {

  private static final String STATUS_ONLINE = "ONLINE";
  private static final String STATUS_OFFLINE = "OFFLINE";

  private final AtomicLong seq = new AtomicLong(20260418000L);
  private final ConcurrentMap<String, StorageDemandEntity> store = new ConcurrentHashMap<>();

  public InMemoryStorageDemandRepository() {
    seed();
  }

  public StorageDemandFilterOptions getFilterOptions() {
    return new StorageDemandFilterOptions(
        List.of("唐山", "天津", "无锡", "佛山", "武汉", "成都"),
        List.of("螺纹钢", "热卷", "中厚板", "型钢", "管材", "其他"),
        List.of("仅仓储", "需要装卸", "需要分拣", "装卸+分拣"));
  }

  public List<StorageDemandEntity> query(StorageDemandFilterRequest request) {
    String city = normalize(request.city());
    String goodsCategory = normalize(request.goodsCategory());
    String serviceNeed = normalize(request.serviceNeed());
    String keyword = normalize(request.keyword());
    return store.values().stream()
        .filter(item -> STATUS_ONLINE.equals(item.getStatus()))
        .filter(item -> city == null || item.getCity().contains(city))
        .filter(item -> goodsCategory == null || item.getGoodsCategory().contains(goodsCategory))
        .filter(item -> serviceNeed == null || serviceNeedLabel(item).contains(serviceNeed))
        .filter(item -> keyword == null || matchKeyword(item, keyword))
        .sorted(
            Comparator.comparing(StorageDemandEntity::isPinned).reversed()
                .thenComparing(StorageDemandEntity::getUpdatedAt, Comparator.reverseOrder()))
        .toList();
  }

  public StorageDemandEntity getById(String id) {
    StorageDemandEntity entity = store.get(id);
    if (entity == null || !STATUS_ONLINE.equals(entity.getStatus())) {
      throw new BaseException(ErrorCode.NOT_FOUND.getCode(), "仓储需求不存在");
    }
    return entity;
  }

  public StorageDemandEntity publish(StorageDemandPublishRequest request) {
    String id = "SD" + seq.incrementAndGet();
    StorageDemandEntity entity =
        new StorageDemandEntity(
            id,
            request.title(),
            request.city(),
            request.goodsCategory(),
            request.tonnage(),
            request.storageDays(),
            request.inboundDate(),
            bool(request.needLoading()),
            bool(request.needSorting()),
            request.contactName(),
            request.contactPhone(),
            request.companyName(),
            defaultText(request.remark(), "-"),
            STATUS_ONLINE,
            false,
            LocalDateTime.now());
    store.put(id, entity);
    return entity;
  }

  public List<StorageDemandEntity> listAll() {
    return store.values().stream()
        .sorted(
            Comparator.comparing(StorageDemandEntity::isPinned).reversed()
                .thenComparing(StorageDemandEntity::getUpdatedAt, Comparator.reverseOrder()))
        .toList();
  }

  public StorageDemandEntity adminCreate(StorageDemandPublishRequest request) {
    return publish(request);
  }

  public StorageDemandEntity adminUpdate(String id, StorageDemandAdminUpdateRequest request) {
    StorageDemandEntity entity = requireById(id);
    String status = request.status() == null ? null : normalizeStatus(request.status());
    entity.update(
        request.title(),
        request.city(),
        request.goodsCategory(),
        request.tonnage(),
        request.storageDays(),
        request.inboundDate(),
        request.needLoading(),
        request.needSorting(),
        request.contactName(),
        request.contactPhone(),
        request.companyName(),
        request.remark(),
        status,
        request.pinned());
    return entity;
  }

  public StorageDemandEntity adminChangeStatus(String id, String status) {
    StorageDemandEntity entity = requireById(id);
    entity.setStatus(normalizeStatus(status));
    return entity;
  }

  public StorageDemandEntity adminPin(String id, boolean pinned) {
    StorageDemandEntity entity = requireById(id);
    entity.setPinned(pinned);
    return entity;
  }

  public void adminDelete(String id) {
    StorageDemandEntity entity = requireById(id);
    store.remove(entity.getId());
  }

  private StorageDemandEntity requireById(String id) {
    StorageDemandEntity entity = store.get(id);
    if (entity == null) {
      throw new BaseException(ErrorCode.NOT_FOUND.getCode(), "仓储需求不存在");
    }
    return entity;
  }

  private boolean bool(Boolean value) {
    return value != null && value;
  }

  private String defaultText(String text, String fallback) {
    return text == null || text.isBlank() ? fallback : text;
  }

  private String normalize(String text) {
    if (text == null || text.isBlank()) {
      return null;
    }
    return text.trim();
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

  private boolean matchKeyword(StorageDemandEntity item, String keyword) {
    String kw = keyword.toLowerCase(Locale.ROOT);
    return item.getTitle().toLowerCase(Locale.ROOT).contains(kw)
        || item.getCity().toLowerCase(Locale.ROOT).contains(kw)
        || item.getGoodsCategory().toLowerCase(Locale.ROOT).contains(kw)
        || item.getCompanyName().toLowerCase(Locale.ROOT).contains(kw);
  }

  private String serviceNeedLabel(StorageDemandEntity entity) {
    if (entity.isNeedLoading() && entity.isNeedSorting()) {
      return "装卸+分拣";
    }
    if (entity.isNeedLoading()) {
      return "需要装卸";
    }
    if (entity.isNeedSorting()) {
      return "需要分拣";
    }
    return "仅仓储";
  }

  private void seed() {
    addSeed(
        "唐山螺纹钢短期仓储需求",
        "唐山",
        "螺纹钢",
        "300",
        "15",
        LocalDate.now().plusDays(2).toString(),
        true,
        false,
        "唐山钢贸集团",
        "王经理",
        "13800138000",
        "需白天入库",
        true,
        4);
    addSeed(
        "无锡热卷项目仓储需求",
        "无锡",
        "热卷",
        "500",
        "30",
        LocalDate.now().plusDays(1).toString(),
        true,
        true,
        "无锡板材贸易",
        "李总",
        "13900139000",
        "需要分拣打包",
        false,
        3);
    addSeed(
        "佛山型钢周转仓储",
        "佛山",
        "型钢",
        "200",
        "10",
        LocalDate.now().plusDays(3).toString(),
        false,
        true,
        "佛山物流供应链",
        "陈先生",
        "13700137000",
        "可夜间作业优先",
        false,
        2);
  }

  private void addSeed(
      String title,
      String city,
      String goodsCategory,
      String tonnage,
      String storageDays,
      String inboundDate,
      boolean needLoading,
      boolean needSorting,
      String companyName,
      String contactName,
      String contactPhone,
      String remark,
      boolean pinned,
      long minusHours) {
    String id = "SD" + seq.incrementAndGet();
    StorageDemandEntity entity =
        new StorageDemandEntity(
            id,
            title,
            city,
            goodsCategory,
            tonnage,
            storageDays,
            inboundDate,
            needLoading,
            needSorting,
            contactName,
            contactPhone,
            companyName,
            remark,
            STATUS_ONLINE,
            pinned,
            LocalDateTime.now().minusHours(minusHours));
    store.put(id, entity);
  }
}
