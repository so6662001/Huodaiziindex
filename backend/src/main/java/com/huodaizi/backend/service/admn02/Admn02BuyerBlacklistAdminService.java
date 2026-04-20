package com.huodaizi.backend.service.admn02;

import com.huodaizi.backend.dto.admn02.Admn02BuyerBlacklistUpdateRequest;
import com.huodaizi.backend.dto.admn02.Admn02BuyerDetailResponse;
import com.huodaizi.backend.dto.admn02.Admn02BuyerListItemDTO;
import com.huodaizi.backend.dto.admn02.Admn02BuyerListRequest;
import com.huodaizi.backend.dto.admn02.Admn02BuyerListResponse;
import com.huodaizi.backend.repository.auth.Admn02BuyerBlacklistRecordEntity;
import com.huodaizi.backend.repository.auth.AuthUserEntity;
import com.huodaizi.backend.repository.auth.InMemoryAuthRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import org.springframework.stereotype.Service;

@Service
public class Admn02BuyerBlacklistAdminService {
  private final InMemoryAuthRepository repository;

  public Admn02BuyerBlacklistAdminService(InMemoryAuthRepository repository) {
    this.repository = repository;
  }

  public Admn02BuyerListResponse list(Admn02BuyerListRequest request) {
    int page = request == null ? 1 : request.safePage();
    int pageSize = request == null ? 10 : request.safePageSize();
    List<AuthUserEntity> all =
        repository.listAllBuyersForAdmin(
            request == null ? null : request.accountStatus(),
            request == null ? null : request.blacklistStatus(),
            request == null ? null : request.keyword());
    int from = Math.min((page - 1) * pageSize, all.size());
    int to = Math.min(from + pageSize, all.size());
    List<Admn02BuyerListItemDTO> records =
        all.subList(from, to).stream().map(this::toListItem).toList();
    int blacklistedCount = (int) all.stream().filter(item -> isBlacklisted(item.getUserId())).count();
    return new Admn02BuyerListResponse(
        all.size(),
        page,
        pageSize,
        request == null ? "" : safeText(request.keyword()),
        request == null ? "" : safeText(request.accountStatus()),
        request == null ? "" : safeText(request.blacklistStatus()),
        blacklistedCount,
        all.size() - blacklistedCount,
        records);
  }

  public Admn02BuyerDetailResponse detail(String userId) {
    AuthUserEntity buyer = repository.getBuyerByIdForAdmin(userId);
    return toDetail(buyer);
  }

  public Admn02BuyerDetailResponse blacklist(String userId, Admn02BuyerBlacklistUpdateRequest request) {
    repository.updateBuyerBlacklistForAdmin(
        userId, request.action(), request.reasonCode(), request.remark(), request.operator());
    return detail(userId);
  }

  private Admn02BuyerListItemDTO toListItem(AuthUserEntity buyer) {
    boolean blacklisted = isBlacklisted(buyer.getUserId());
    String lastOrderAt = repository.latestOrderAtForBuyer(buyer.getUserId());
    return new Admn02BuyerListItemDTO(
        buyer.getUserId(),
        maskAccount(buyer.getAccount()),
        safeText(buyer.getCompanyName()),
        safeText(buyer.getContactName()),
        safeText(buyer.getPhoneMasked()),
        safeText(buyer.getRole()),
        safeText(buyer.getStatus()),
        accountStatusText(buyer.getStatus()),
        blacklisted,
        blacklisted ? "黑名单中" : "正常",
        riskLevel(blacklisted, lastOrderAt),
        lastOrderAt,
        toText(buyer.getCreatedAt()),
        toText(buyer.getUpdatedAt()));
  }

  private Admn02BuyerDetailResponse toDetail(AuthUserEntity buyer) {
    Optional<Admn02BuyerBlacklistRecordEntity> recordOpt = repository.getBuyerBlacklistRecord(buyer.getUserId());
    boolean blacklisted = recordOpt.map(Admn02BuyerBlacklistRecordEntity::isBlacklisted).orElse(false);
    String lastOrderAt = repository.latestOrderAtForBuyer(buyer.getUserId());
    return new Admn02BuyerDetailResponse(
        buyer.getUserId(),
        maskAccount(buyer.getAccount()),
        safeText(buyer.getCompanyName()),
        safeText(buyer.getContactName()),
        safeText(buyer.getPhoneMasked()),
        safeText(buyer.getRole()),
        safeText(buyer.getStatus()),
        accountStatusText(buyer.getStatus()),
        blacklisted,
        blacklisted ? "黑名单中" : "正常",
        riskLevel(blacklisted, lastOrderAt),
        recordOpt.map(Admn02BuyerBlacklistRecordEntity::getReasonCode).orElse(""),
        recordOpt.map(Admn02BuyerBlacklistRecordEntity::getRemark).orElse(""),
        recordOpt.map(Admn02BuyerBlacklistRecordEntity::getOperator).orElse(""),
        recordOpt.map(Admn02BuyerBlacklistRecordEntity::getOperateAt).map(this::toText).orElse(""),
        lastOrderAt,
        toText(buyer.getCreatedAt()),
        toText(buyer.getUpdatedAt()),
        availableActions(blacklisted));
  }

  private boolean isBlacklisted(String userId) {
    return repository.getBuyerBlacklistRecord(userId).map(Admn02BuyerBlacklistRecordEntity::isBlacklisted).orElse(false);
  }

  private List<String> availableActions(boolean blacklisted) {
    return blacklisted ? List.of("UNBLACKLIST") : List.of("BLACKLIST");
  }

  private String riskLevel(boolean blacklisted, String latestOrderAt) {
    if (blacklisted) {
      return "HIGH";
    }
    return safeText(latestOrderAt).isBlank() ? "MEDIUM" : "LOW";
  }

  private String accountStatusText(String status) {
    return switch (safeText(status).toUpperCase(Locale.ROOT)) {
      case "ACTIVE" -> "正常";
      case "DISABLED", "INACTIVE", "BANNED" -> "已禁用";
      default -> "未知";
    };
  }

  private String maskAccount(String account) {
    String digits = safeText(account).replaceAll("\\D", "");
    if (digits.length() == 11) {
      return repository.maskPhone(digits);
    }
    if (account == null || account.isBlank()) {
      return "";
    }
    if (account.length() <= 4) {
      return account;
    }
    return account.substring(0, 2) + "***" + account.substring(account.length() - 2);
  }

  private String safeText(String value) {
    return value == null ? "" : value.trim();
  }

  private String toText(LocalDateTime value) {
    return value == null ? "" : value.toString();
  }
}
