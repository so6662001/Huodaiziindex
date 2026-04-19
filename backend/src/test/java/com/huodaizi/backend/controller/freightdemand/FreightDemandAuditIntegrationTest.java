package com.huodaizi.backend.controller.freightdemand;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(
    properties = {
      "huodaizi.admin.auth.enabled=true",
      "huodaizi.admin.auth.token=test-admin-token"
    })
class FreightDemandAuditIntegrationTest {

  @Autowired private MockMvc mockMvc;
  @Autowired private ObjectMapper objectMapper;

  @Test
  void listShouldApplyTimelinessAndNeedFiltersAndMaskContactFields() throws Exception {
    MvcResult result =
        mockMvc
            .perform(
                get("/api/v1/freight-demand/list")
                    .param("timeliness", "48小时内")
                    .param("invoiceNeed", "需开票")
                    .param("loadingNeed", "仅运输"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value("0"))
            .andExpect(jsonPath("$.data.total").value(1))
            .andReturn();

    Map<?, ?> payload = objectMapper.readValue(result.getResponse().getContentAsString(), Map.class);
    Map<?, ?> data = (Map<?, ?>) payload.get("data");
    List<?> items = (List<?>) data.get("items");
    assertThat(items).hasSize(1);

    Map<?, ?> first = (Map<?, ?>) items.get(0);
    assertThat(String.valueOf(first.get("timeliness"))).isNotBlank();
    assertThat(String.valueOf(first.get("invoiceNeed"))).isNotBlank();
    assertThat(String.valueOf(first.get("loadingNeed"))).isNotBlank();
    String maskedName = String.valueOf(first.get("contactNameMasked"));
    assertThat(maskedName).isNotBlank().endsWith("**");
    assertThat(String.valueOf(first.get("contactPhoneMasked"))).isEqualTo("138****7862");
  }

  @Test
  void adminEndpointShouldRejectWhenMissingToken() throws Exception {
    mockMvc
        .perform(get("/api/admin/freight-demand"))
        .andExpect(status().isUnauthorized())
        .andExpect(jsonPath("$.code").value("UNAUTHORIZED"));
  }

  @Test
  void adminEndpointShouldAllowWhenTokenValid() throws Exception {
    mockMvc
        .perform(get("/api/admin/freight-demand").header("X-Admin-Token", "test-admin-token"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value("0"));
  }

  @Test
  void adminStatusShouldReturnBadRequestForInvalidStatus() throws Exception {
    mockMvc
        .perform(
            put("/api/admin/freight-demand/FDM20260418001/status")
                .header("X-Admin-Token", "test-admin-token")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"status\":\"INVALID\"}"))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.code").value("BAD_REQUEST"));
  }

  @Test
  void adminPinShouldReturnBadRequestWhenPinnedMissing() throws Exception {
    mockMvc
        .perform(
            put("/api/admin/freight-demand/FDM20260418001/pin")
                .header("X-Admin-Token", "test-admin-token")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.code").value("BAD_REQUEST"))
        .andExpect(jsonPath("$.message").value("pinned 不能为空"));
  }
}
