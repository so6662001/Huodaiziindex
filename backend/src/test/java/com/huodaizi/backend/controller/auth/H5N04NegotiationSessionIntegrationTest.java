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
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class H5N04NegotiationSessionIntegrationTest {

  @Autowired private MockMvc mockMvc;

  @Test
  void h5NegotiationListDetailSendAndStatusShouldWork() throws Exception {
    mockMvc
        .perform(
            post("/api/v1/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                    {
                      "accountType":"MOBILE",
                      "mobile":"13999990003",
                      "password":"Pass@1234",
                      "confirmPassword":"Pass@1234",
                      "smsCode":"123456",
                      "companyName":"H5-N04自动化测试公司",
                      "contactName":"赵六",
                      "operator":"h5-n04-test"
                    }
                    """))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value("0"));

    mockMvc
        .perform(
            post("/api/v1/auth/h5/send-login-code")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                    {
                      "mobile":"13999990003",
                      "operator":"h5-n04-test"
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
                          "mobile":"13999990003",
                          "smsCode":"123456",
                          "channel":"H5",
                          "operator":"h5-n04-test"
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
                get("/api/v1/auth/h5/negotiations")
                    .header("X-Auth-Token", token)
                    .param("pageNo", "1")
                    .param("pageSize", "10"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value("0"))
            .andExpect(jsonPath("$.data.channel").value("H5"))
            .andExpect(jsonPath("$.data.records").isArray())
            .andReturn()
            .getResponse()
            .getContentAsString();

    java.util.Map<?, ?> listBody = mapper.readValue(listResp, java.util.Map.class);
    java.util.Map<?, ?> listData = (java.util.Map<?, ?>) listBody.get("data");
    java.util.List<?> sessions = (java.util.List<?>) listData.get("records");
    if (sessions.isEmpty()) {
      return;
    }
    java.util.Map<?, ?> first = (java.util.Map<?, ?>) sessions.get(0);
    String sessionId = String.valueOf(first.get("sessionId"));

    mockMvc
        .perform(get("/api/v1/auth/h5/negotiations/{sessionId}", sessionId).header("X-Auth-Token", token))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value("0"))
        .andExpect(jsonPath("$.data.sessionId").value(sessionId))
        .andExpect(jsonPath("$.data.channel").value("H5"))
        .andExpect(jsonPath("$.data.messages").isArray());

    mockMvc
        .perform(
            post("/api/v1/auth/h5/negotiations/{sessionId}/messages", sessionId)
                .header("X-Auth-Token", token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                    {
                      "senderRole":"BUYER",
                      "messageType":"TEXT",
                      "content":"H5 端确认明早可到港，请给最终价。",
                      "operator":"h5-n04-test"
                    }
                    """))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value("0"))
        .andExpect(jsonPath("$.data.channel").value("H5"))
        .andExpect(jsonPath("$.data.messages[4].content").value("H5 端确认明早可到港，请给最终价。"));

    mockMvc
        .perform(
            post("/api/v1/auth/h5/negotiations/{sessionId}/status", sessionId)
                .header("X-Auth-Token", token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                    {
                      "action":"WAIT_CONFIRM",
                      "remark":"H5端已同步报价，等待双方确认"
                    }
                    """))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value("0"))
        .andExpect(jsonPath("$.data.status").value("WAIT_CONFIRM"))
        .andExpect(jsonPath("$.data.statusText").value("待确认"));
  }

  @Test
  void h5NegotiationListShouldRejectWhenMissingToken() throws Exception {
    mockMvc
        .perform(get("/api/v1/auth/h5/negotiations"))
        .andExpect(status().isUnauthorized())
        .andExpect(jsonPath("$.code").value("UNAUTHORIZED"));
  }
}
