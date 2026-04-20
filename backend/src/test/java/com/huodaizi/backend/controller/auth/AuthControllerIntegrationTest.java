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
class AuthControllerIntegrationTest {

  @Autowired private MockMvc mockMvc;

  @Test
  void registerLoginSessionLogoutShouldWork() throws Exception {
    String registerResp =
        mockMvc
            .perform(
                post("/api/v1/auth/register")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(
                        """
                        {
                          "accountType":"MOBILE",
                          "mobile":"13911112222",
                          "password":"Pass@1234",
                          "confirmPassword":"Pass@1234",
                          "smsCode":"123456",
                          "companyName":"N01测试钢贸",
                          "contactName":"张经理",
                          "operator":"n01-test"
                        }
                        """))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value("0"))
            .andExpect(jsonPath("$.data.account").value("13911112222"))
            .andReturn()
            .getResponse()
            .getContentAsString();

    com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
    java.util.Map<?, ?> body = mapper.readValue(registerResp, java.util.Map.class);
    java.util.Map<?, ?> data = (java.util.Map<?, ?>) body.get("data");
    String registerToken = String.valueOf(data.get("token"));

    mockMvc
        .perform(
            get("/api/v1/auth/session")
                .header("X-Auth-Token", registerToken))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value("0"))
        .andExpect(jsonPath("$.data.account").value("13911112222"));

    String loginResp =
        mockMvc
            .perform(
                post("/api/v1/auth/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(
                        """
                        {
                          "account":"13911112222",
                          "password":"Pass@1234",
                          "contactMobile":"13911112222"
                        }
                        """))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value("0"))
            .andExpect(jsonPath("$.data.account").value("13911112222"))
            .andReturn()
            .getResponse()
            .getContentAsString();

    java.util.Map<?, ?> loginBody = mapper.readValue(loginResp, java.util.Map.class);
    java.util.Map<?, ?> loginData = (java.util.Map<?, ?>) loginBody.get("data");
    String loginToken = String.valueOf(loginData.get("token"));

    mockMvc
        .perform(post("/api/v1/auth/logout").header("X-Auth-Token", loginToken))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value("0"));

    mockMvc
        .perform(get("/api/v1/auth/session").header("X-Auth-Token", loginToken))
        .andExpect(status().isUnauthorized())
        .andExpect(jsonPath("$.code").value("UNAUTHORIZED"));
  }

  @Test
  void loginShouldRejectWrongPassword() throws Exception {
    mockMvc
        .perform(
            post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                    {
                      "account":"13800138000",
                      "password":"WrongPass",
                      "contactMobile":"13800138000"
                    }
                    """))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.code").value("BAD_REQUEST"));
  }
}
