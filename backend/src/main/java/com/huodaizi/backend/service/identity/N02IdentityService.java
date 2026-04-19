package com.huodaizi.backend.service.identity;

import com.huodaizi.backend.dto.identity.N02IdentityListResponse;
import com.huodaizi.backend.dto.identity.N02IdentityOptionDTO;
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

  private String roleName(String code) {
    return switch (code) {
      case "BUYER" -> "采购方";
      case "SUPPLIER" -> "供应方";
      case "OPERATOR" -> "运营方";
      default -> code;
    };
  }
}
