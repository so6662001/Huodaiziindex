package com.huodaizi.backend.repository.freightdemand;

import com.huodaizi.backend.common.BaseException;
import com.huodaizi.backend.common.ErrorCode;
import com.huodaizi.backend.dto.freightdemand.FreightDemandAdminCreateRequest;
import com.huodaizi.backend.dto.freightdemand.FreightDemandAdminUpdateRequest;
import com.huodaizi.backend.dto.freightdemand.FreightDemandFilterOptionsResponse;
import com.huodaizi.backend.dto.freightdemand.FreightDemandFilterRequest;
import com.huodaizi.backend.dto.freightdemand.FreightDemandPublishRequest;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicLong;
import org.springframework.stereotype.Repository;

@Repository
public class InMemoryFreightDemandRepository {

  private static final String STATUS_ONLINE = "ONLINE";
  private static final String STATUS_OFFLINE = "OFFLINE";

  private final AtomicLong seq = new AtomicLong(20260418000L);
  private final ConcurrentMap<String, FreightDemandEntity> store = new ConcurrentHashMap<>();

  public InMemoryFreightDemandRepository() {
    seed();
  }

  public List<FreightDemandEntity> listOnline(FreightDemandFilterRequest request) {
    String origin = normalize(request.originCity());
    String destination = normalize(request.destinationCity());
    String goods = normalize(request.goodsCategory());
    String vehicle = normalize(request.vehicleType());
    String keyword = normalize(request.keyword());
    String invoiceNeed = normalize(request.invoiceNeed());
    String loadingNeed = normalize(request.loadingNeed());
    return store.values().stream()
        .filter(item -> STATUS_ONLINE.equals(item.getStatus()))
        .filter(item -> origin.isBlank() || normalize(item.getOriginCity()).contains(origin))
        .filter(item -> destination.isBlank() || normalize(item.getDestinationCity()).contains(destination))
        .filter(item -> goods.isBlank() || normalize(item.getGoodsCategory()).contains(goods))
        .filter(item -> vehicle.isBlank() || normalize(item.getVehicleType()).contains(vehicle))
        .filter(item -> invoiceNeed.isBlank() || matchNeed(item.isNeedInvoice(), invoiceNeed))
        .filter(item -> loadingNeed.isBlank() || matchNeed(item.isNeedLoading(), loadingNeed))
        .filter(
            item ->
                keyword.isBlank()
                    || normalize(item.getTitle()).contains(keyword)
                    || normalize(item.getRemark()).contains(keyword))
        .sorted(
            Comparator.comparing(FreightDemandEntity::isPinned).reversed()
                .thenComparing(FreightDemandEntity::getUpdatedAt, Comparator.reverseOrder()))
        .toList();
  }

  public List<FreightDemandEntity> adminList() {
    return store.values().stream()
        .sorted(
            Comparator.comparing(FreightDemandEntity::isPinned).reversed()
                .thenComparing(FreightDemandEntity::getUpdatedAt, Comparator.reverseOrder()))
        .toList();
  }

  public FreightDemandFilterOptionsResponse filterOptions() {
    return new FreightDemandFilterOptionsResponse(
        List.of("唐山", "天津", "无锡", "佛山", "武汉", "广州", "长沙"),
        List.of("唐山", "天津", "无锡", "佛山", "武汉", "广州", "长沙"),
        List.of("螺纹钢", "热卷", "中厚板", "型钢", "管材", "其他"),
        List.of("13米平板", "17.5米平板", "13米高栏", "厢式货车", "不限车型"),
        List.of("24小时内", "48小时内", "72小时内", "一周内"),
        List.of("需开票", "不开票"),
        List.of("需装卸协同", "仅运输"));
  }

  public FreightDemandEntity publish(FreightDemandPublishRequest request) {
    String id = "FDM" + seq.incrementAndGet();
    FreightDemandEntity entity =
        new FreightDemandEntity(
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

  public FreightDemandEntity adminCreate(FreightDemandAdminCreateRequest request) {
    String id = "FDM" + seq.incrementAndGet();
    String status = request.status() == null ? STATUS_ONLINE : normalizeStatus(request.status());
    FreightDemandEntity entity =
        new FreightDemandEntity(
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
            request.contactName(),
            request.contactPhone(),
            request.companyName(),
            defaultText(request.remark(), "-"),
            status,
            request.pinned() != null && request.pinned(),
            LocalDateTime.now());
    store.put(id, entity);
    return entity;
  }

  public FreightDemandEntity adminUpdate(String id, FreightDemandAdminUpdateRequest request) {
    FreightDemandEntity entity = requireById(id);
    String normalizedStatus = request.status() == null ? null : normalizeStatus(request.status());
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
        request.contactName(),
        request.contactPhone(),
        request.companyName(),
        request.remark(),
        normalizedStatus,
        request.pinned());
    return entity;
  }

  public FreightDemandEntity adminChangeStatus(String id, String status) {
    FreightDemandEntity entity = requireById(id);
    entity.setStatus(normalizeStatus(status));
    return entity;
  }

  public FreightDemandEntity adminPin(String id, boolean pinned) {
    FreightDemandEntity entity = requireById(id);
    entity.setPinned(pinned);
    return entity;
  }

  public void adminDelete(String id) {
    FreightDemandEntity entity = requireById(id);
    store.remove(entity.getId());
  }

  private boolean matchNeed(boolean value, String filter) {
    return switch (filter) {
      case "yes", "true", "1", "需要", "需" -> value;
      case "no", "false", "0", "不需要", "无需" -> !value;
      default -> true;
    };
  }

  private FreightDemandEntity requireById(String id) {
    FreightDemandEntity entity = store.get(id);
    if (entity == null) {
      throw new BaseException(ErrorCode.NOT_FOUND.getCode(), "运输需求不存在");
    }
    return entity;
  }

  private String normalize(String text) {
    return text == null ? "" : text.trim().toLowerCase(Locale.ROOT);
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
    addSeed(
        "FDM20260418001",
        "唐山到无锡螺纹钢运输需求，48小时内到货",
        "唐山",
        "无锡",
        "螺纹钢",
        "32",
        "13米平板",
        "48小时内",
        "2026-04-20",
        true,
        false,
        "王先生",
        "13812347862",
        "唐山某钢贸有限公司",
        "要求到货后可回单",
        true,
        10);
    addSeed(
        "FDM20260418002",
        "佛山短途配送需求，次日达",
        "佛山",
        "广州",
        "热卷",
        "20",
        "厢式货车",
        "24小时内",
        "2026-04-19",
        false,
        true,
        "陈女士",
        "13956781234",
        "佛山某终端企业",
        "需配合装卸与多点卸货",
        false,
        8);
    addSeed(
        "FDM20260418003",
        "武汉到长沙型钢运输需求",
        "武汉",
        "长沙",
        "型钢",
        "28",
        "13米高栏",
        "72小时内",
        "2026-04-21",
        true,
        false,
        "刘先生",
        "13722334455",
        "武汉某物流公司",
        "可接受夜间装车",
        false,
        6);
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
      String contactName,
      String contactPhone,
      String companyName,
      String remark,
      boolean pinned,
      long minusMinutes) {
    seq.updateAndGet(v -> Math.max(v, parseNumericId(id)));
    FreightDemandEntity entity =
        new FreightDemandEntity(
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
            contactName,
            contactPhone,
            companyName,
            remark,
            STATUS_ONLINE,
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
