package com.huodaizi.backend.repository.transportdemand;

import com.huodaizi.backend.common.BaseException;
import com.huodaizi.backend.common.ErrorCode;
import com.huodaizi.backend.dto.transportdemand.TransportDemandAdminUpdateRequest;
import com.huodaizi.backend.dto.transportdemand.TransportDemandFilterOptions;
import com.huodaizi.backend.dto.transportdemand.TransportDemandFilterRequest;
import com.huodaizi.backend.dto.transportdemand.TransportDemandPublishRequest;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicLong;
import org.springframework.stereotype.Repository;

@Repository
public class InMemoryTransportDemandRepository {

  private static final String STATUS_ONLINE = "ONLINE";
  private static final String STATUS_OFFLINE = "OFFLINE";

  private final AtomicLong seq = new AtomicLong(20260418000L);
  private final ConcurrentMap<String, TransportDemandEntity> store = new ConcurrentHashMap<>();

  public InMemoryTransportDemandRepository() {
    seed();
  }

  public TransportDemandFilterOptions getFilterOptions() {
    return TransportDemandFilterOptions.defaultOptions();
  }

  public List<TransportDemandEntity> query(TransportDemandFilterRequest request) {
    String origin = normalize(request.originCity());
    String destination = normalize(request.destinationCity());
    String goodsCategory = normalize(request.goodsCategory());
    String vehicleType = normalize(request.vehicleType());
    String timeliness = normalize(request.timeliness());
    String invoiceNeed = normalize(request.invoiceNeed());
    String keyword = normalize(request.keyword());
    return store.values().stream()
        .filter(item -> STATUS_ONLINE.equals(item.getStatus()))
        .filter(
            item ->
                origin.isBlank()
                    || "全部起运地".equals(origin)
                    || normalize(item.getOriginCity()).contains(origin))
        .filter(
            item ->
                destination.isBlank()
                    || "全部目的地".equals(destination)
                    || normalize(item.getDestinationCity()).contains(destination))
        .filter(
            item ->
                goodsCategory.isBlank()
                    || "全部品类".equals(goodsCategory)
                    || normalize(item.getGoodsCategory()).contains(goodsCategory))
        .filter(
            item ->
                vehicleType.isBlank()
                    || "全部车型".equals(vehicleType)
                    || normalize(item.getVehicleType()).contains(vehicleType))
        .filter(
            item ->
                timeliness.isBlank()
                    || "全部时效".equals(timeliness)
                    || normalize(item.getTimeliness()).contains(timeliness))
        .filter(
            item ->
                invoiceNeed.isBlank()
                    || "全部票据".equals(invoiceNeed)
                    || ("需要开票".equals(invoiceNeed) && item.isNeedInvoice())
                    || ("无需开票".equals(invoiceNeed) && !item.isNeedInvoice()))
        .filter(
            item ->
                keyword.isBlank()
                    || normalize(item.getTitle()).contains(keyword)
                    || normalize(item.getRoute()).contains(keyword)
                    || normalize(item.getCompanyName()).contains(keyword)
                    || normalize(item.getRemark()).contains(keyword))
        .sorted(
            Comparator.comparing(TransportDemandEntity::isPinned).reversed()
                .thenComparing(TransportDemandEntity::getUpdatedAt, Comparator.reverseOrder()))
        .toList();
  }

  public TransportDemandEntity getById(String id) {
    TransportDemandEntity entity = requireById(id);
    if (!STATUS_ONLINE.equals(entity.getStatus())) {
      throw new BaseException(ErrorCode.NOT_FOUND.getCode(), "运输需求不存在或已下线");
    }
    return entity;
  }

  public List<TransportDemandEntity> listAll() {
    return store.values().stream()
        .sorted(
            Comparator.comparing(TransportDemandEntity::isPinned).reversed()
                .thenComparing(TransportDemandEntity::getUpdatedAt, Comparator.reverseOrder()))
        .toList();
  }

  public TransportDemandEntity publish(TransportDemandPublishRequest request) {
    String id = "TD" + seq.incrementAndGet();
    TransportDemandEntity entity =
        new TransportDemandEntity(
            id,
            request.title(),
            request.originCity(),
            request.destinationCity(),
            request.goodsCategory(),
            request.tonnage(),
            request.vehicleType(),
            request.timeliness(),
            request.loadDate(),
            request.needInvoice() != null && request.needInvoice(),
            request.needLoading() != null && request.needLoading(),
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

  public TransportDemandEntity adminCreate(TransportDemandPublishRequest request) {
    return publish(request);
  }

  public TransportDemandEntity adminUpdate(String id, TransportDemandAdminUpdateRequest request) {
    TransportDemandEntity entity = requireById(id);
    String normalizedStatus = request.status() == null ? null : normalizeStatus(request.status());
    String maskedPhone = request.contactPhone() == null ? null : maskPhone(request.contactPhone());
    entity.update(
        request.title(),
        request.originCity(),
        request.destinationCity(),
        request.goodsCategory(),
        request.tonnage(),
        request.vehicleType(),
        request.timeliness(),
        request.loadDate(),
        request.needInvoice(),
        request.needLoading(),
        request.companyName(),
        request.contactName(),
        maskedPhone,
        request.remark(),
        normalizedStatus,
        request.pinned());
    return entity;
  }

  public TransportDemandEntity adminChangeStatus(String id, String status) {
    TransportDemandEntity entity = requireById(id);
    entity.setStatus(normalizeStatus(status));
    return entity;
  }

  public TransportDemandEntity adminPin(String id, boolean pinned) {
    TransportDemandEntity entity = requireById(id);
    entity.setPinned(pinned);
    return entity;
  }

  public void adminDelete(String id) {
    TransportDemandEntity removed = store.remove(id);
    if (removed == null) {
      throw new BaseException(ErrorCode.NOT_FOUND.getCode(), "运输需求记录不存在");
    }
  }

  private TransportDemandEntity requireById(String id) {
    TransportDemandEntity entity = store.get(id);
    if (entity == null) {
      throw new BaseException(ErrorCode.NOT_FOUND.getCode(), "运输需求记录不存在");
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

  private String maskContactName(String name) {
    if (name == null || name.isBlank() || "未公开".equals(name)) {
      return "未公开";
    }
    String trimmed = name.trim();
    if (trimmed.length() == 1) {
      return "*";
    }
    return trimmed.substring(0, 1) + "**";
  }

  private void seed() {
    addSeed(
        "TD20260418001",
        "唐山到无锡螺纹钢运输需求（260吨）",
        "唐山",
        "无锡",
        "螺纹钢",
        "260吨",
        "13米平板",
        "48小时内",
        "2026-04-20",
        true,
        false,
        "唐山某钢贸有限公司",
        "王经理",
        "13700027834",
        "要求中途可追踪，签收需回单",
        STATUS_ONLINE,
        true,
        8);
    addSeed(
        "TD20260418002",
        "天津到广州热卷运输需求（180吨）",
        "天津",
        "广州",
        "热卷",
        "180吨",
        "17.5米平板",
        "72小时内",
        "2026-04-22",
        false,
        true,
        "天津某贸易公司",
        "李经理",
        "13900025620",
        "需现场协同装货，优先整车报价",
        STATUS_ONLINE,
        false,
        14);
    addSeed(
        "TD20260418003",
        "武汉到长沙型钢运输需求（120吨）",
        "武汉",
        "长沙",
        "型钢",
        "120吨",
        "13米高栏",
        "24小时内",
        "2026-04-19",
        true,
        false,
        "武汉某项目部",
        "赵工",
        "13600018976",
        "要求次日发车，需开票",
        STATUS_ONLINE,
        false,
        22);
    addSeed(
        "TD20260418004",
        "佛山到成都中板运输需求（210吨）",
        "佛山",
        "成都",
        "中厚板",
        "210吨",
        "不限车型",
        "一周内",
        "2026-04-25",
        false,
        false,
        "佛山某制造集团",
        "陈经理",
        "13800011024",
        "可分批发运，接受返程车",
        STATUS_ONLINE,
        false,
        30);
  }

  private void addSeed(
      String id,
      String title,
      String originCity,
      String destinationCity,
      String goodsCategory,
      String tonnage,
      String vehicleType,
      String timeliness,
      String loadDate,
      boolean needInvoice,
      boolean needLoading,
      String companyName,
      String contactName,
      String contactPhone,
      String remark,
      String status,
      boolean pinned,
      long minusMinutes) {
    seq.updateAndGet(v -> Math.max(v, parseNumericId(id)));
    TransportDemandEntity entity =
        new TransportDemandEntity(
            id,
            title,
            originCity,
            destinationCity,
            goodsCategory,
            tonnage,
            vehicleType,
            timeliness,
            loadDate,
            needInvoice,
            needLoading,
            companyName,
            maskContactName(contactName),
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
