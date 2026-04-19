package com.huodaizi.backend.controller.warehousedetail;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
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
class WarehouseDetailAuditIntegrationTest {

  @Autowired private MockMvc mockMvc;

  @Autowired private ObjectMapper objectMapper;

  @Test
  void publicDetailShouldReturnMaskedContactAndCorrectRelatedWarehouseTypeQuote() throws Exception {
    MvcResult result =
        mockMvc
            .perform(get("/api/v1/warehouse-detail").param("id", "W20260418001"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value("0"))
            .andExpect(jsonPath("$.data.detail.id").value("W20260418001"))
            .andExpect(jsonPath("$.data.contact.contactNameMasked").value("张**"))
            .andExpect(jsonPath("$.data.contact.phoneMasked").value("139****4832"))
            .andReturn();

    String body = result.getResponse().getContentAsString();
    Map<?, ?> payload = objectMapper.readValue(body, Map.class);
    Map<?, ?> data = (Map<?, ?>) payload.get("data");
    Map<?, ?> firstRelatedWarehouse = (Map<?, ?>) ((java.util.List<?>) data.get("relatedWarehouses")).get(0);
    assertThat(firstRelatedWarehouse.get("type")).isNotEqualTo(firstRelatedWarehouse.get("quote"));
  }

  @Test
  void adminEndpointShouldRejectWhenMissingToken() throws Exception {
    mockMvc
        .perform(get("/api/admin/warehouse-detail/W20260418001/MAIN"))
        .andExpect(status().isUnauthorized())
        .andExpect(jsonPath("$.code").value("UNAUTHORIZED"));
  }

  @Test
  void adminEndpointShouldAllowWhenTokenValid() throws Exception {
    mockMvc
        .perform(
            get("/api/admin/warehouse-detail/W20260418001/MAIN")
                .header("X-Admin-Token", "test-admin-token"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value("0"));
  }

  @Test
  void adminStatusShouldReturnBadRequestForInvalidStatus() throws Exception {
    mockMvc
        .perform(
            put("/api/admin/warehouse-detail/W20260418001/MAIN/WD2026041800101/status")
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
            put("/api/admin/warehouse-detail/W20260418001/MAIN/WD2026041800101/pin")
                .header("X-Admin-Token", "test-admin-token")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.code").value("BAD_REQUEST"))
        .andExpect(jsonPath("$.message").value("pinned 不能为空"));
  }
}
