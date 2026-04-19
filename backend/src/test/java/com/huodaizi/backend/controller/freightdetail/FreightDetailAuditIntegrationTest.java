package com.huodaizi.backend.controller.freightdetail;

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
class FreightDetailAuditIntegrationTest {

  @Autowired private MockMvc mockMvc;
  @Autowired private ObjectMapper objectMapper;

  @Test
  void publicDetailShouldReturnMaskedContactAndCorrectMainRelatedData() throws Exception {
    MvcResult result =
        mockMvc
            .perform(get("/api/v1/freight-detail").param("id", "F20260418001"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value("0"))
            .andExpect(jsonPath("$.data.main.id").value("F20260418001"))
            .andExpect(jsonPath("$.data.main.provider").value("唐山宏运车队"))
            .andExpect(jsonPath("$.data.main.route").value("唐山 → 无锡"))
            .andExpect(jsonPath("$.data.main.timeliness").value("48小时"))
            .andExpect(jsonPath("$.data.main.quote").value("120元/吨起"))
            .andExpect(jsonPath("$.data.contact.contactNameMasked").value("王**"))
            .andExpect(jsonPath("$.data.contact.phoneMasked").value("138****7862"))
            .andReturn();

    Map<?, ?> payload = objectMapper.readValue(result.getResponse().getContentAsString(), Map.class);
    Map<?, ?> data = (Map<?, ?>) payload.get("data");
    List<?> relatedLines = (List<?>) data.get("relatedLines");
    assertThat(relatedLines).isNotEmpty();

    Map<?, ?> first = (Map<?, ?>) relatedLines.get(0);
    String route = String.valueOf(first.get("route"));
    String timeliness = String.valueOf(first.get("timeliness"));
    String quote = String.valueOf(first.get("quote"));
    assertThat(route).isNotBlank();
    assertThat(timeliness).isNotBlank();
    assertThat(quote).isNotBlank();
  }

  @Test
  void adminEndpointShouldRejectWhenMissingToken() throws Exception {
    mockMvc
        .perform(get("/api/admin/freight-detail/F20260418001/MAIN"))
        .andExpect(status().isUnauthorized())
        .andExpect(jsonPath("$.code").value("UNAUTHORIZED"));
  }

  @Test
  void adminEndpointShouldAllowWhenTokenValid() throws Exception {
    mockMvc
        .perform(
            get("/api/admin/freight-detail/F20260418001/MAIN")
                .header("X-Admin-Token", "test-admin-token"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value("0"));
  }

  @Test
  void adminStatusShouldReturnBadRequestForInvalidStatus() throws Exception {
    mockMvc
        .perform(
            put("/api/admin/freight-detail/F20260418001/MAIN/FD2026041800101/status")
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
            put("/api/admin/freight-detail/F20260418001/MAIN/FD2026041800101/pin")
                .header("X-Admin-Token", "test-admin-token")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.code").value("BAD_REQUEST"))
        .andExpect(jsonPath("$.message").value("pinned 不能为空"));
  }
}
