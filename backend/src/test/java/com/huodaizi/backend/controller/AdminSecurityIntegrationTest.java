package com.huodaizi.backend.controller;

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
class AdminSecurityIntegrationTest {

  @Autowired
  private MockMvc mockMvc;

  @Test
  void shouldRejectAdminRequestWhenTokenMissing() throws Exception {
    mockMvc
        .perform(get("/api/admin/config"))
        .andExpect(status().isUnauthorized())
        .andExpect(jsonPath("$.code").value("UNAUTHORIZED"));
  }

  @Test
  void shouldAllowAdminRequestWhenTokenValid() throws Exception {
    mockMvc
        .perform(get("/api/admin/config").header("X-Admin-Token", "change-this-admin-token"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value("0"))
        .andExpect(jsonPath("$.data").exists());
  }
}
