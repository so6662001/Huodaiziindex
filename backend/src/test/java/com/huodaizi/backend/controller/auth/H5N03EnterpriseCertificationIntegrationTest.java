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
class H5N03EnterpriseCertificationIntegrationTest {

  @Autowired private MockMvc mockMvc;

  @Test
  void h5SubmitAndDetailShouldWork() throws Exception {
    String mobile = "139" + String.format("%08d", Math.floorMod(System.nanoTime(), 100000000L));
    mockMvc
        .perform(
            post("/api/v1/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                    {
                      "accountType":"MOBILE",
                      "mobile":"%s",
                      "password":"Pass@1234",
                      "confirmPassword":"Pass@1234",
                      "smsCode":"123456",
                      "companyName":"H5-N03测试企业",
                      "contactName":"测试联系人",
                      "operator":"h5-n03-test"
                    }
                    """
                        .formatted(mobile)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value("0"));

    mockMvc
        .perform(
            post("/api/v1/auth/h5/send-login-code")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                    {
                      "mobile":"%s",
                      "operator":"h5-n03-test"
                    }
                    """
                        .formatted(mobile)))
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
                          "mobile":"%s",
                          "smsCode":"123456",
                          "channel":"H5",
                          "operator":"h5-n03-test"
                        }
                        """
                            .formatted(mobile)))
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
        .perform(get("/api/v1/auth/h5/enterprise-certification/detail").header("X-Auth-Token", token))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value("0"))
        .andExpect(jsonPath("$.data.status").value("UNSUBMITTED"))
        .andExpect(jsonPath("$.data.statusText").value("待提交"))
        .andExpect(jsonPath("$.data.channel").value("H5"))
        .andExpect(jsonPath("$.data.canResubmit").value(true));

    mockMvc
        .perform(
            post("/api/v1/auth/h5/enterprise-certification/submit")
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
                      "businessLicenseUrl":"https://cdn.huodaizi.com/n03/license.jpg",
                      "legalIdFrontUrl":"https://cdn.huodaizi.com/n03/id-front.jpg",
                      "legalIdBackUrl":"https://cdn.huodaizi.com/n03/id-back.jpg",
                      "bankAccountName":"唐山弘达钢贸有限公司",
                      "bankAccountNo":"6222021234567890123",
                      "bankName":"中国工商银行唐山分行",
                      "province":"河北省",
                      "city":"唐山市",
                      "address":"路北区建设路100号",
                      "remark":"H5-N03自动化测试提交",
                      "operator":"h5-n03-test"
                    }
                    """))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value("0"))
        .andExpect(jsonPath("$.data.companyName").value("唐山弘达钢贸有限公司"))
        .andExpect(jsonPath("$.data.status").value("PENDING_REVIEW"))
        .andExpect(jsonPath("$.data.statusText").value("审核中"))
        .andExpect(jsonPath("$.data.channel").value("H5"))
        .andExpect(jsonPath("$.data.legalPersonIdNoMasked").value("1301********1234"))
        .andExpect(jsonPath("$.data.bankAccountNoMasked").value("6222********0123"));
  }

  @Test
  void h5CertificationShouldRejectWhenMissingToken() throws Exception {
    mockMvc
        .perform(get("/api/v1/auth/h5/enterprise-certification/detail"))
        .andExpect(status().isUnauthorized())
        .andExpect(jsonPath("$.code").value("UNAUTHORIZED"));
  }
}
