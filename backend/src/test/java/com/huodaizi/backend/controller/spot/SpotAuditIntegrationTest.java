package com.huodaizi.backend.controller.spot;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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
class SpotAuditIntegrationTest {

  @Autowired
  private MockMvc mockMvc;

  @Test
  void shouldRejectAdminSpotListWhenTokenMissing() throws Exception {
    mockMvc
        .perform(get("/api/admin/spot"))
        .andExpect(status().isUnauthorized())
        .andExpect(jsonPath("$.code").value("UNAUTHORIZED"));
  }

  @Test
  void shouldAllowAdminSpotListWhenTokenProvided() throws Exception {
    mockMvc
        .perform(get("/api/admin/spot").header("X-Admin-Token", "change-this-admin-token"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value("0"))
        .andExpect(jsonPath("$.data").isArray());
  }

  @Test
  void shouldCreateSpotByPublicPublishApi() throws Exception {
    String payload =
        """
        {
          "title": "测试现货 Q235B 3.0*1250",
          "category": "热卷",
          "spec": "Q235B 3.0*1250",
          "seller": "测试钢贸",
          "city": "无锡",
          "price": "3800元/吨",
          "tonnage": "100吨",
          "delivery": "现货即提",
          "contactName": "张三",
          "contactPhone": "13800138000"
        }
        """;
    mockMvc
        .perform(post("/api/v1/spot").contentType(MediaType.APPLICATION_JSON).content(payload))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value("0"))
        .andExpect(jsonPath("$.data.id").exists())
        .andExpect(jsonPath("$.data.status").value("ONLINE"));
  }
}
