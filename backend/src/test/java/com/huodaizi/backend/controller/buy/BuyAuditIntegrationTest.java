package com.huodaizi.backend.controller.buy;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class BuyAuditIntegrationTest {

  @Autowired
  private MockMvc mockMvc;

  @Test
  void shouldRejectAdminBuyWhenTokenMissing() throws Exception {
    mockMvc
        .perform(get("/api/admin/buy"))
        .andExpect(status().isUnauthorized())
        .andExpect(jsonPath("$.code").value("UNAUTHORIZED"));
  }

  @Test
  void shouldAllowAdminBuyWhenTokenValid() throws Exception {
    mockMvc
        .perform(get("/api/admin/buy").header("X-Admin-Token", "change-this-admin-token"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value("0"))
        .andExpect(jsonPath("$.data").isArray());
  }

  @Test
  void shouldReturnMaskedPhoneInBuyDetail() throws Exception {
    mockMvc
        .perform(get("/api/v1/buy/B20260418001"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data.contactPhone").value("138****1234"));
  }
}
