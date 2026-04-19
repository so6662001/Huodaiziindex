package com.huodaizi.backend.controller.warehouse;

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
class WarehouseAuditIntegrationTest {

  @Autowired private MockMvc mockMvc;

  @Test
  void shouldReturnWarehouseDetailWithMaskedPhoneAndCategories() throws Exception {
    mockMvc
        .perform(get("/api/v1/warehouse/W20260418001"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value("0"))
        .andExpect(jsonPath("$.data.contactPhone").value("138****1234"))
        .andExpect(jsonPath("$.data.categories.length()").value(3))
        .andExpect(jsonPath("$.data.categories[0]").value("螺纹钢"));
  }

  @Test
  void shouldFilterWarehouseByCategoryKeywordAndPriceRange() throws Exception {
    mockMvc
        .perform(
            get("/api/v1/warehouse")
                .param("city", "唐山")
                .param("category", "螺纹钢")
                .param("priceRange", "0.8-1.0元/吨/天")
                .param("keyword", "行车")
                .param("page", "1")
                .param("pageSize", "10"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value("0"))
        .andExpect(jsonPath("$.data.total").value(1))
        .andExpect(jsonPath("$.data.items[0].id").value("W20260418001"));
  }

  @Test
  void shouldRejectWarehouseAdminRequestWithoutToken() throws Exception {
    mockMvc.perform(get("/api/admin/warehouse")).andExpect(status().isUnauthorized());
  }

  @Test
  void shouldAllowWarehouseAdminRequestWithToken() throws Exception {
    mockMvc
        .perform(get("/api/admin/warehouse").header("X-Admin-Token", "change-this-admin-token"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value("0"));
  }

  @Test
  void shouldReturnBadRequestForInvalidWarehouseStatus() throws Exception {
    mockMvc
        .perform(
            put("/api/admin/warehouse/W20260418001/status")
                .header("X-Admin-Token", "change-this-admin-token")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"status\":\"INVALID\"}"))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.code").value("BAD_REQUEST"));
  }

  @Test
  void shouldRejectWarehousePinWhenPinnedIsNull() throws Exception {
    mockMvc
        .perform(
            put("/api/admin/warehouse/W20260418001/pin")
                .header("X-Admin-Token", "change-this-admin-token")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"status\":\"ONLINE\"}"))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.code").value("BAD_REQUEST"));
  }
}
