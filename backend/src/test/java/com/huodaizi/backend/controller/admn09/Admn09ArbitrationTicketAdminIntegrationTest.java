package com.huodaizi.backend.controller.admn09;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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
class Admn09ArbitrationTicketAdminIntegrationTest {

  @Autowired private MockMvc mockMvc;

  @Test
  void listShouldRejectWhenMissingToken() throws Exception {
    mockMvc
        .perform(get("/api/admin/arbitration-tickets"))
        .andExpect(status().isUnauthorized())
        .andExpect(jsonPath("$.code").value("UNAUTHORIZED"));
  }

  @Test
  void listDetailAssignReviewShouldWork() throws Exception {
    String listResp =
        mockMvc
            .perform(
                get("/api/admin/arbitration-tickets")
                    .header("X-Admin-Token", "test-admin-token")
                    .param("arbitrationStatus", "PENDING_ASSIGN")
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
            get("/api/admin/arbitration-tickets/{ticketId}", ticketId)
                .header("X-Admin-Token", "test-admin-token"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value("0"))
        .andExpect(jsonPath("$.data.ticketId").value(ticketId))
        .andExpect(jsonPath("$.data.availableActions").isArray());

    mockMvc
        .perform(
            put("/api/admin/arbitration-tickets/{ticketId}/assign", ticketId)
                .header("X-Admin-Token", "test-admin-token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                    {
                      "action":"ACCEPT",
                      "assignedArbitrator":"仲裁员-高阳",
                      "priorityLevel":"URGENT",
                      "handleRemark":"争议升级，优先安排当日处理",
                      "operator":"admn09-dispatcher"
                    }
                    """))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value("0"))
        .andExpect(jsonPath("$.data.ticketId").value(ticketId))
        .andExpect(jsonPath("$.data.arbitrationStatus").value("PROCESSING"))
        .andExpect(jsonPath("$.data.priorityLevel").value("URGENT"))
        .andExpect(jsonPath("$.data.assignedArbitrator").value("仲裁员-高阳"));

    mockMvc
        .perform(
            post("/api/admin/arbitration-tickets/{ticketId}/review", ticketId)
                .header("X-Admin-Token", "test-admin-token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                    {
                      "action":"SUPPORT_BUYER",
                      "resolutionSummary":"支持买方诉求，卖方承担延期损失",
                      "resolutionDetail":"裁决供应方承担滞车费并优先释放次日早班窗口",
                      "operator":"admn09-judge"
                    }
                    """))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value("0"))
        .andExpect(jsonPath("$.data.ticketId").value(ticketId))
        .andExpect(jsonPath("$.data.arbitrationStatus").value("RESOLVED"))
        .andExpect(jsonPath("$.data.latestRemark").value("裁决供应方承担滞车费并优先释放次日早班窗口"));

    mockMvc
        .perform(
            get("/api/admin/arbitration-tickets")
                .header("X-Admin-Token", "test-admin-token")
                .param("arbitrationStatus", "RESOLVED")
                .param("keyword", "支持买方诉求")
                .param("page", "1")
                .param("pageSize", "20"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value("0"))
        .andExpect(jsonPath("$.data.records.length()").value(Matchers.greaterThanOrEqualTo(1)));
  }
}
