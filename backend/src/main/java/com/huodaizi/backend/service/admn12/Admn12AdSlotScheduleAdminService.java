package com.huodaizi.backend.service.admn12;

import com.huodaizi.backend.dto.admn12.Admn12AdSlotScheduleDetailResponse;
import com.huodaizi.backend.dto.admn12.Admn12AdSlotScheduleListItemDTO;
import com.huodaizi.backend.dto.admn12.Admn12AdSlotScheduleListRequest;
import com.huodaizi.backend.dto.admn12.Admn12AdSlotScheduleListResponse;
import com.huodaizi.backend.dto.admn12.Admn12AdSlotScheduleUpsertRequest;
import com.huodaizi.backend.dto.admn12.Admn12AdSlotScheduleWindowDTO;
import com.huodaizi.backend.repository.auth.Admn12AdSlotScheduleEntity;
import com.huodaizi.backend.repository.auth.InMemoryAuthRepository;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class Admn12AdSlotScheduleAdminService {
  private final InMemoryAuthRepository repository;

  public Admn12AdSlotScheduleAdminService(InMemoryAuthRepository repository) {
    this.repository = repository;
  }

  public Admn12AdSlotScheduleListResponse list(Admn12AdSlotScheduleListRequest request) {
    int page = request == null ? 1 : request.safePage();
    int pageSize = request == null ? 10 : request.safePageSize();
    List<Admn12AdSlotScheduleEntity> all =
        repository.listAdSlotSchedulesForAdmin(
            request == null ? null : request.scheduleStatus(),
            request == null ? null : request.slotType(),
            request == null ? null : request.cityCode(),
            request == null ? null : request.keyword());
    int from = Math.min((page - 1) * pageSize, all.size());
    int to = Math.min(from + pageSize, all.size());
    List<Admn12AdSlotScheduleListItemDTO> records =
        all.subList(from, to).stream().map(this::toListItem).toList();
    int activeCount =
        (int)
            all.stream()
                .filter(item -> "ACTIVE".equalsIgnoreCase(safeText(item.getScheduleStatus())))
                .count();
    int draftCount =
        (int)
            all.stream()
                .filter(item -> "DRAFT".equalsIgnoreCase(safeText(item.getScheduleStatus())))
                .count();
    int pausedCount =
        (int)
            all.stream()
                .filter(item -> "PAUSED".equalsIgnoreCase(safeText(item.getScheduleStatus())))
                .count();
    int soldOutCount =
        (int)
            all.stream()
                .filter(item -> "SOLD".equalsIgnoreCase(safeText(item.getScheduleFillStatus())))
                .count();
    return new Admn12AdSlotScheduleListResponse(
        all.size(),
        page,
        pageSize,
        request == null ? "" : safeText(request.scheduleStatus()),
        request == null ? "" : safeText(request.slotType()),
        request == null ? "" : safeText(request.cityCode()),
        request == null ? "" : safeText(request.keyword()),
        activeCount,
        draftCount,
        pausedCount,
        soldOutCount,
        records);
  }

  public Admn12AdSlotScheduleDetailResponse detail(String scheduleId) {
    return toDetail(repository.getAdSlotScheduleForAdmin(scheduleId));
  }

  public Admn12AdSlotScheduleDetailResponse upsert(Admn12AdSlotScheduleUpsertRequest request) {
    List<Admn12AdSlotScheduleEntity.ScheduleWindow> windows =
        List.of(
            new Admn12AdSlotScheduleEntity.ScheduleWindow(
                safeText(request.startDate()),
                safeText(request.endDate()),
                safeText(request.scheduleStatus()),
                scheduleFillStatusText(request.scheduleFillStatus()),
                safeText(request.advertiserName())));
    Admn12AdSlotScheduleEntity entity =
        repository.upsertAdSlotScheduleForAdmin(
            request.slotCode(),
            request.slotName(),
            request.slotType(),
            request.cityCode(),
            request.cityName(),
            request.scheduleStatus(),
            request.scheduleFillStatus(),
            request.startDate(),
            request.endDate(),
            request.totalSlots(),
            request.soldSlots(),
            request.pricePerDay(),
            request.creativeUrl(),
            request.advertiserName(),
            request.campaignName(),
            request.operator(),
            request.remark(),
            windows);
    repository.appendAuditLogForAdmin(
        "ADMN12",
        "AD_SLOT_SCHEDULE_UPSERT",
        "AD_SLOT_SCHEDULE",
        entity.getScheduleId(),
        safeText(request.operator()),
        "ADMIN",
        "TRACE_ADMN12_UPSERT_" + entity.getScheduleId(),
        "SUCCESS",
        "ACTIVE".equalsIgnoreCase(entity.getScheduleStatus()) ? "MEDIUM" : "LOW",
        "广告位排期新增/更新：" + entity.getScheduleNo(),
        "",
        "slotType="
            + entity.getSlotType()
            + ", city="
            + entity.getCityCode()
            + ", status="
            + entity.getScheduleStatus(),
        "127.0.0.1",
        "admn12-service");
    return toDetail(entity);
  }

  public void appendAuditQueryLogForAdmin(String operator, String summary, String traceId, String targetId) {
    repository.appendAuditLogForAdmin(
        "ADMN12",
        "AD_SLOT_SCHEDULE_QUERY",
        "AD_SLOT_SCHEDULE",
        safeText(targetId).isBlank() ? "LIST" : targetId,
        operator,
        "ADMIN",
        traceId,
        "SUCCESS",
        "LOW",
        summary,
        "",
        "",
        "127.0.0.1",
        "admn12-service");
  }

  private Admn12AdSlotScheduleListItemDTO toListItem(Admn12AdSlotScheduleEntity entity) {
    return new Admn12AdSlotScheduleListItemDTO(
        entity.getScheduleId(),
        entity.getScheduleNo(),
        entity.getSlotId(),
        entity.getSlotName(),
        entity.getSlotType(),
        repository.admn12SlotTypeText(entity.getSlotType()),
        entity.getCityCode(),
        repository.admn12CityText(entity.getCityCode()),
        entity.getScheduleStatus(),
        repository.admn12ScheduleStatusText(entity.getScheduleStatus()),
        entity.getPricePerDay(),
        entity.getTotalSlots(),
        entity.getSoldSlots(),
        String.valueOf(entity.getRemainingSlots()),
        entity.getStartDate(),
        entity.getEndDate(),
        entity.getOperator(),
        toText(entity.getUpdatedAt()));
  }

  private Admn12AdSlotScheduleDetailResponse toDetail(Admn12AdSlotScheduleEntity entity) {
    List<Admn12AdSlotScheduleWindowDTO> windows =
        entity.getWindows().stream()
            .map(
                window ->
                    new Admn12AdSlotScheduleWindowDTO(
                        windowsIndexOf(entity.getWindows(), window),
                        window.startDate(),
                        window.endDate(),
                        window.windowStatus(),
                        window.windowStatusText(),
                        window.bookedBy()))
            .toList();
    return new Admn12AdSlotScheduleDetailResponse(
        entity.getScheduleId(),
        entity.getScheduleNo(),
        entity.getSlotCode(),
        entity.getSlotName(),
        entity.getSlotType(),
        repository.admn12SlotTypeText(entity.getSlotType()),
        entity.getCityCode(),
        repository.admn12CityText(entity.getCityCode()),
        entity.getScheduleStatus(),
        repository.admn12ScheduleStatusText(entity.getScheduleStatus()),
        entity.getScheduleFillStatus(),
        scheduleFillStatusText(entity.getScheduleFillStatus()),
        entity.getStartDate(),
        entity.getEndDate(),
        entity.getTotalSlots(),
        entity.getSoldSlots(),
        entity.getPricePerDay(),
        entity.getCreativeUrl(),
        entity.getAdvertiserName(),
        entity.getCampaignName(),
        entity.getOperator(),
        entity.getRemark(),
        toText(entity.getCreatedAt()),
        toText(entity.getUpdatedAt()),
        windows,
        availableActions(entity.getScheduleStatus()));
  }

  private int windowsIndexOf(
      List<Admn12AdSlotScheduleEntity.ScheduleWindow> windows, Admn12AdSlotScheduleEntity.ScheduleWindow target) {
    int idx = windows.indexOf(target);
    return idx < 0 ? 1 : idx + 1;
  }

  private String scheduleFillStatusText(String status) {
    return switch (safeText(status).toUpperCase()) {
      case "ON_SALE" -> "可售";
      case "PARTIAL" -> "部分售罄";
      case "SOLD" -> "已售罄";
      case "OFFLINE" -> "下线";
      default -> "其他";
    };
  }

  private List<String> availableActions(String status) {
    return switch (safeText(status).toUpperCase()) {
      case "ACTIVE" -> List.of("VIEW", "EDIT", "PAUSE");
      case "PAUSED" -> List.of("VIEW", "EDIT", "RESUME");
      case "DRAFT" -> List.of("VIEW", "EDIT", "PUBLISH");
      case "SOLD_OUT" -> List.of("VIEW", "EXTEND");
      default -> List.of("VIEW", "EDIT");
    };
  }

  private String safeText(String text) {
    return text == null ? "" : text.trim();
  }

  private String toText(LocalDateTime value) {
    return value == null ? "" : value.toString();
  }
}
