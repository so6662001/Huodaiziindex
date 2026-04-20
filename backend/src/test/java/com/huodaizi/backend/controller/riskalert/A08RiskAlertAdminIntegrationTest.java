package com.huodaizi.backend.controller.riskalert;

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
class A08RiskAlertAdminIntegrationTest {

  @Autowired private MockMvc mockMvc;

  @Test
  void listShouldRejectWhenMissingToken() throws Exception {
    mockMvc
        .perform(get("/api/admin/risk-alert/tasks"))
        .andExpect(status().isUnauthorized())
        .andExpect(jsonPath("$.code").value("UNAUTHORIZED"));
  }

  @Test
  void listShouldWorkWithValidToken() throws Exception {
    mockMvc
        .perform(
            get("/api/admin/risk-alert/tasks")
                .header("X-Admin-Token", "test-admin-token")
                .param("highOnly", "true")
                .param("page", "1")
                .param("pageSize", "20"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value("0"))
        .andExpect(jsonPath("$.data.overview.totalCount").isNumber())
        .andExpect(jsonPath("$.data.buckets").isArray())
        .andExpect(jsonPath("$.data.items").isArray());
  }

  @Test
  void batchStatusShouldWork() throws Exception {
    String firstAlertId =
        mockMvc
            .perform(
                get("/api/admin/risk-alert/tasks")
                    .header("X-Admin-Token", "test-admin-token")
                    .param("page", "1")
                    .param("pageSize", "1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value("0"))
            .andReturn()
            .getResponse()
            .getContentAsString();

    com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
    java.util.Map<?, ?> body = mapper.readValue(firstAlertId, java.util.Map.class);
    java.util.Map<?, ?> data = (java.util.Map<?, ?>) body.get("data");
    java.util.List<?> items = (java.util.List<?>) data.get("items");
    String alertId =
        String.valueOf(((java.util.Map<?, ?>) items.get(0)).get("alertId"));

    mockMvc
        .perform(
            put("/api/admin/risk-alert/tasks/status")
                .header("X-Admin-Token", "test-admin-token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                    {
                      "alertIds":["%s"],
                      "status":"RESOLVED",
                      "operator":"a08-admin",
                      "remark":"A08批量处理"
                    }
                    """
                        .formatted(alertId)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value("0"))
        .andExpect(jsonPath("$.data.items[0].status").value("RESOLVED"));
  }
}
