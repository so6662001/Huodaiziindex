package com.huodaizi.backend.controller.admn13;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.hamcrest.Matchers;
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
class Admn13CreditModelVersionAdminIntegrationTest {

  @Autowired private MockMvc mockMvc;

  @Test
  void listShouldRejectWhenMissingToken() throws Exception {
    mockMvc
        .perform(get("/api/admin/credit-model-versions"))
        .andExpect(status().isUnauthorized())
        .andExpect(jsonPath("$.code").value("UNAUTHORIZED"));
  }

  @Test
  void listDetailAndUpsertShouldWork() throws Exception {
    String listResp =
        mockMvc
            .perform(
                get("/api/admin/credit-model-versions")
                    .header("X-Admin-Token", "test-admin-token")
                    .param("versionStatus", "ACTIVE")
                    .param("riskLevel", "LOW")
                    .param("page", "1")
                    .param("pageSize", "20"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value("0"))
            .andExpect(jsonPath("$.data.records").isArray())
            .andExpect(jsonPath("$.data.activeCount").isNumber())
            .andReturn()
            .getResponse()
            .getContentAsString();

    com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
    java.util.Map<?, ?> listBody = mapper.readValue(listResp, java.util.Map.class);
    java.util.Map<?, ?> listData = (java.util.Map<?, ?>) listBody.get("data");
    java.util.List<?> records = (java.util.List<?>) listData.get("records");
    String versionId = String.valueOf(((java.util.Map<?, ?>) records.get(0)).get("modelId"));

    mockMvc
        .perform(
            get("/api/admin/credit-model-versions/{versionId}", versionId)
                .header("X-Admin-Token", "test-admin-token"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value("0"))
        .andExpect(jsonPath("$.data.versionId").value(versionId))
        .andExpect(jsonPath("$.data.factorWeights").isArray())
        .andExpect(jsonPath("$.data.availableActions").isArray());

    String upsertResp =
        mockMvc
            .perform(
                put("/api/admin/credit-model-versions")
                    .header("X-Admin-Token", "test-admin-token")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(
                        """
                        {
                          "modelCode":"CREDIT_MERCHANT_V2",
                          "modelName":"商家信用评分模型V2",
                          "versionNo":"v2026.06",
                          "versionStatus":"ACTIVE",
                          "applicableScope":"MERCHANT",
                          "effectiveFrom":"2026-06-01",
                          "effectiveTo":"2026-12-31",
                          "baseScore":"100",
                          "passThreshold":"78",
                          "riskThreshold":"58",
                          "factors":[
                            {"factorCode":"FULFILLMENT","factorName":"履约稳定性","weightPercent":"32","impactDirection":"POSITIVE"},
                            {"factorCode":"RESPONSE","factorName":"响应时效","weightPercent":"26","impactDirection":"POSITIVE"},
                            {"factorCode":"DISPUTE","factorName":"争议率","weightPercent":"22","impactDirection":"NEGATIVE"},
                            {"factorCode":"PAYMENT","factorName":"回款及时率","weightPercent":"20","impactDirection":"POSITIVE"}
                          ],
                          "remark":"升级模型，强化履约和回款因子权重",
                          "operator":"admn13-admin"
                        }
                        """))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value("0"))
            .andExpect(jsonPath("$.data.versionCode").value("v2026.06"))
            .andExpect(jsonPath("$.data.modelStatus").value("ACTIVE"))
            .andExpect(jsonPath("$.data.modelStatus").value("ACTIVE"))
            .andReturn()
            .getResponse()
            .getContentAsString();

    java.util.Map<?, ?> upsertBody = mapper.readValue(upsertResp, java.util.Map.class);
    java.util.Map<?, ?> upsertData = (java.util.Map<?, ?>) upsertBody.get("data");
    String createdVersionId = String.valueOf(upsertData.get("versionId"));

    mockMvc
        .perform(
            put("/api/admin/credit-model-versions")
                .header("X-Admin-Token", "test-admin-token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                    {
                      "modelCode":"CREDIT_MERCHANT_V2",
                      "modelName":"商家信用评分模型V2",
                      "versionNo":"v2026.06",
                      "versionStatus":"ARCHIVED",
                      "applicableScope":"MERCHANT",
                      "effectiveFrom":"2026-06-01",
                      "effectiveTo":"2026-12-31",
                      "baseScore":"100",
                      "passThreshold":"78",
                      "riskThreshold":"58",
                      "factors":[
                        {"factorCode":"FULFILLMENT","factorName":"履约稳定性","weightPercent":"32","impactDirection":"POSITIVE"},
                        {"factorCode":"RESPONSE","factorName":"响应时效","weightPercent":"26","impactDirection":"POSITIVE"},
                        {"factorCode":"DISPUTE","factorName":"争议率","weightPercent":"22","impactDirection":"NEGATIVE"},
                        {"factorCode":"PAYMENT","factorName":"回款及时率","weightPercent":"20","impactDirection":"POSITIVE"}
                      ],
                      "remark":"版本下线，切换至后续版本",
                      "operator":"admn13-admin"
                    }
                    """))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value("0"))
        .andExpect(jsonPath("$.data.versionId").value(createdVersionId))
        .andExpect(jsonPath("$.data.modelStatus").value("ARCHIVED"))
        .andExpect(jsonPath("$.data.availableActions").isArray());

    mockMvc
        .perform(
            get("/api/admin/credit-model-versions")
                .header("X-Admin-Token", "test-admin-token")
                .param("keyword", "商家信用评分模型V2")
                .param("riskLevel", "LOW")
                .param("page", "1")
                .param("pageSize", "20"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value("0"))
        .andExpect(jsonPath("$.data.records.length()").value(Matchers.greaterThanOrEqualTo(1)))
        .andExpect(jsonPath("$.data.records[0].releaseType").value("MERCHANT"));
  }
}
