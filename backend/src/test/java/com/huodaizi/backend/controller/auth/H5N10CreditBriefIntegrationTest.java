package com.huodaizi.backend.controller.auth;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class H5N10CreditBriefIntegrationTest {

  @Autowired private MockMvc mockMvc;

  @Test
  void h5CreditBriefListAndDetailShouldWork() throws Exception {
    mockMvc
        .perform(
            post("/api/v1/auth/h5/send-login-code")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                    {
                      "mobile":"13800138000",
                      "operator":"h5-n10-test"
                    }
                    """))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value("0"));

    String loginResp =
        mockMvc
            .perform(
                post("/api/v1/auth/h5/quick-login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(
                        """
                        {
                          "mobile":"13800138000",
                          "smsCode":"123456",
                          "channel":"H5",
                          "operator":"h5-n10-test"
                        }
                        """))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value("0"))
            .andReturn()
            .getResponse()
            .getContentAsString();

    com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
    java.util.Map<?, ?> body = mapper.readValue(loginResp, java.util.Map.class);
    java.util.Map<?, ?> data = (java.util.Map<?, ?>) body.get("data");
    String token = String.valueOf(data.get("token"));

    String listResp =
        mockMvc
            .perform(
                get("/api/v1/auth/h5/credit-brief/scores")
                    .header("X-Auth-Token", token)
                    .param("pageNo", "1")
                    .param("pageSize", "10"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value("0"))
            .andExpect(jsonPath("$.data.channel").value("H5"))
            .andExpect(jsonPath("$.data.records").isArray())
            .andExpect(jsonPath("$.data.records[0].totalScore").isNotEmpty())
            .andReturn()
            .getResponse()
            .getContentAsString();

    java.util.Map<?, ?> listBody = mapper.readValue(listResp, java.util.Map.class);
    java.util.Map<?, ?> listData = (java.util.Map<?, ?>) listBody.get("data");
    java.util.List<?> records = (java.util.List<?>) listData.get("records");
    java.util.Map<?, ?> first = (java.util.Map<?, ?>) records.get(0);
    String scoreId = String.valueOf(first.get("scoreId"));

    mockMvc
        .perform(
            get("/api/v1/auth/h5/credit-brief/scores/{scoreId}", scoreId)
                .header("X-Auth-Token", token))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value("0"))
        .andExpect(jsonPath("$.data.channel").value("H5"))
        .andExpect(jsonPath("$.data.scoreId").value(scoreId))
        .andExpect(jsonPath("$.data.totalScore").isNotEmpty())
        .andExpect(jsonPath("$.data.availableActions").isArray());
  }

  @Test
  void h5CreditBriefApisShouldRejectWhenMissingToken() throws Exception {
    mockMvc
        .perform(get("/api/v1/auth/h5/credit-brief/scores"))
        .andExpect(status().isUnauthorized())
        .andExpect(jsonPath("$.code").value("UNAUTHORIZED"));
  }
}
