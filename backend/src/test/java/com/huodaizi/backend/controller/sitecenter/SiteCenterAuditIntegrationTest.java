package com.huodaizi.backend.controller.sitecenter;

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
class SiteCenterAuditIntegrationTest {

  @Autowired private MockMvc mockMvc;
  @Autowired private ObjectMapper objectMapper;

  @Test
  void overviewShouldReturnStableRegionOrderAndCityData() throws Exception {
    MvcResult result =
        mockMvc
            .perform(get("/api/v1/site-center/overview"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value("0"))
            .andReturn();

    Map<?, ?> payload = objectMapper.readValue(result.getResponse().getContentAsString(), Map.class);
    Map<?, ?> data = (Map<?, ?>) payload.get("data");
    List<?> groups = (List<?>) data.get("regionGroups");
    assertThat(groups).isNotEmpty();

    Map<?, ?> firstRegion = (Map<?, ?>) groups.get(0);
    assertThat(String.valueOf(firstRegion.get("region"))).isNotBlank();

    List<?> cities = (List<?>) firstRegion.get("cities");
    assertThat(cities).isNotEmpty();
    Map<?, ?> firstCity = (Map<?, ?>) cities.get(0);
    assertThat(String.valueOf(firstCity.get("name"))).isNotBlank();
    assertThat(String.valueOf(firstCity.get("slug"))).isNotBlank();
  }

  @Test
  void adminEndpointShouldRejectWhenMissingToken() throws Exception {
    mockMvc
        .perform(get("/api/admin/site-center/CITY"))
        .andExpect(status().isUnauthorized())
        .andExpect(jsonPath("$.code").value("UNAUTHORIZED"));
  }

  @Test
  void adminEndpointShouldAllowWhenTokenValid() throws Exception {
    mockMvc
        .perform(get("/api/admin/site-center/CITY").header("X-Admin-Token", "test-admin-token"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value("0"));
  }

  @Test
  void adminStatusShouldReturnBadRequestForInvalidStatus() throws Exception {
    mockMvc
        .perform(
            put("/api/admin/site-center/CITY/SC20260418005/status")
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
            put("/api/admin/site-center/CITY/SC20260418005/pin")
                .header("X-Admin-Token", "test-admin-token")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.code").value("BAD_REQUEST"))
        .andExpect(jsonPath("$.message").value("pinned 不能为空"));
  }
}
