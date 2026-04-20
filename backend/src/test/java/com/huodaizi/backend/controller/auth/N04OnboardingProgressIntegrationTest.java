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
class N04OnboardingProgressIntegrationTest {

  @Autowired private MockMvc mockMvc;

  @Test
  void progressShouldReturnUnsubmittedWhenNoCertification() throws Exception {
    String registerResp =
        mockMvc
            .perform(
                post("/api/v1/auth/register")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(
                        """
                        {
                          "accountType":"MOBILE",
                          "mobile":"13999990011",
                          "password":"Pass@1234",
                          "confirmPassword":"Pass@1234",
                          "smsCode":"123456",
                          "companyName":"N04空态测试公司",
                          "contactName":"赵六",
                          "operator":"n04-empty"
                        }
                        """))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();

    com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
    java.util.Map<?, ?> body = mapper.readValue(registerResp, java.util.Map.class);
    java.util.Map<?, ?> data = (java.util.Map<?, ?>) body.get("data");
    String token = String.valueOf(data.get("token"));

    mockMvc
        .perform(get("/api/v1/auth/onboarding/progress").header("X-Auth-Token", token))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value("0"))
        .andExpect(jsonPath("$.data.status").value("UNSUBMITTED"))
        .andExpect(jsonPath("$.data.progressPercent").value(10))
        .andExpect(jsonPath("$.data.nodes[0].status").value("PENDING"))
        .andExpect(jsonPath("$.data.nodes[1].status").value("PENDING"));
  }

  @Test
  void progressShouldReturnPendingReviewAfterCertificationSubmit() throws Exception {
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
            .andReturn()
            .getResponse()
            .getContentAsString();

    com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
    java.util.Map<?, ?> body = mapper.readValue(loginResp, java.util.Map.class);
    java.util.Map<?, ?> data = (java.util.Map<?, ?>) body.get("data");
    String token = String.valueOf(data.get("token"));

    mockMvc
        .perform(
            post("/api/v1/auth/enterprise-certification/submit")
                .header("X-Auth-Token", token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                    {
                      "companyName":"唐山弘达钢贸有限公司",
                      "unifiedSocialCreditCode":"91130200MA0ABCDE1X",
                      "legalPersonName":"张三",
                      "legalPersonIdNo":"130123199001011234",
                      "contactName":"李经理",
                      "contactMobile":"13900001111",
                      "businessLicenseUrl":"https://cdn.huodaizi.com/n04/license.jpg",
                      "legalIdFrontUrl":"https://cdn.huodaizi.com/n04/id-front.jpg",
                      "legalIdBackUrl":"https://cdn.huodaizi.com/n04/id-back.jpg",
                      "bankAccountName":"唐山弘达钢贸有限公司",
                      "bankAccountNo":"6222021234567890123",
                      "bankName":"中国工商银行唐山分行",
                      "province":"河北省",
                      "city":"唐山市",
                      "address":"路北区建设路100号",
                      "remark":"N04自动化测试提交",
                      "operator":"n04-submit"
                    }
                    """))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value("0"));

    mockMvc
        .perform(get("/api/v1/auth/onboarding/progress").header("X-Auth-Token", token))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value("0"))
        .andExpect(jsonPath("$.data.status").value("PENDING_REVIEW"))
        .andExpect(jsonPath("$.data.progressPercent").value(55))
        .andExpect(jsonPath("$.data.currentStepCode").value("REVIEW"))
        .andExpect(jsonPath("$.data.nodes[1].status").value("IN_PROGRESS"))
        .andExpect(jsonPath("$.data.nodes[2].status").value("PENDING"));
  }
}
