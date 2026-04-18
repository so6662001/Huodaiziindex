package com.huodaizi.backend.repository.storagedemand;

import com.huodaizi.backend.common.BaseException;
import com.huodaizi.backend.common.ErrorCode;
import com.huodaizi.backend.dto.storagedemand.StorageDemandAdminUpdateRequest;
import com.huodaizi.backend.dto.storagedemand.StorageDemandFilterOptions;
import com.huodaizi.backend.dto.storagedemand.StorageDemandFilterRequest;
import com.huodaizi.backend.dto.storagedemand.StorageDemandPublishRequest;
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
    return StorageDemandFilterOptions.defaultOptions();
  }

  public List<StorageDemandEntity> query(StorageDemandFilterRequest request) {
    String city = normalize(request.city());
    String goodsCategory = normalize(request.goodsCategory());
    String serviceNeed = normalize(request.serviceNeed());
    String keyword = normalize(request.keyword());
    return store.values().stream()
        .filter(item -> STATUS_ONLINE.equals(item.getStatus()))
        .filter(
            item ->
                city.isBlank()
                    || "全部城市".equals(city)
                    || normalize(item.getCity()).contains(city))
        .filter(
            item ->
                goodsCategory.isBlank()
                    || "全部品类".equals(goodsCategory)
                    || normalize(item.getGoodsCategory()).contains(goodsCategory))
        .filter(item -> matchServiceNeed(item, serviceNeed))
        .filter(
            item ->
                keyword.isBlank()
                    || normalize(item.getTitle()).contains(keyword)
                    || normalize(item.getCompanyName()).contains(keyword)
                    || normalize(item.getRemark()).contains(keyword))
        .sorted(
            Comparator.comparing(StorageDemandEntity::isPinned).reversed()
                .thenComparing(StorageDemandEntity::getUpdatedAt, Comparator.reverseOrder()))
        .toList();
  }

  public StorageDemandEntity getById(String id) {
    StorageDemandEntity entity = requireById(id);
    if (!STATUS_ONLINE.equals(entity.getStatus())) {
      throw new BaseException(ErrorCode.NOT_FOUND.getCode(), "仓储需求不存在或已下线");
    }
    return entity;
  }

  public List<StorageDemandEntity> listAll() {
    return store.values().stream()
        .sorted(
            Comparator.comparing(StorageDemandEntity::isPinned).reversed()
                .thenComparing(StorageDemandEntity::getUpdatedAt, Comparator.reverseOrder()))
        .toList();
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
            request.needLoading() != null && request.needLoading(),
            request.needSorting() != null && request.needSorting(),
            defaultText(request.companyName(), "未填写"),
            defaultText(request.contactName(), "未公开"),
            maskPhone(request.contactPhone()),
            defaultText(request.remark(), "-"),
            STATUS_ONLINE,
            false,
            LocalDateTime.now());
    store.put(id, entity);
    return entity;
  }

  public StorageDemandEntity adminCreate(StorageDemandPublishRequest request) {
    return publish(request);
  }

  public StorageDemandEntity adminUpdate(String id, StorageDemandAdminUpdateRequest request) {
    StorageDemandEntity entity = requireById(id);
    String normalizedStatus = request.status() == null ? null : normalizeStatus(request.status());
    String maskedPhone = request.contactPhone() == null ? null : maskPhone(request.contactPhone());
    entity.update(
        request.title(),
        request.city(),
        request.goodsCategory(),
        request.tonnage(),
        request.storageDays(),
        request.inboundDate(),
        request.needLoading(),
        request.needSorting(),
        request.companyName(),
        request.contactName(),
        maskedPhone,
        request.remark(),
        normalizedStatus,
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
    StorageDemandEntity removed = store.remove(id);
    if (removed == null) {
      throw new BaseException(ErrorCode.NOT_FOUND.getCode(), "仓储需求记录不存在");
    }
  }

  private StorageDemandEntity requireById(String id) {
    StorageDemandEntity entity = store.get(id);
    if (entity == null) {
      throw new BaseException(ErrorCode.NOT_FOUND.getCode(), "仓储需求记录不存在");
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

  private boolean matchServiceNeed(StorageDemandEntity item, String serviceNeedFilter) {
    if (serviceNeedFilter.isBlank() || "全部服务".equals(serviceNeedFilter)) {
      return true;
    }
    String need = normalize(serviceNeedLabel(item.isNeedLoading(), item.isNeedSorting()));
    return need.equals(serviceNeedFilter);
  }

  private String serviceNeedLabel(boolean needLoading, boolean needSorting) {
    if (needLoading && needSorting) {
      return "装卸+分拣";
    }
    if (needLoading) {
      return "需要装卸";
    }
    if (needSorting) {
      return "需要分拣";
    }
    return "仅仓储";
  }

  private void seed() {
    addSeed(
        "SD20260418001",
        "唐山螺纹钢短期仓储需求，需2天内入库",
        "唐山",
        "螺纹钢",
        "300吨",
        "15天",
        "2026-04-20",
        true,
        false,
        "唐山某钢贸有限公司",
        "张经理",
        "13800011024",
        "需白天装卸，入库后分批提货",
        STATUS_ONLINE,
        true,
        8);
    addSeed(
        "SD20260418002",
        "佛山终端企业短期仓储需求，需夜间作业",
        "佛山",
        "热卷",
        "500吨",
        "20天",
        "2026-04-22",
        true,
        true,
        "佛山某制造集团",
        "李经理",
        "13900015620",
        "要求夜间可作业，支持短驳出库",
        STATUS_ONLINE,
        false,
        14);
    addSeed(
        "SD20260418003",
        "南京项目部求200吨中板仓储，需分批出库",
        "无锡",
        "中厚板",
        "200吨",
        "10天",
        "2026-04-19",
        false,
        true,
        "南京某工程项目部",
        "王工",
        "13700017834",
        "可接受露天场，但需分拣后出库",
        STATUS_ONLINE,
        false,
        22);
    addSeed(
        "SD20260418004",
        "武汉型钢仓储需求，需长期合作",
        "武汉",
        "型钢",
        "420吨",
        "30天",
        "2026-04-24",
        false,
        false,
        "武汉某商贸公司",
        "赵经理",
        "13600018976",
        "希望价格稳定，可月度结算",
        STATUS_ONLINE,
        false,
        30);
  }

  private void addSeed(
      String id,
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
      String status,
      boolean pinned,
      long minusMinutes) {
    seq.updateAndGet(v -> Math.max(v, parseNumericId(id)));
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
            companyName,
            contactName,
            maskPhone(contactPhone),
            remark,
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
