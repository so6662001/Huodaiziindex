package com.huodaizi.backend.controller.logistics;

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
class LogisticsAuditIntegrationTest {

  @Autowired private MockMvc mockMvc;

  @Test
  void shouldReturnLogisticsOverview() throws Exception {
    mockMvc
        .perform(get("/api/v1/logistics/overview"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value("0"))
        .andExpect(jsonPath("$.data.quickEntries.length()").value(4))
        .andExpect(jsonPath("$.data.warehouses.length()").value(3))
        .andExpect(jsonPath("$.data.freights.length()").value(3))
        .andExpect(jsonPath("$.data.storageDemands.length()").value(3))
        .andExpect(jsonPath("$.data.transportDemands.length()").value(3))
        .andExpect(jsonPath("$.data.stations.length()").value(6))
        .andExpect(jsonPath("$.data.adCard.id").exists());
  }

  @Test
  void shouldReturnSearchResultsWithTypeAndKeywordFilters() throws Exception {
    mockMvc
        .perform(
            get("/api/v1/logistics/search")
                .param("type", "WAREHOUSE")
                .param("keyword", "唐山"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value("0"))
        .andExpect(jsonPath("$.data.total").value(1))
        .andExpect(jsonPath("$.data.items.length()").value(1))
        .andExpect(jsonPath("$.data.items[0].type").value("WAREHOUSE"))
        .andExpect(jsonPath("$.data.items[0].city").value("唐山"))
        .andExpect(jsonPath("$.data.items[0].link").value("/logistics/warehouse/1"));
  }

  @Test
  void shouldRejectAdminRequestWithoutToken() throws Exception {
    mockMvc.perform(get("/api/admin/logistics/WAREHOUSE")).andExpect(status().isUnauthorized());
  }

  @Test
  void shouldAllowAdminRequestWithToken() throws Exception {
    mockMvc
        .perform(
            get("/api/admin/logistics/WAREHOUSE")
                .header("X-Admin-Token", "change-this-admin-token"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value("0"))
        .andExpect(jsonPath("$.data.length()").value(3));
  }

  @Test
  void shouldReturnBadRequestForInvalidStatus() throws Exception {
    mockMvc
        .perform(
            put("/api/admin/logistics/WAREHOUSE/WH2026041801/status")
                .header("X-Admin-Token", "change-this-admin-token")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"status\":\"INVALID\"}"))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.code").value("BAD_REQUEST"));
  }

  @Test
  void shouldReturnBadRequestWhenPinnedMissing() throws Exception {
    mockMvc
        .perform(
            put("/api/admin/logistics/WAREHOUSE/WH2026041801/pin")
                .header("X-Admin-Token", "change-this-admin-token")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"status\":\"ONLINE\"}"))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.code").value("BAD_REQUEST"));
  }
}
