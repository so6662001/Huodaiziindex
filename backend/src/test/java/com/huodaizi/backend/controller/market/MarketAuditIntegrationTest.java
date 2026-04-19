package com.huodaizi.backend.controller.market;

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
class MarketAuditIntegrationTest {

  @Autowired private MockMvc mockMvc;

  @Test
  void shouldReturnOnlyThirtyDayPanelWhenRangeIsThirtyDay() throws Exception {
    mockMvc
        .perform(get("/api/v1/market/overview").param("range", "30日"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value("0"))
        .andExpect(jsonPath("$.data.quotePanels.length()").value(1))
        .andExpect(jsonPath("$.data.quotePanels[0].label").value("近30日区间"));
  }

  @Test
  void shouldReturnInsightContentInsteadOfStaticTitle() throws Exception {
    mockMvc
        .perform(get("/api/v1/market/overview"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value("0"))
        .andExpect(jsonPath("$.data.insights[0]").value("北方建材成交回暖，螺纹现货价格小幅走强。"));
  }

  @Test
  void shouldRejectAdminMarketRequestWithoutToken() throws Exception {
    mockMvc.perform(get("/api/admin/market/SNAPSHOT")).andExpect(status().isUnauthorized());
  }

  @Test
  void shouldAllowAdminMarketRequestWithToken() throws Exception {
    mockMvc
        .perform(get("/api/admin/market/SNAPSHOT").header("X-Admin-Token", "change-this-admin-token"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value("0"));
  }

  @Test
  void shouldReturnBadRequestStatusForInvalidStatusValue() throws Exception {
    mockMvc
        .perform(
            put("/api/admin/market/SNAPSHOT/MK20260418001/status")
                .header("X-Admin-Token", "change-this-admin-token")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"status\":\"INVALID\"}"))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.code").value("BAD_REQUEST"));
  }
}
