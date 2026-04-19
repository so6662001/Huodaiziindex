package com.huodaizi.backend.controller.news;

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
class NewsAuditIntegrationTest {

  @Autowired private MockMvc mockMvc;

  @Test
  void shouldRejectAdminNewsRequestWithoutToken() throws Exception {
    mockMvc.perform(get("/api/admin/news/NEWS")).andExpect(status().isUnauthorized());
  }

  @Test
  void shouldAllowAdminNewsRequestWithToken() throws Exception {
    mockMvc
        .perform(get("/api/admin/news/NEWS").header("X-Admin-Token", "change-this-admin-token"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value("0"));
  }

  @Test
  void shouldReturnBadRequestWhenPinValueMissing() throws Exception {
    mockMvc
        .perform(
            put("/api/admin/news/NEWS/NW20260418001/pin")
                .header("X-Admin-Token", "change-this-admin-token")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"status\":\"ONLINE\"}"))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.code").value("BAD_REQUEST"));
  }

  @Test
  void shouldReturnBadRequestStatusForInvalidStatusValue() throws Exception {
    mockMvc
        .perform(
            put("/api/admin/news/NEWS/NW20260418001/status")
                .header("X-Admin-Token", "change-this-admin-token")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"status\":\"INVALID\"}"))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.code").value("BAD_REQUEST"));
  }

  @Test
  void shouldReturnNewsOverviewWithHotAndAd() throws Exception {
    mockMvc
        .perform(get("/api/v1/news/overview").param("page", "1").param("pageSize", "5"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value("0"))
        .andExpect(jsonPath("$.data.items.length()").value(4))
        .andExpect(jsonPath("$.data.hotItems.length()").value(3))
        .andExpect(jsonPath("$.data.ad.id").value("NW20260418008"));
  }
}
