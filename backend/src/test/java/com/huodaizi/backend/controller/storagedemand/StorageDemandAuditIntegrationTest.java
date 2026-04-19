package com.huodaizi.backend.controller.storagedemand;

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
class StorageDemandAuditIntegrationTest {

  @Autowired private MockMvc mockMvc;

  @Test
  void shouldReturnStorageDemandDetailWithMaskedContact() throws Exception {
    mockMvc
        .perform(get("/api/v1/storage-demand/SD20260418001"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value("0"))
        .andExpect(jsonPath("$.data.id").value("SD20260418001"))
        .andExpect(jsonPath("$.data.contactNameMasked").value("张**"))
        .andExpect(jsonPath("$.data.contactPhoneMasked").value("138****1024"));
  }

  @Test
  void shouldFilterStorageDemandByServiceNeed() throws Exception {
    mockMvc
        .perform(
            get("/api/v1/storage-demand")
                .param("serviceNeed", "装卸+分拣")
                .param("page", "1")
                .param("pageSize", "10"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value("0"))
        .andExpect(jsonPath("$.data.total").value(1))
        .andExpect(jsonPath("$.data.items[0].id").value("SD20260418002"));
  }

  @Test
  void shouldRejectAdminStorageDemandRequestWithoutToken() throws Exception {
    mockMvc.perform(get("/api/admin/storage-demand")).andExpect(status().isUnauthorized());
  }

  @Test
  void shouldAllowAdminStorageDemandRequestWithToken() throws Exception {
    mockMvc
        .perform(
            get("/api/admin/storage-demand").header("X-Admin-Token", "change-this-admin-token"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value("0"));
  }

  @Test
  void shouldReturnBadRequestForInvalidStatus() throws Exception {
    mockMvc
        .perform(
            put("/api/admin/storage-demand/SD20260418001/status")
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
            put("/api/admin/storage-demand/SD20260418001/pin")
                .header("X-Admin-Token", "change-this-admin-token")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"status\":\"ONLINE\"}"))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.code").value("BAD_REQUEST"));
  }
}
