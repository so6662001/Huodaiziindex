package com.huodaizi.backend.controller.admn08;

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
class Admn08DealFunnelAdminIntegrationTest {

  @Autowired private MockMvc mockMvc;

  @Test
  void funnelShouldRejectWhenMissingToken() throws Exception {
    mockMvc
        .perform(get("/api/admin/deal-funnel"))
        .andExpect(status().isUnauthorized())
        .andExpect(jsonPath("$.code").value("UNAUTHORIZED"));
  }

  @Test
  void funnelShouldWorkWithValidToken() throws Exception {
    mockMvc
        .perform(
            get("/api/admin/deal-funnel")
                .header("X-Admin-Token", "test-admin-token")
                .param("days", "30")
                .param("city", "唐山"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value("0"))
        .andExpect(jsonPath("$.data.windowDays").value(30))
        .andExpect(jsonPath("$.data.city").value("唐山"))
        .andExpect(jsonPath("$.data.inquiryCount").isNumber())
        .andExpect(jsonPath("$.data.paidCount").isNumber())
        .andExpect(jsonPath("$.data.nodes").isArray())
        .andExpect(jsonPath("$.data.nodes.length()").value(6))
        .andExpect(jsonPath("$.data.trends").isArray())
        .andExpect(jsonPath("$.data.insights").isArray());
  }
}
