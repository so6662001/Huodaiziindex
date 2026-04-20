package com.huodaizi.backend.service.identity;

import com.huodaizi.backend.dto.identity.N02IdentityListResponse;
import com.huodaizi.backend.dto.identity.N02IdentityOptionDTO;
import com.huodaizi.backend.dto.identity.H5N02IdentityListResponse;
import com.huodaizi.backend.dto.identity.H5N02IdentityOptionDTO;
import com.huodaizi.backend.dto.identity.H5N02IdentitySwitchRequest;
import com.huodaizi.backend.dto.identity.H5N02IdentitySwitchResponse;
import com.huodaizi.backend.dto.identity.N02IdentitySwitchRequest;
import com.huodaizi.backend.dto.identity.N02IdentitySwitchResponse;
import com.huodaizi.backend.repository.auth.AuthUserEntity;
import com.huodaizi.backend.repository.auth.InMemoryAuthRepository;
import com.huodaizi.backend.repository.auth.InMemoryAuthRepository.SessionEntity;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class N02IdentityService {
  private final InMemoryAuthRepository repository;

  public N02IdentityService(InMemoryAuthRepository repository) {
    this.repository = repository;
  }

  public N02IdentityListResponse list(String token) {
    SessionEntity session = repository.requireSession(token);
    AuthUserEntity user = repository.findByToken(token).orElseThrow();
    String currentIdentityCode = session.getActiveIdentityCode();
    List<N02IdentityOptionDTO> identities =
        List.of(
            new N02IdentityOptionDTO(
                "BUYER", "采购方", "发起询价、比较报价、确认成交", "/inquiry/create", "BUYER".equalsIgnoreCase(currentIdentityCode), true, true),
            new N02IdentityOptionDTO(
                "SUPPLIER",
                "供应方",
                "管理线索、快速报价、提货履约协同",
                "/merchant/quote/workbench",
                "SUPPLIER".equalsIgnoreCase(currentIdentityCode),
                false,
                true),
            new N02IdentityOptionDTO(
                "OPERATOR",
                "运营方",
                "监控风险、分发策略、经营看板与配置",
                "/admin/dashboard/a01",
                "OPERATOR".equalsIgnoreCase(currentIdentityCode),
                false,
                true));
    return new N02IdentityListResponse(
        user.getUserId(),
        user.getAccount(),
        user.getCompanyName(),
        session.getDefaultRoleCode(),
        currentIdentityCode,
        roleName(currentIdentityCode),
        identities,
        session.getExpireAt().toString());
  }

  public N02IdentitySwitchResponse switchIdentity(String token, N02IdentitySwitchRequest request) {
    SessionEntity updated = repository.switchIdentity(token, request.identityCode());
    return new N02IdentitySwitchResponse(
        updated.getUserId(),
        updated.getAccount(),
        updated.getDefaultRoleCode(),
        updated.getActiveIdentityCode(),
        roleName(updated.getActiveIdentityCode()),
        updated.getUpdatedAt().toString(),
        updated.getToken(),
        updated.getExpireAt().toString(),
        "身份已切换为 " + updated.getActiveIdentityCode());
  }

  public H5N02IdentityListResponse h5List(String token) {
    SessionEntity session = repository.requireSession(token);
    AuthUserEntity user = repository.findByToken(token).orElseThrow();
    String currentIdentityCode = session.getActiveIdentityCode();
    String channel = session.getChannel();
    java.util.List<H5N02IdentityOptionDTO> identities =
        java.util.List.of(
            new H5N02IdentityOptionDTO(
                "BUYER",
                "采购方",
                "我要买货，快速询价下单",
                "/h5/inquiry/step1",
                "BUYER".equalsIgnoreCase(currentIdentityCode),
                true,
                true),
            new H5N02IdentityOptionDTO(
                "SUPPLIER",
                "供应方",
                "我要卖货，管理线索报价",
                "/h5/merchant/leads?merchantId=S001",
                "SUPPLIER".equalsIgnoreCase(currentIdentityCode),
                false,
                true),
            new H5N02IdentityOptionDTO(
                "OPERATOR",
                "运营方",
                "平台运营与风控看板",
                "/admin/dashboard/a01",
                "OPERATOR".equalsIgnoreCase(currentIdentityCode),
                false,
                true));
    return new H5N02IdentityListResponse(
        user.getUserId(),
        user.getAccount(),
        user.getCompanyName(),
        currentIdentityCode,
        roleName(currentIdentityCode),
        identities,
        channel,
        session.getExpireAt().toString());
  }

  public H5N02IdentitySwitchResponse h5SwitchIdentity(String token, H5N02IdentitySwitchRequest request) {
    SessionEntity updated = repository.switchIdentity(token, request.identityCode());
    return new H5N02IdentitySwitchResponse(
        updated.getUserId(),
        updated.getAccount(),
        updated.getActiveIdentityCode(),
        roleName(updated.getActiveIdentityCode()),
        updated.getChannel(),
        updated.getUpdatedAt().toString(),
        updated.getToken(),
        updated.getExpireAt().toString(),
        "H5身份已切换为 " + updated.getActiveIdentityCode());
  }

  private String roleName(String code) {
    return switch (code) {
      case "BUYER" -> "采购方";
      case "SUPPLIER" -> "供应方";
      case "OPERATOR" -> "运营方";
      default -> code;
    };
  }
}
