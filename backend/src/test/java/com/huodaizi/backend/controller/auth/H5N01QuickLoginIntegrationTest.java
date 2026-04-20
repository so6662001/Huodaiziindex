package com.huodaizi.backend.controller.auth;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class H5N01QuickLoginIntegrationTest {

  @Autowired private MockMvc mockMvc;
  @Autowired private ObjectMapper objectMapper;

  @Test
  void sendCodeAndQuickLoginShouldWork() throws Exception {
    String sendResp =
        mockMvc
            .perform(
                post("/api/v1/auth/h5/send-login-code")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(
                        """
                        {
                          "mobile":"13800138000",
                          "operator":"h5-n01-test"
                        }
                        """))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value("0"))
            .andExpect(jsonPath("$.data.mobile").value("13800138000"))
            .andExpect(jsonPath("$.data.maskedMobile").value("138****8000"))
            .andExpect(jsonPath("$.data.codeToken").isNotEmpty())
            .andReturn()
            .getResponse()
            .getContentAsString();

    Map<?, ?> sendBody = objectMapper.readValue(sendResp, Map.class);
    Map<?, ?> sendData = (Map<?, ?>) sendBody.get("data");
    String codeToken = String.valueOf(sendData.get("codeToken"));
    org.junit.jupiter.api.Assertions.assertFalse(codeToken.isBlank(), "codeToken 不应为空");

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
                          "operator":"h5-n01-test"
                        }
                        """
                            ))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value("0"))
            .andExpect(jsonPath("$.data.account").value("13800138000"))
            .andExpect(jsonPath("$.data.channel").value("H5"))
            .andReturn()
            .getResponse()
            .getContentAsString();

    Map<?, ?> loginBody = objectMapper.readValue(loginResp, Map.class);
    Map<?, ?> loginData = (Map<?, ?>) loginBody.get("data");
    String token = String.valueOf(loginData.get("token"));

    mockMvc
        .perform(get("/api/v1/auth/session").header("X-Auth-Token", token))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value("0"))
        .andExpect(jsonPath("$.data.account").value("13800138000"));
  }

  @Test
  void quickLoginShouldRejectWrongCode() throws Exception {
    mockMvc
        .perform(
            post("/api/v1/auth/h5/send-login-code")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                    {
                      "mobile":"13800138000",
                      "operator":"h5-n01-test"
                    }
                    """))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value("0"));

    mockMvc
        .perform(
            post("/api/v1/auth/h5/quick-login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                    {
                      "mobile":"13800138000",
                      "smsCode":"000000",
                      "channel":"H5",
                      "operator":"h5-n01-test"
                    }
                    """
                        ))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.code").value("BAD_REQUEST"));
  }
}
