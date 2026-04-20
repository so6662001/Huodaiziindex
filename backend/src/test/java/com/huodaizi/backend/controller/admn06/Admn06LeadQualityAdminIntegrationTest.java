package com.huodaizi.backend.controller.admn06;

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
class Admn06LeadQualityAdminIntegrationTest {

  @Autowired private MockMvc mockMvc;

  @Test
  void listShouldRejectWhenMissingToken() throws Exception {
    mockMvc
        .perform(get("/api/admin/lead-quality"))
        .andExpect(status().isUnauthorized())
        .andExpect(jsonPath("$.code").value("UNAUTHORIZED"));
  }

  @Test
  void listDetailAndReviewShouldWork() throws Exception {
    String listResp =
        mockMvc
            .perform(
                get("/api/admin/lead-quality")
                    .header("X-Admin-Token", "test-admin-token")
                    .param("source", "INQUIRY")
                    .param("page", "1")
                    .param("pageSize", "20"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value("0"))
            .andExpect(jsonPath("$.data.records").isArray())
            .andExpect(jsonPath("$.data.pendingCount").isNumber())
            .andReturn()
            .getResponse()
            .getContentAsString();

    com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
    java.util.Map<?, ?> listBody = mapper.readValue(listResp, java.util.Map.class);
    java.util.Map<?, ?> listData = (java.util.Map<?, ?>) listBody.get("data");
    java.util.List<?> records = (java.util.List<?>) listData.get("records");
    String qualityId = String.valueOf(((java.util.Map<?, ?>) records.get(0)).get("qualityId"));

    mockMvc
        .perform(get("/api/admin/lead-quality/{qualityId}", qualityId).header("X-Admin-Token", "test-admin-token"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value("0"))
        .andExpect(jsonPath("$.data.qualityId").value(qualityId))
        .andExpect(jsonPath("$.data.availableActions").isArray());

    mockMvc
        .perform(
            put("/api/admin/lead-quality/{qualityId}/review", qualityId)
                .header("X-Admin-Token", "test-admin-token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                    {
                      "qualityStatus":"RECHECK",
                      "riskLevel":"MEDIUM",
                      "qualityScore":78,
                      "ruleCode":"FOLLOW_DELAY",
                      "reviewRemark":"线索需要补充回访时间与预算字段",
                      "reviewer":"admn06-reviewer"
                    }
                    """))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value("0"))
        .andExpect(jsonPath("$.data.qualityId").value(qualityId))
        .andExpect(jsonPath("$.data.qualityStatus").value("RECHECK"))
        .andExpect(jsonPath("$.data.riskLevel").value("MEDIUM"))
        .andExpect(jsonPath("$.data.reviewComment").value("线索需要补充回访时间与预算字段"));

    mockMvc
        .perform(
            get("/api/admin/lead-quality")
                .header("X-Admin-Token", "test-admin-token")
                .param("qualityStatus", "RECHECK")
                .param("keyword", "FOLLOW_DELAY")
                .param("page", "1")
                .param("pageSize", "20"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value("0"))
        .andExpect(jsonPath("$.data.records.length()").value(Matchers.greaterThanOrEqualTo(1)));
  }
}
