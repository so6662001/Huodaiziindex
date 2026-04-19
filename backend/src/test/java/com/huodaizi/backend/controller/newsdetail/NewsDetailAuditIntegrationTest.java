package com.huodaizi.backend.controller.newsdetail;

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
class NewsDetailAuditIntegrationTest {

  @Autowired private MockMvc mockMvc;

  @Test
  void shouldReturnNewsDetailSuccessfully() throws Exception {
    mockMvc
        .perform(get("/api/v1/news-detail").param("id", "N20260418001"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value("0"))
        .andExpect(jsonPath("$.data.article.id").value("N20260418001"))
        .andExpect(jsonPath("$.data.article.content.length()").value(3));
  }

  @Test
  void shouldOrderContentByNumericPublishSequence() throws Exception {
    mockMvc
        .perform(get("/api/v1/news-detail").param("id", "N20260418001"))
        .andExpect(status().isOk())
        .andExpect(
            jsonPath("$.data.article.content[0]")
                .value(
                    "本周华北、华东多个重点城市建材成交较上周明显回升，主要受基建项目开工节奏加快和终端阶段性补库推动。市场询单活跃度提升，低价资源成交占比下降。"));
  }

  @Test
  void shouldRejectAdminRequestWithoutToken() throws Exception {
    mockMvc
        .perform(get("/api/admin/news-detail/N20260418001/ARTICLE"))
        .andExpect(status().isUnauthorized());
  }

  @Test
  void shouldAllowAdminRequestWithToken() throws Exception {
    mockMvc
        .perform(
            get("/api/admin/news-detail/N20260418001/ARTICLE")
                .header("X-Admin-Token", "change-this-admin-token"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value("0"))
        .andExpect(jsonPath("$.data[0].tags").value("螺纹钢,成交,基建"))
        .andExpect(jsonPath("$.data[0].relatedNewsId").value("-"))
        .andExpect(jsonPath("$.data[0].content").value("-"));
  }

  @Test
  void shouldReturnBadRequestForInvalidStatus() throws Exception {
    mockMvc
        .perform(
            put("/api/admin/news-detail/N20260418001/ARTICLE/ND2026041800101/status")
                .header("X-Admin-Token", "change-this-admin-token")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"status\":\"INVALID\"}"))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.code").value("BAD_REQUEST"));
  }

  @Test
  void shouldRejectPinWhenPinnedIsNull() throws Exception {
    mockMvc
        .perform(
            put("/api/admin/news-detail/N20260418001/ARTICLE/ND2026041800101/pin")
                .header("X-Admin-Token", "change-this-admin-token")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"status\":\"ONLINE\"}"))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.code").value("BAD_REQUEST"));
  }
}
