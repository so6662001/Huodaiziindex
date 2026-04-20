package com.huodaizi.backend.controller.admn15;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.hamcrest.Matchers;
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
class Admn15RiskAlertTicketAdminIntegrationTest {

  @Autowired private MockMvc mockMvc;

  @Test
  void listShouldRejectWhenMissingToken() throws Exception {
    mockMvc
        .perform(get("/api/admin/risk-alert-tickets"))
        .andExpect(status().isUnauthorized())
        .andExpect(jsonPath("$.code").value("UNAUTHORIZED"));
  }

  @Test
  void listDetailAndHandleShouldWork() throws Exception {
    String listResp =
        mockMvc
            .perform(
                get("/api/admin/risk-alert-tickets")
                    .header("X-Admin-Token", "test-admin-token")
                    .param("ticketStatus", "OPEN")
                    .param("riskLevel", "HIGH")
                    .param("page", "1")
                    .param("pageSize", "20"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value("0"))
            .andExpect(jsonPath("$.data.records").isArray())
            .andExpect(jsonPath("$.data.pendingCount").isNumber())
            .andReturn()
            .getResponse()
            .getContentAsString();

    com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
    java.util.Map<?, ?> listBody = mapper.readValue(listResp, java.util.Map.class);
    java.util.Map<?, ?> listData = (java.util.Map<?, ?>) listBody.get("data");
    java.util.List<?> records = (java.util.List<?>) listData.get("records");
    String ticketId = String.valueOf(((java.util.Map<?, ?>) records.get(0)).get("ticketId"));

    mockMvc
        .perform(
            get("/api/admin/risk-alert-tickets/{ticketId}", ticketId)
                .header("X-Admin-Token", "test-admin-token"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value("0"))
        .andExpect(jsonPath("$.data.ticketId").value(ticketId))
        .andExpect(jsonPath("$.data.progressNodes").isArray())
        .andExpect(jsonPath("$.data.availableActions").isArray());

    mockMvc
        .perform(
            put("/api/admin/risk-alert-tickets/{ticketId}/handle", ticketId)
                .header("X-Admin-Token", "test-admin-token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                    {
                      "action":"START_PROCESS",
                      "targetStatus":"PROCESSING",
                      "owner":"风控值班组",
                      "solution":"已联系商家与调度，启动风险工单跟进",
                      "remark":"升级为P1，15分钟复核一次",
                      "operator":"admn15-operator"
                    }
                    """))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value("0"))
        .andExpect(jsonPath("$.data.ticketId").value(ticketId))
        .andExpect(jsonPath("$.data.ticketStatus").value("PROCESSING"))
        .andExpect(jsonPath("$.data.owner").value("风控值班组"));

    mockMvc
        .perform(
            put("/api/admin/risk-alert-tickets/{ticketId}/handle", ticketId)
                .header("X-Admin-Token", "test-admin-token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                    {
                      "action":"RESOLVE",
                      "targetStatus":"RESOLVED",
                      "owner":"风控值班组",
                      "solution":"风险已解除，完成归档",
                      "remark":"关闭预警并记录复盘结论",
                      "operator":"admn15-operator"
                    }
                    """))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value("0"))
        .andExpect(jsonPath("$.data.ticketId").value(ticketId))
        .andExpect(jsonPath("$.data.ticketStatus").value("RESOLVED"))
        .andExpect(jsonPath("$.data.latestRemark").value("关闭预警并记录复盘结论"));

    mockMvc
        .perform(
            get("/api/admin/risk-alert-tickets")
                .header("X-Admin-Token", "test-admin-token")
                .param("ticketStatus", "RESOLVED")
                .param("keyword", "关闭预警并记录复盘结论")
                .param("page", "1")
                .param("pageSize", "20"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value("0"))
        .andExpect(jsonPath("$.data.records.length()").value(Matchers.greaterThanOrEqualTo(1)));
  }
}
