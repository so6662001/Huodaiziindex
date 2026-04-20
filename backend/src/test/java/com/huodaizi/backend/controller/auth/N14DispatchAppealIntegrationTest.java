package com.huodaizi.backend.controller.auth;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class N14DispatchAppealIntegrationTest {

  @Autowired private MockMvc mockMvc;
  @Autowired private ObjectMapper objectMapper;

  @Test
  void dispatchAppealFlowShouldWork() throws Exception {
    String loginResp =
        mockMvc
            .perform(
                post("/api/v1/auth/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(
                        """
                        {
                          "account":"13800138000",
                          "password":"Demo@123456",
                          "contactMobile":"13800138000"
                        }
                        """))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value("0"))
            .andReturn()
            .getResponse()
            .getContentAsString();

    Map<?, ?> loginBody = objectMapper.readValue(loginResp, Map.class);
    Map<?, ?> loginData = (Map<?, ?>) loginBody.get("data");
    String token = String.valueOf(loginData.get("token"));

    String listResp =
        mockMvc
            .perform(
                get("/api/v1/auth/dispatch-appeals")
                    .header("X-Auth-Token", token)
                    .param("pageNo", "1")
                    .param("pageSize", "10"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value("0"))
            .andExpect(jsonPath("$.data.records").isArray())
            .andReturn()
            .getResponse()
            .getContentAsString();

    Map<?, ?> listBody = objectMapper.readValue(listResp, Map.class);
    Map<?, ?> listData = (Map<?, ?>) listBody.get("data");
    List<?> records = (List<?>) listData.get("records");
    Assertions.assertFalse(records.isEmpty(), "N14申诉列表不应为空");

    String createResp =
        mockMvc
            .perform(
                post("/api/v1/auth/dispatch-appeals")
                    .header("X-Auth-Token", token)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(
                        """
                        {
                          "sceneCode":"MERCHANT_LEAD",
                          "targetId":"S001",
                          "appealType":"SCORE_MISMATCH",
                          "appealReason":"我司信用评分与履约指标保持稳定，但分发量下降明显，申请核查评分与分发权重。",
                          "evidenceFiles":"https://cdn.huodaizi.com/appeal/ev-001.png,https://cdn.huodaizi.com/appeal/ev-002.xlsx",
                          "operator":"n14-test"
                        }
                        """))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value("0"))
            .andExpect(jsonPath("$.data.sceneCode").value("MERCHANT_LEAD"))
            .andExpect(jsonPath("$.data.reasonType").value("SCORE_MISMATCH"))
            .andReturn()
            .getResponse()
            .getContentAsString();

    Map<?, ?> createBody = objectMapper.readValue(createResp, Map.class);
    Map<?, ?> createData = (Map<?, ?>) createBody.get("data");
    String appealId = String.valueOf(createData.get("appealId"));

    mockMvc
        .perform(get("/api/v1/auth/dispatch-appeals/{appealId}", appealId).header("X-Auth-Token", token))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value("0"))
        .andExpect(jsonPath("$.data.appealId").value(appealId))
        .andExpect(jsonPath("$.data.status").value("SUBMITTED"))
        .andExpect(jsonPath("$.data.processLogs").isArray());

    mockMvc
        .perform(
            post("/api/v1/auth/dispatch-appeals/{appealId}/status", appealId)
                .header("X-Auth-Token", token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                    {
                      "status":"PROCESSING",
                      "remark":"已转运营策略组核查",
                      "operator":"n14-test"
                    }
                    """))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value("0"))
        .andExpect(jsonPath("$.data.status").value("PROCESSING"))
        .andExpect(jsonPath("$.data.statusText").value("处理中"));
  }
}
