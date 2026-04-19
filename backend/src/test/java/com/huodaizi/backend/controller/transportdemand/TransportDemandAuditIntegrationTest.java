package com.huodaizi.backend.controller.transportdemand;

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
class TransportDemandAuditIntegrationTest {

  @Autowired private MockMvc mockMvc;

  @Test
  void shouldReturnTransportDemandDetailWithMaskedContacts() throws Exception {
    mockMvc
        .perform(get("/api/v1/transport-demand/TD20260418001"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value("0"))
        .andExpect(jsonPath("$.data.contactNameMasked").value("王**"))
        .andExpect(jsonPath("$.data.contactPhoneMasked").value("137****7834"));
  }

  @Test
  void shouldSupportFilterByInvoiceNeed() throws Exception {
    mockMvc
        .perform(
            get("/api/v1/transport-demand")
                .param("invoiceNeed", "需要开票")
                .param("page", "1")
                .param("pageSize", "10"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value("0"))
        .andExpect(jsonPath("$.data.items[0].invoiceNeed").value("需要开票"));
  }

  @Test
  void shouldRejectAdminTransportDemandRequestWithoutToken() throws Exception {
    mockMvc.perform(get("/api/admin/transport-demand")).andExpect(status().isUnauthorized());
  }

  @Test
  void shouldAllowAdminTransportDemandRequestWithToken() throws Exception {
    mockMvc
        .perform(get("/api/admin/transport-demand").header("X-Admin-Token", "change-this-admin-token"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value("0"));
  }

  @Test
  void shouldReturnBadRequestForInvalidStatus() throws Exception {
    mockMvc
        .perform(
            put("/api/admin/transport-demand/TD20260418001/status")
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
            put("/api/admin/transport-demand/TD20260418001/pin")
                .header("X-Admin-Token", "change-this-admin-token")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"status\":\"ONLINE\"}"))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.code").value("BAD_REQUEST"));
  }
}
