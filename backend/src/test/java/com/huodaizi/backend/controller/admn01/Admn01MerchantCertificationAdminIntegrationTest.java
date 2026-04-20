package com.huodaizi.backend.controller.admn01;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(
    properties = {
      "huodaizi.admin.auth.enabled=true",
      "huodaizi.admin.auth.token=test-admin-token"
    })
class Admn01MerchantCertificationAdminIntegrationTest {

  @Autowired private MockMvc mockMvc;

  @Test
  void listShouldRejectWhenMissingToken() throws Exception {
    mockMvc
        .perform(get("/api/admin/merchant-certifications"))
        .andExpect(status().isUnauthorized())
        .andExpect(jsonPath("$.code").value("UNAUTHORIZED"));
  }

  @Test
  void listDetailAndReviewShouldWork() throws Exception {
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
                      "companyName":"ADMN01测试钢贸有限公司",
                      "unifiedSocialCreditCode":"91130200MA0ABCDE1X",
                      "legalPersonName":"赵总",
                      "legalPersonIdNo":"130123199001011234",
                      "contactName":"王经理",
                      "contactMobile":"13900001111",
                      "businessLicenseUrl":"https://cdn.huodaizi.com/admn01/license.jpg",
                      "legalIdFrontUrl":"https://cdn.huodaizi.com/admn01/id-front.jpg",
                      "legalIdBackUrl":"https://cdn.huodaizi.com/admn01/id-back.jpg",
                      "bankAccountName":"ADMN01测试钢贸有限公司",
                      "bankAccountNo":"6222021234567890123",
                      "bankName":"中国工商银行唐山分行",
                      "province":"河北省",
                      "city":"唐山市",
                      "address":"路北区建设路101号",
                      "remark":"待审核",
                      "operator":"admn01-test"
                    }
                    """))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value("0"))
        .andExpect(jsonPath("$.data.status").value("PENDING_REVIEW"));

    String listResp =
        mockMvc
            .perform(
                get("/api/admin/merchant-certifications")
                    .header("X-Admin-Token", "test-admin-token")
                    .param("status", "PENDING_REVIEW")
                    .param("page", "1")
                    .param("pageSize", "20"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value("0"))
            .andExpect(jsonPath("$.data.records").isArray())
            .andReturn()
            .getResponse()
            .getContentAsString();

    java.util.Map<?, ?> listBody = mapper.readValue(listResp, java.util.Map.class);
    java.util.Map<?, ?> listData = (java.util.Map<?, ?>) listBody.get("data");
    java.util.List<?> records = (java.util.List<?>) listData.get("records");
    java.util.Map<?, ?> first = (java.util.Map<?, ?>) records.get(0);
    String certificationId = String.valueOf(first.get("certificationId"));

    mockMvc
        .perform(
            get("/api/admin/merchant-certifications/{certificationId}", certificationId)
                .header("X-Admin-Token", "test-admin-token"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value("0"))
        .andExpect(jsonPath("$.data.certificationId").value(certificationId))
        .andExpect(jsonPath("$.data.status").value("PENDING_REVIEW"));

    mockMvc
        .perform(
            put("/api/admin/merchant-certifications/{certificationId}/review", certificationId)
                .header("X-Admin-Token", "test-admin-token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                    {
                      "action":"APPROVE",
                      "reviewRemark":"资料完整，审核通过",
                      "reviewer":"admn01-admin"
                    }
                    """))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value("0"))
        .andExpect(jsonPath("$.data.status").value("APPROVED"))
        .andExpect(jsonPath("$.data.latestRemark").value("资料完整，审核通过"));
  }
}
