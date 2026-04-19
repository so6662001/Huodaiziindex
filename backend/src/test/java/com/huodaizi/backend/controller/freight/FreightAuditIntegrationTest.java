package com.huodaizi.backend.controller.freight;

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
class FreightAuditIntegrationTest {

  @Autowired private MockMvc mockMvc;

  @Test
  void shouldReturnFreightDetailWithMaskedPhoneAndTimeliness() throws Exception {
    mockMvc
        .perform(get("/api/v1/freight/F20260418001"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value("0"))
        .andExpect(jsonPath("$.data.id").value("F20260418001"))
        .andExpect(jsonPath("$.data.contactPhone").value("138****1234"))
        .andExpect(jsonPath("$.data.timeliness").value("48小时"));
  }

  @Test
  void shouldSupportFreightListFiltering() throws Exception {
    mockMvc
        .perform(
            get("/api/v1/freight")
                .param("origin", "唐山")
                .param("destination", "无锡")
                .param("vehicleType", "13米平板")
                .param("page", "1")
                .param("pageSize", "5"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value("0"))
        .andExpect(jsonPath("$.data.total").value(1))
        .andExpect(jsonPath("$.data.items[0].id").value("F20260418001"));
  }

  @Test
  void shouldRejectAdminFreightRequestWithoutToken() throws Exception {
    mockMvc.perform(get("/api/admin/freight")).andExpect(status().isUnauthorized());
  }

  @Test
  void shouldAllowAdminFreightRequestWithToken() throws Exception {
    mockMvc
        .perform(get("/api/admin/freight").header("X-Admin-Token", "change-this-admin-token"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value("0"));
  }

  @Test
  void shouldReturnBadRequestForInvalidStatus() throws Exception {
    mockMvc
        .perform(
            put("/api/admin/freight/F20260418001/status")
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
            put("/api/admin/freight/F20260418001/pin")
                .header("X-Admin-Token", "change-this-admin-token")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"status\":\"ONLINE\"}"))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.code").value("BAD_REQUEST"));
  }
}
