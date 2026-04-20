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
class H5N11MessageSettingsIntegrationTest {

  @Autowired private MockMvc mockMvc;

  @Test
  void h5MessageSettingsQueryAndUpdateShouldWork() throws Exception {
    mockMvc
        .perform(
            post("/api/v1/auth/h5/send-login-code")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                    {
                      "mobile":"13800138000",
                      "operator":"h5-n11-test"
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
                          "operator":"h5-n11-test"
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

    mockMvc
        .perform(get("/api/v1/auth/h5/message-settings").header("X-Auth-Token", token))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value("0"))
        .andExpect(jsonPath("$.data.channel").value("H5"))
        .andExpect(jsonPath("$.data.globalPushEnabled").isBoolean())
        .andExpect(jsonPath("$.data.availableActions").isArray());

    mockMvc
        .perform(
            post("/api/v1/auth/h5/message-settings")
                .header("X-Auth-Token", token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                    {
                      "systemNoticeEnabled":true,
                      "orderNoticeEnabled":true,
                      "financeNoticeEnabled":true,
                      "marketingNoticeEnabled":false,
                      "pushEnabled":true,
                      "smsEnabled":false,
                      "emailEnabled":true,
                      "doNotDisturbEnabled":true,
                      "quietStart":"23:00",
                      "quietEnd":"07:30",
                      "extraMutedScenes":["MARKETING"],
                      "operator":"h5-n11-test"
                    }
                    """))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value("0"))
        .andExpect(jsonPath("$.data.doNotDisturbEnabled").value(true))
        .andExpect(jsonPath("$.data.doNotDisturbStart").value("23:00"))
        .andExpect(jsonPath("$.data.doNotDisturbEnd").value("07:30"))
        .andExpect(jsonPath("$.data.marketingEnabled").value(false))
        .andExpect(jsonPath("$.data.smsPushEnabled").value(false));
  }

  @Test
  void h5MessageSettingsApisShouldRejectWhenMissingToken() throws Exception {
    mockMvc
        .perform(get("/api/v1/auth/h5/message-settings"))
        .andExpect(status().isUnauthorized())
        .andExpect(jsonPath("$.code").value("UNAUTHORIZED"));
  }
}
