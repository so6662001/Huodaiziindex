package com.huodaizi.backend.controller.marketdetail;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class MarketDetailAuditIntegrationTest {

  @Autowired private MockMvc mockMvc;

  @Test
  void shouldReturnDetailForValidSymbolAndCity() throws Exception {
    mockMvc
        .perform(get("/api/v1/market-detail").param("symbol", "rebar").param("city", "tangshan"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value("0"))
        .andExpect(jsonPath("$.data.symbol").value("rebar"))
        .andExpect(jsonPath("$.data.city").value("tangshan"))
        .andExpect(jsonPath("$.data.quoteSummary.length()").value(3))
        .andExpect(jsonPath("$.data.relatedNews.length()").value(2));
  }

  @Test
  void shouldRejectAdminAccessWithoutToken() throws Exception {
    mockMvc
        .perform(get("/api/admin/market-detail/rebar/tangshan/SUMMARY"))
        .andExpect(status().isUnauthorized())
        .andExpect(jsonPath("$.code").value("UNAUTHORIZED"));
  }

  @Test
  void shouldAllowAdminAccessWithToken() throws Exception {
    mockMvc
        .perform(
            get("/api/admin/market-detail/rebar/tangshan/SUMMARY")
                .header("X-Admin-Token", "change-this-admin-token"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value("0"));
  }

  @Test
  void shouldReturnBadRequestForInvalidStatusUpdate() throws Exception {
    mockMvc
        .perform(
            put("/api/admin/market-detail/rebar/tangshan/SUMMARY/MD-rebar-tangshan-S1/status")
                .header("X-Admin-Token", "change-this-admin-token")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"status\":\"INVALID\"}"))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.code").value("BAD_REQUEST"));
  }
}
