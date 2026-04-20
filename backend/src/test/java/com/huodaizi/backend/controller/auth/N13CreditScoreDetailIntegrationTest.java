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
class N13CreditScoreDetailIntegrationTest {

  @Autowired private MockMvc mockMvc;
  @Autowired private ObjectMapper objectMapper;

  @Test
  void creditScoreListAndDetailShouldWork() throws Exception {
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
                get("/api/v1/auth/credit-scores")
                    .header("X-Auth-Token", token)
                    .param("pageNo", "1")
                    .param("pageSize", "10"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value("0"))
            .andExpect(jsonPath("$.data.records").isArray())
            .andExpect(jsonPath("$.data.total").isNumber())
            .andReturn()
            .getResponse()
            .getContentAsString();

    Map<?, ?> listBody = objectMapper.readValue(listResp, Map.class);
    Map<?, ?> listData = (Map<?, ?>) listBody.get("data");
    List<?> records = (List<?>) listData.get("records");
    Assertions.assertFalse(records.isEmpty(), "N13评分列表不应为空");

    Map<?, ?> first = (Map<?, ?>) records.get(0);
    String scoreId = String.valueOf(first.get("scoreId"));
    String merchantId = String.valueOf(first.get("merchantId"));

    mockMvc
        .perform(
            get("/api/v1/auth/credit-scores/{scoreId}", scoreId)
                .header("X-Auth-Token", token))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value("0"))
        .andExpect(jsonPath("$.data.scoreId").value(scoreId))
        .andExpect(jsonPath("$.data.merchantId").value(merchantId))
        .andExpect(jsonPath("$.data.factors").isArray())
        .andExpect(jsonPath("$.data.trend").isArray());
  }
}
