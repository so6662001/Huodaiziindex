package com.huodaizi.backend.controller.admn02;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(
    properties = {
      "huodaizi.admin.auth.enabled=true",
      "huodaizi.admin.auth.token=test-admin-token"
    })
class Admn02BuyerBlacklistAdminIntegrationTest {

  @Autowired private MockMvc mockMvc;

  @Test
  void listShouldRejectWhenMissingToken() throws Exception {
    mockMvc
        .perform(get("/api/admin/buyers"))
        .andExpect(status().isUnauthorized())
        .andExpect(jsonPath("$.code").value("UNAUTHORIZED"));
  }

  @Test
  void listDetailAndBlacklistFlowShouldWork() throws Exception {
    String userId = "U000000000021";

    mockMvc
        .perform(
            get("/api/admin/buyers")
                .header("X-Admin-Token", "test-admin-token")
                .param("blacklistStatus", "NORMAL")
                .param("page", "1")
                .param("pageSize", "20"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value("0"))
        .andExpect(jsonPath("$.data.records").isArray())
        .andExpect(jsonPath("$.data.normalCount").isNumber())
        .andExpect(jsonPath("$.data.blacklistedCount").isNumber());

    mockMvc
        .perform(get("/api/admin/buyers/{userId}", userId).header("X-Admin-Token", "test-admin-token"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value("0"))
        .andExpect(jsonPath("$.data.userId").value(userId))
        .andExpect(jsonPath("$.data.blacklisted").value(false));

    mockMvc
        .perform(
            put("/api/admin/buyers/{userId}/blacklist", userId)
                .header("X-Admin-Token", "test-admin-token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                    {
                      "action":"BLACKLIST",
                      "reasonCode":"RISK_CONTROL",
                      "remark":"测试加入黑名单",
                      "operator":"admn02-admin"
                    }
                    """))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value("0"))
        .andExpect(jsonPath("$.data.userId").value(userId))
        .andExpect(jsonPath("$.data.blacklisted").value(true))
        .andExpect(jsonPath("$.data.latestBlacklistReason").value("RISK_CONTROL"));

    mockMvc
        .perform(
            put("/api/admin/buyers/{userId}/blacklist", userId)
                .header("X-Admin-Token", "test-admin-token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                    {
                      "action":"UNBLACKLIST",
                      "reasonCode":"RECOVERED",
                      "remark":"测试解除黑名单",
                      "operator":"admn02-admin"
                    }
                    """))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value("0"))
        .andExpect(jsonPath("$.data.userId").value(userId))
        .andExpect(jsonPath("$.data.blacklisted").value(false))
        .andExpect(jsonPath("$.data.latestBlacklistReason").value("RECOVERED"));
  }
}
