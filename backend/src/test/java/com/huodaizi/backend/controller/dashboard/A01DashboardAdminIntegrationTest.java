package com.huodaizi.backend.controller.dashboard;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(
    properties = {
      "huodaizi.admin.auth.enabled=true",
      "huodaizi.admin.auth.token=test-admin-token"
    })
class A01DashboardAdminIntegrationTest {

  @Autowired private MockMvc mockMvc;

  @Test
  void dashboardShouldRejectWhenMissingToken() throws Exception {
    mockMvc
        .perform(get("/api/admin/dashboard/a01"))
        .andExpect(status().isUnauthorized())
        .andExpect(jsonPath("$.code").value("UNAUTHORIZED"));
  }

  @Test
  void dashboardShouldWorkWithValidToken() throws Exception {
    mockMvc
        .perform(
            get("/api/admin/dashboard/a01")
                .header("X-Admin-Token", "test-admin-token")
                .param("days", "7"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value("0"))
        .andExpect(jsonPath("$.data.overview.inquiryTotal").isNumber())
        .andExpect(jsonPath("$.data.funnel.inquiryToQuoteRate").isNotEmpty())
        .andExpect(jsonPath("$.data.trends").isArray())
        .andExpect(jsonPath("$.data.operations.reconcileDisputedCount").isNumber());
  }
}
